package org.example.entity;

import lombok.Getter;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

import java.util.ArrayList;
import java.util.List;

@Getter
@TypeOf(type = GQLType.OBJECT)
public class Book {
    @FieldOf(type = GQLType.SCALAR_INT)
    private final long id;
    @FieldOf(type = GQLType.SCALAR_STRING)
    private final String title;
    @FieldOf(type = GQLType.ENUM)
    private final GenreType genre;
    @FieldOf(type = GQLType.OBJECT)
    private final Author author;
    @FieldOf(type = GQLType.LIST)
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
