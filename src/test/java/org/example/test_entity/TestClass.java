package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.QGLField;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

@Getter
@TypeOf(type = GQLType.OBJECT)
public class TestClass {
    @QGLField(type = GQLType.SCALAR_INT)
    private final int id;
    @QGLField(type = GQLType.SCALAR_STRING)
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
