package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Book {
    @GQLField
    private final long id;
    @GQLField
    private final String title;
    @GQLField
    private final GenreType genre;
    @GQLField
    private final Author author;
    @GQLField
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
