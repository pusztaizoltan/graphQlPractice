package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.GQLField;

@Getter
public class TestClass {
    @GQLField
    private final int id;
    @GQLField
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
