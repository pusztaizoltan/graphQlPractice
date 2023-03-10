package org.example.entity;

import lombok.Getter;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.FieldType;

// TODO: is the 'Getter' annotation used anywhere? (and the lombok lib also)
// TODO: also missing Javadocs
@Getter
public class TestClass {
    @FieldOf(type = FieldType.SCALAR_INT)
    private final int id;
    @FieldOf(type = FieldType.SCALAR_STRING)
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
