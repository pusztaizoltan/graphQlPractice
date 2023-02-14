package org.example;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.example.db.ListDbTestImpl;
import org.example.graphql.SchemaGeneratorImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class QueryTest {
    public static GraphQL build = new SchemaGeneratorImpl(new ListDbTestImpl()).getGraphQL();

    @Test
    void queryAllTestClass_ShouldReturnAllTenTestObject() {
        ExecutionResult result = build.execute("{allTestClass {id, content}}");
        result.getErrors().forEach(System.out::println);
        Assertions.assertAll(
                ()->Assertions.assertEquals(0, result.getErrors().size()),
                ()->Assertions.assertEquals(10, ((List<?>)(((Map<?, ?>) result.getData()).get("allTestClass"))).size()));
    }
}