package org.example;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.example.graphql.SchemaGeneratorImpl;
import org.example.test_db.ListDbTestImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MutationTest {
    public static GraphQL build;

    @BeforeEach
    void initTest() {
        build = new SchemaGeneratorImpl(new ListDbTestImpl()).getGraphQL();
    }

    @Test
    void addNewReader_ShouldIncreaseReaderCount() {
        ExecutionResult mutation = build.execute("mutation {newReader(readerDTO: {id: 200, fullName:\"fullName_0\", email:\"email_0\"})}");
        ExecutionResult query = build.execute("{allReader {id, fullName, email}}");
        mutation.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, mutation.getErrors().size()),
                () -> assertEquals(21, ((List<?>) (((Map<?, ?>) query.getData()).get("allReader"))).size()));
    }

    @Test
    void addNewReader_ShouldReturnWhenQueriedById() {
        ExecutionResult mutation = build.execute("mutation {newReader(readerDTO: {id: 200, fullName:\"fullName_0\", email:\"email_0\"})}");
        mutation.getErrors().forEach(System.out::println);
        ExecutionResult query = build.execute("{readerById(id: 200) {id}}");
        query.getErrors().forEach(System.out::println);
        int idFromQuery = (Integer) ((Map<?, ?>) (((Map<?, ?>) query.getData()).get("readerById"))).get("id");
        assertAll(
                () -> assertEquals(0, mutation.getErrors().size()),
                () -> assertEquals(200, idFromQuery));
    }
}