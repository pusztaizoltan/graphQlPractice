package org.example;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.entity.TestClass;
import org.example.db.ListDb;
import org.example.db.CustomFetcher;
import org.example.graphQL.SchemaGeneratorImpl;
import org.example.graphQL.SchemaLoader;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

public class Main {
    static ListDb db;
    static SchemaLoader source = new SchemaLoader("schema.graphqls");
    static CustomFetcher customFetcher;

    public static void main(String[] args) {
//        Schemable.graphQLObjectTypeFromClass(Book.class);
//        task1();
//        book.experimentMethod();
        SchemaGeneratorImpl generator = new SchemaGeneratorImpl(TestClass.class);
        GraphQL build = generator.getGraphQL();
//        graphQLEnumTypeFromEnum(GenreType.class);
        System.out.println("-------------------------ALL FROM TESTCLASSES----------");
        ExecutionResult er2 = build.execute("{allTestClass {id, content}}");
//        ExecutionResult er2 = build.execute("{allTestClass}");
        er2.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{allTestClass {id, content}}").getData().toString());
//        System.out.println(build.execute("{allTestClass}").getData().toString());
    }


    static void task1() {
        db = new ListDb();
        db.initDb();
        customFetcher = new CustomFetcher(db);
        SchemaGeneratorImpl schemaGenerator = new SchemaGeneratorImpl(TestClass.class, Author.class);
        GraphQL build = schemaGenerator.getGraphQL();
//        TypeDefinitionRegistry fromFile = source.getSchemaFromFile();
////        RuntimeWiring runtimeWiring = getRuntimeWiring();
//        SchemaGenerator schemaGenerator = new SchemaGenerator();
//        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(fromFile, runtimeWiring);
//        GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
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
