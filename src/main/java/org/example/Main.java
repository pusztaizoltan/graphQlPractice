package org.example;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.entity.Schemable;
import org.example.entity.TestClass;
import org.example.db.ListDb;
import org.example.db.CustomFetcher;
import org.example.graphQL.SchemaLoader;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.UseMarker;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class Main {
    static ListDb db;
    static SchemaLoader source = new SchemaLoader("schema.graphqls");
    static CustomFetcher customFetcher;

    public static void main(String[] args) {
//        Schemable.graphQLObjectTypeFromClass(Book.class);
//        task1();
//        book.experimentMethod();
        initWith(Book.class, Author.class, Reader.class);
    }

    static Set<Class<?>> initWith(Class<?>... classes) {
        HashSet<Class<?>> components = new HashSet<>(Arrays.asList(classes));
        for (Class<?> cls : components) {
            components = getUniqueClasses(cls, components);

        }
        System.out.println(components);
        return components;
    }

    static HashSet<Class<?>> getUniqueClasses(Class<?> cls, HashSet<Class<?>> components) {
        System.out.println(cls);
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            GraphQlIdentifyer category = field.getAnnotation(UseMarker.class).category();
            if (category == GraphQlIdentifyer.TYPE) {
                Class<?> type = field.getType();
                if (components.contains(type)) {
                    break;
                }
                components.add(type);
                components = getUniqueClasses(type, components); // recursive usage
            } else if (category == GraphQlIdentifyer.NESTED_TYPE) {
                Type generic = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                if (components.contains((Class<?>) generic)) {
                    break;
                }
                components.add((Class<?>) generic);
                components = getUniqueClasses((Class<?>) generic, components);
            }
        }
        return components;
    }

    static void task1() {
        db = new ListDb();
        db.initDb();
        customFetcher = new CustomFetcher(db);
        TypeDefinitionRegistry fromFile = source.getSchemaFromFile();
        RuntimeWiring runtimeWiring = getRuntimeWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(fromFile, runtimeWiring);
        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
        System.out.println("-------------------------TESTcLASS BY ID-----------");
        ExecutionResult er1 = build.execute("{testClassById(id: 1){id, content}}");
        er1.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{testClassById(id: 1){id, content}}").getData().toString());
        System.out.println("-------------------------ALL FROM TESTCLASSES----------");
        ExecutionResult er2 = build.execute("{allTestClass {id, content}}");
        er2.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{allTestClass {id, content}}").getData().toString());
        System.out.println("-------------------------BOOK BY STRING GENRE BOOTH IN SCHEMA AND IN OBJECT ----------");
        ExecutionResult er3 = build.execute("{booksByGenreString(genreAsString: \"SCIENCE\") {id, title, author, genreAsString, genreAsEnum}}");
        er3.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{booksByGenreString(genreAsString: \"SCIENCE\") {id, title, author, genreAsString, genreAsEnum}}").getData().toString());
        System.out.println("-------------------------BOOK BY ENUM GENRE BOOTH IN SCHEMA AND IN OBJECT ----------");
        ExecutionResult er4 = build.execute("{booksByGenreEnum(genreAsEnum: SCIENCE) {id, title, author, genreAsString, genreAsEnum}}");
        er4.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{booksByGenreEnum(genreAsEnum: SCIENCE) {id, title, author, genreAsString, genreAsEnum}}").getData().toString());
    }

    static RuntimeWiring getRuntimeWiring() {
        return newRuntimeWiring()
                .type(Schemable.TypeRuntimeWiringFromClass(TestClass.class))
                .type(Schemable.TypeRuntimeWiringFromClass(Book.class))
                .type(Schemable.TypeRuntimeWiringFromClass(Author.class))
                .type(Schemable.TypeRuntimeWiringFromClass(Reader.class))
                .type("Query", builder -> builder.dataFetcher("allTestClass", customFetcher.testClassFetcher)
                                                 .dataFetcher("testClassById", customFetcher.testClassByIdFetcher)
                                                 .dataFetcher("allClients", customFetcher.readerFetcher)
                                                 .dataFetcher("allBooks", customFetcher.bookFetcher)
//                                                 .dataFetcher("booksByGenreString", customFetcher.booksByGenreString)
//                                                 .dataFetcher("booksByGenreEnum", customFetcher.booksByGenreEnum)
                )
                .type("GenreType", builder -> builder.dataFetcher("title", env -> ((Book) env.getSource()).getTitle())
                ).type(Schemable.TypeRuntimeWiringFromClass(Book.class))
                .build();
    }

    static TypeResolver getTypeResolver() {
        return (env) -> {
            GraphQLObjectType result = null;
            Object obj = env.getObject();
            if (obj instanceof TestClass) {
                result = env.getSchema().getObjectType("TestClass");
            } else if (obj instanceof Book) {
                result = env.getSchema().getObjectType("Book");
            } else if (obj instanceof Author) {
                result = env.getSchema().getObjectType("Author");
            } else if (obj instanceof Reader) {
                result = env.getSchema().getObjectType("Reader");
            }
            return result;
        };
    }
}
