package org.example;

import lombok.Getter;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLType;

@Getter
public class Author {
    @GQLField(type = GQLType.SCALAR_INT)
    private final long id;
    @GQLField(type = GQLType.SCALAR_STRING)
    private final String name;
    @GQLField(type = GQLType.SCALAR_BOOLEAN)
    private final boolean isAlive;

    public Author(long id, String name, boolean isAlive) {
        this.id = id;
        this.name = name;
        this.isAlive = isAlive;
    }
}
