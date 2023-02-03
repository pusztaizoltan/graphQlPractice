package org.example.entity;

//import graphql.annotations.GraphQLField;
//import graphql.annotations.GraphQLName;
import lombok.Getter;
import lombok.Setter;
//@GraphQLName("TestClass")
public class TestClass {
    @Getter
    @Setter
//    @GraphQLField
    private int id;
    @Getter
    @Setter
//    @GraphQLField
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
