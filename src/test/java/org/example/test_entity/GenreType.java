package org.example.test_entity;

import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

import java.util.Arrays;

@TypeOf(type = GQLType.ENUM)
public enum GenreType {
    SCIENCE(0),
    ROMANTIC(1),
    FICTION(2),
    FANTASY(3),
    ;
    final long id;

    GenreType(long id) {
        this.id = id;
    }

    public static GenreType getById(long id) {
        return Arrays.stream(GenreType.values())
                     .filter((genre) -> genre.id == id)
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(String.format("invalid genre id:%s", id)));
    }
}
