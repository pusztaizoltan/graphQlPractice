package org.example.entity;

//import graphql.annotations.GraphQLField;
//import graphql.annotations.GraphQLName;
import lombok.Getter;
import lombok.Setter;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.ScalarFitter;
import org.example.graphQL.annotation.UseMarker;

//@GraphQLName("TestClass")
@Getter
public class TestClass {
//    @GraphQLField
    @UseMarker(category = GraphQlIdentifyer.SCALAR, asScalar = ScalarFitter.INT)
    private int id;
//    @GraphQLField
    @UseMarker(category = GraphQlIdentifyer.SCALAR, asScalar = ScalarFitter.STRING)
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
