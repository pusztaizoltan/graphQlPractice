package org.example.entity;

import lombok.Getter;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.FieldType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Book {
    @FieldOf(type = FieldType.SCALAR_INT)
    private final long id;
    @FieldOf(type = FieldType.SCALAR_STRING)
    private final String title;
    @FieldOf(type = FieldType.ENUM)
    private final GenreType genreAsEnum;
    @FieldOf(type = FieldType.OBJECT)
    private final Author author;
    @FieldOf(type = FieldType.LIST)
    private final List<Reader> readers = new ArrayList<>();

    public Book(long id, String title, GenreType genreAsEnum, Author author) {
        this.id = id;
        this.title = title;
        this.genreAsEnum = genreAsEnum;
        this.author = author;
    }

    public void addReader(Reader reader) {
        this.readers.add(reader);
    }
}
