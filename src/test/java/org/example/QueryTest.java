package org.example;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.example.graphql.SchemaGeneratorImpl;
import org.example.test_db.ListDbTestImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryTest {
    public static GraphQL build;

    @BeforeAll
    static void initTest() {
        ListDbTestImpl listDbTest = new ListDbTestImpl();
        listDbTest.initDb();
        build = new SchemaGeneratorImpl(listDbTest).getGraphQL();
    }

    @Test
    void queryAllTestClass_ShouldReturnAllTenTestObject() {
        ExecutionResult result = build.execute("{allTestClass {id, content}}");
        result.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, result.getErrors().size()),
                () -> assertEquals(10, ((List<?>) (((Map<?, ?>) result.getData()).get("allTestClass"))).size()));
    }

    @Test
    void queryAllReader_ShouldReturnAllTwentyReader() {
        ExecutionResult result = build.execute("{allReader {id, fullName, email}}");
        result.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, result.getErrors().size()),
                () -> assertEquals(20, ((List<?>) (((Map<?, ?>) result.getData()).get("allReader"))).size()));
    }

    @Test
    void queryAllBook_ShouldReturnAllHundredBook() {
        ExecutionResult result = build.execute("{allBook {id, title, genre}}");
        result.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, result.getErrors().size()),
                () -> assertEquals(100, ((List<?>) (((Map<?, ?>) result.getData()).get("allBook"))).size()));
    }

    @Test
    void queryTestClassById_ShouldReturnSpecifiedNumberOfFieldValuesWhenExist() {
        ExecutionResult result = build.execute("{testClassById(id: 1){id, content}}");
        result.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, result.getErrors().size()),
                () -> assertEquals(2, ((Map<?, ?>) ((Map<?, ?>) result.getData()).get("testClassById")).size()));
    }

    @Test
    void queryReaderById_ShouldReturnSpecifiedNumberOfFieldValuesWhenExist() {
        ExecutionResult result = build.execute("{readerById(id: 1) {id, fullName, email}}");
        result.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, result.getErrors().size()),
                () -> assertEquals(3, ((Map<?, ?>) ((Map<?, ?>) result.getData()).get("readerById")).size()));
    }

    @Test
    void queryBookById_ShouldReturnSpecifiedNumberOfFieldValuesWhenExist() {
        ExecutionResult result = build.execute("{bookById(id: 2) {id, title, genre, author{name}}}");
        result.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, result.getErrors().size()),
                () -> assertEquals(4, ((Map<?, ?>) ((Map<?, ?>) result.getData()).get("bookById")).size()));
    }

    @Test
    void queryAuthorByIsAlive_ShouldReturnFiveAuthor() {
        ExecutionResult result = build.execute("{authorByIsAlive(isAlive: true) {name, isAlive}}");
        result.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, result.getErrors().size()),
                () -> assertEquals(5, ((List<?>) ((Map<?, ?>) result.getData()).get("authorByIsAlive")).size()));
    }

    @Test
    void queryByTitleContent_ShouldReturnTenBook() {
        ExecutionResult result = build.execute("{bookByTitleContent (titleContent: \"Title_2\") {id, title}}");
        result.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, result.getErrors().size()),
                () -> assertEquals(11, ((List<?>) ((Map<?, ?>) result.getData()).get("bookByTitleContent")).size()));
    }

    @Test
    void queryBookByGenreEnum_ShouldReturnTwentyFiveBook() {
        ExecutionResult result = build.execute("{bookByGenre(genre: FICTION) {title, genre}}");
        result.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, result.getErrors().size()),
                () -> assertEquals(25, ((List<?>) ((Map<?, ?>) result.getData()).get("bookByGenre")).size()));
    }
}