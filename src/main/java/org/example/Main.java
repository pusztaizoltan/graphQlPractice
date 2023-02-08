package org.example;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.example.db.ListDbTestImpl;
import org.example.graphql.SchemaGeneratorImpl;

public class Main {
    public static void main(String[] args) {
        testTasks();
    }

    static void testTasks() {
        SchemaGeneratorImpl generator = new SchemaGeneratorImpl(new ListDbTestImpl());
        GraphQL build = generator.getGraphQL();
        System.out.println("-------------------------ALL FROM TEST-CLASSES----------");
        ExecutionResult er2 = build.execute("{allTestClass {id, content}}");
        er2.getErrors().forEach(System.out::println);
        System.out.println(build.execute("{allTestClass {id, content}}").getData().toString());
        System.out.println("-------------------------TEST-CLASS BY ID-----------");
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
}
