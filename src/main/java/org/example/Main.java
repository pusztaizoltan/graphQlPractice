package org.example;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import org.example.db.ListDbImpl;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.entity.TestClass;
import org.example.graphQL.SchemaGeneratorImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Main {
    public static void main(String[] args) {
        task();
    }

    static void task() {
        SchemaGeneratorImpl generator = new SchemaGeneratorImpl(new ListDbImpl());
        GraphQL build = generator.getGraphQL();
//        graphQLEnumTypeFromEnum(GenreType.class);
        System.out.println("-------------------------ALL FROM TESTCLASSES----------");
        ExecutionResult er2 = build.execute("{allTestClass {id, content}}");
        er2.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{allTestClass {id, content}}").getData().toString());
        System.out.println("-------------------------TESTcLASS BY ID-----------");
        ExecutionResult er1 = build.execute("{testClassById(id: 1){id, content}}");
        er1.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{testClassById(id: 1){id, content}}").getData().toString());
        System.out.println("-------------------------ALL FROM Readers----------");
        ExecutionResult er3 = build.execute("{allReader {id, fullName, email}}");
        er3.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{allReader {id, fullName, email}}").getData().toString());
        System.out.println("-------------------------Reader BY ID----------");
        ExecutionResult er4 = build.execute("{readerById(id: 1) {id, fullName, email}}");
        er4.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{readerById(id: 1) {id, fullName, email}}").getData().toString());
        System.out.println("-------------------------ALL FROM Books----------");
        ExecutionResult er5 = build.execute("{allBook {id, title, genreAsEnum}}");
        er5.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{allBook {id, title, genreAsEnum}}").getData().toString());
        System.out.println("-------------------------Books BY ID----------");
        ExecutionResult er6 = build.execute("{bookById(id: 2) {id, title, genreAsEnum, author{name,isAlive}}}");
        er6.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{bookById(id: 2) {id, title, genreAsEnum, author{name,isAlive}}}").getData().toString());
    }

    static void methodReflectionExperiments(Object datasource) {
        Method[] methods = datasource.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                if (method.getParameters().length == 1) {
                    System.out.println("" + method.getName());
//                    System.out.println("" + method.getParameters()[0].getAnnotations().length >=1);
////                    System.out.println(method);
//                    var aa = ((Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]).getSimpleName();
//                    System.out.println("" + method.getName() + " " + ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
//                    System.out.println("- " + aa);
//                    System.out.println("- " + method.getReturnType().getSimpleName());
                    //                    System.out.println("- "+method.getModifiers());
//                    System.out.println("- "+(method.getAnnotatedReturnType().getType().getTypeName()));
//                String genericTypeName = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0].getClass().getSimpleName();
                }
            }
        }
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
