package org.example.entity;

import lombok.Getter;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

@Getter
@TypeOf(type = GQLType.OBJECT)
public class TestClass {
    @FieldOf(type = GQLType.SCALAR_INT)
    private final int id;
    @FieldOf(type = GQLType.SCALAR_STRING)
    private final String content;

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
