package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.QGLField;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

import java.util.ArrayList;
import java.util.List;

@Getter
@TypeOf(type = GQLType.OBJECT)
public class Book {
    @QGLField(type = GQLType.SCALAR_INT)
    private final long id;
    @QGLField(type = GQLType.SCALAR_STRING)
    private final String title;
    @QGLField(type = GQLType.ENUM)
    private final GenreType genre;
    @QGLField(type = GQLType.OBJECT)
    private final Author author;
    @QGLField(type = GQLType.LIST)
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
