package org.example.entity;
//import graphql.annotations.GraphQLField;
//import graphql.annotations.GraphQLName;

import lombok.Getter;
import org.example.graphQL.annotation.FieldType;
import org.example.graphQL.annotation.FieldOf;

//@GraphQLName("TestClass")
@Getter
public class TestClass {
    //    @GraphQLField
    @FieldOf(type = FieldType.SCALAR_INT)
    private int id;
    //    @GraphQLField
    @FieldOf(type = FieldType.SCALAR_STRING)
    private String content;

    public TestClass(int id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String toString() {
        return "TestClass{" +
               "id=" + id +
               ", content='" + content + '\'' +
               '}';
    }
}
