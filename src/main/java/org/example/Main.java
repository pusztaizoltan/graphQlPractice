package org.example;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.example.db.ListDbTestImpl;
import org.example.graphql.SchemaGeneratorImpl;

public class Main {
    public static GraphQL build = new SchemaGeneratorImpl(new ListDbTestImpl()).getGraphQL();
    public static void main(String[] args) {
        testQueryTasks();
        testMutationTasks();
    }

    static void testMutationTasks() {
        System.out.println("-------------------------Add newReader----------");
        ExecutionResult er1 = build.execute("{newReader(readerDTO:) {id, content}}");
        er1.getErrors().forEach(System.out::println);
        System.out.println(er1.getData().toString());
        System.out.println();


    }
    static void testQueryTasks() {
        System.out.println("-------------------------ALL FROM TEST-CLASSES----------");
        ExecutionResult er2 = build.execute("{allTestClass {id, content}}");
        er2.getErrors().forEach(System.out::println);
        System.out.println(er2.getData().toString());
        System.out.println();
        System.out.println("-------------------------TEST-CLASS BY ID-----------");
        ExecutionResult er1 = build.execute("{testClassById(id: 1){id, content}}");
        er1.getErrors().forEach(System.out::println);
        System.out.println(er1.getData().toString());
        System.out.println();
        System.out.println("-------------------------ALL FROM Readers----------");
        ExecutionResult er3 = build.execute("{allReader {id, fullName, email}}");
        er3.getErrors().forEach(System.out::println);
        System.out.println(er3.getData().toString());
        System.out.println();
        System.out.println("-------------------------Reader BY ID----------");
        ExecutionResult er4 = build.execute("{readerById(id: 1) {id, fullName, email}}");
        er4.getErrors().forEach(System.out::println);
        System.out.println(er4.getData().toString());
        System.out.println();
        System.out.println("-------------------------ALL FROM Books----------");
        ExecutionResult er5 = build.execute("{allBook {id, title, genre}}");
        er5.getErrors().forEach(System.out::println);
        System.out.println(er5.getData().toString());
        System.out.println();
        System.out.println("-------------------------Books BY ID----------");
        ExecutionResult er6 = build.execute("{bookById(id: 2) {id, title, genre, author{name}}}");
        er6.getErrors().forEach(System.out::println);
        System.out.println(er6.getData().toString());
        System.out.println();
        System.out.println("-------------------------Author BY isAlive----------");
        ExecutionResult er7 = build.execute("{authorByIsAlive(isAlive: true) {name, isAlive}}");
        er7.getErrors().forEach(System.out::println);
        System.out.println(er7.getData().toString());
        System.out.println();
        System.out.println("-------------------------Books BY titleContent----------");
        ExecutionResult er8 = build.execute("{bookByTitleContent (titleContent: \"Title_2\") {id, title}}");
        er8.getErrors().forEach(System.out::println);
        System.out.println(er8.getData().toString());
        System.out.println();
        System.out.println("-------------------------Books BY GenreEnum----------");
        ExecutionResult er9 = build.execute("{bookByGenre(genre: FICTION) {title, genre}}");
        er9.getErrors().forEach(System.out::println);
        System.out.println(er9.getData().toString());
    }
}
