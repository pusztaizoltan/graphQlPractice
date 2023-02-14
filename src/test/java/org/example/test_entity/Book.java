package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Book {
    @GQLField(type = GQLType.SCALAR_INT)
    private final long id;
    @GQLField(type = GQLType.SCALAR_STRING)
    private final String title;
    @GQLField(type = GQLType.ENUM)
    private final GenreType genre;
    @GQLField(type = GQLType.OBJECT)
    private final Author author;
    @GQLField(type = GQLType.LIST)
    private final List<Reader> readers = new ArrayList<>();

    public Book(long id, String title, GenreType genre, Author author) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.author = author;
    }

    public void addReader(Reader reader) {
        this.readers.add(reader);
    }
}
