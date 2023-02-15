package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLType;

// TODO: is the 'Getter' annotation used anywhere? (and the lombok lib also)
// TODO: also missing Javadocs
@Getter
public class TestClass {
    @GQLField(type = GQLType.SCALAR_INT)
    private final int id;
    @GQLField(type = GQLType.SCALAR_STRING)
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
