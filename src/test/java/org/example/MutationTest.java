package org.example;

import graphql.ExecutionResult;
import graphql.GraphQL;
import org.example.graphql.SchemaGeneratorImpl;
import org.example.test_db.ListDbTestImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MutationTest {
    public GraphQL build;

    @BeforeEach
    void initTest() {
        build = new SchemaGeneratorImpl(new ListDbTestImpl()).getGraphQL();
    }

    @Test
    void addNewReaderByInputObjectWithMapperMethod_ShouldIncreaseReaderCount() {
        ExecutionResult mutation = build.execute("mutation {newReaderByInputObject(readerDTO: {id: 200, fullName:\"fullName_0\", email:\"email_0\"})}");
        ExecutionResult query = build.execute("{allReader {id, fullName, email}}");
        mutation.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, mutation.getErrors().size()),
                () -> assertEquals(1, ((List<?>) (((Map<?, ?>) query.getData()).get("allReader"))).size()));
    }

    @Test
    void addNewReaderByInputObjectWithMapperMethod_ShouldReturnWhenQueriedById() {
        ExecutionResult mutation = build.execute("mutation {newReaderByInputObject(readerDTO: {id: 200, fullName:\"fullName_0\", email:\"email_0\"})}");
        mutation.getErrors().forEach(System.out::println);
        ExecutionResult query = build.execute("{readerById(id: 200) {id}}");
        query.getErrors().forEach(System.out::println);
        int idFromQuery = (Integer) ((Map<?, ?>) (((Map<?, ?>) query.getData()).get("readerById"))).get("id");
        assertAll(
                () -> assertEquals(0, mutation.getErrors().size()),
                () -> assertEquals(200, idFromQuery));
    }

    @Test
    void addNewReaderByInputArgumentsWithId_ShouldIncreaseReaderCount(){
        ExecutionResult mutation = build.execute("mutation {newReaderByFieldArgsWithId(id: 200, fullName:\"fullName_0\", email:\"email_0\")}");
        ExecutionResult query = build.execute("{allReader {id, fullName, email}}");
        mutation.getErrors().forEach(System.out::println);
        assertAll(
                () -> assertEquals(0, mutation.getErrors().size()),
                () -> assertEquals(1, ((List<?>) (((Map<?, ?>) query.getData()).get("allReader"))).size()));
    }


    @Test
    void addNewReaderByInputArgumentsWithoutId_ShouldReturnIncrementedId(){
        ExecutionResult mutation = build.execute("mutation {newReaderByFieldArgsWithoutId( fullName:\"fullName_0\", email:\"email_0\")}");
        mutation.getErrors().forEach(System.out::println);
        ExecutionResult query = build.execute("{readerById(id: 0) {id}}");
        query.getErrors().forEach(System.out::println);
        int idFromQuery = (Integer) ((Map<?, ?>) (((Map<?, ?>) query.getData()).get("readerById"))).get("id");
        assertAll(
                () -> assertEquals(0, mutation.getErrors().size()),
                () -> assertEquals(0, idFromQuery));
    }

//    @Test
//    void addNewAuthorByInputObjectWithoutIdWithoutMapperMethod_ShouldIncreaseReaderCount() {
//        ExecutionResult mutation = build.execute("mutation {newAuthorByInputObject(authorDTO: {name:\"name_0\", isAlive:true})}");
//        ExecutionResult query = build.execute("{allAuthor {id, name}}");
//        mutation.getErrors().forEach(System.out::println);
//        assertAll(
//                () -> assertEquals(0, mutation.getErrors().size()),
//                () -> assertEquals(1, ((List<?>) (((Map<?, ?>) query.getData()).get("allAuthor"))).size()));
//    }

//    @Test
//    void addNewAuthorByInputObjectWithoutIdWithoutMapperMethod_ShouldReturnIncrementedId() {
//        ExecutionResult mutation = build.execute("mutation {newReaderByInputObject(readerDTO: {id: 200, fullName:\"fullName_0\", email:\"email_0\"})}");
//        mutation.getErrors().forEach(System.out::println);
//        ExecutionResult query = build.execute("{readerById(id: 200) {id}}");
//        query.getErrors().forEach(System.out::println);
//        int idFromQuery = (Integer) ((Map<?, ?>) (((Map<?, ?>) query.getData()).get("readerById"))).get("id");
//        assertAll(
//                () -> assertEquals(0, mutation.getErrors().size()),
//                () -> assertEquals(200, idFromQuery));
//    }

}
