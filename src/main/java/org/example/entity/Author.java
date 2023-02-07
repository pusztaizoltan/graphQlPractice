package org.example.entity;

import lombok.Getter;
import org.example.graphQL.annotation.FieldType;
import org.example.graphQL.annotation.FieldOf;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Author {
    @FieldOf(type = FieldType.SCALAR_INT)
    private final long id;
    @FieldOf(type = FieldType.SCALAR_STRING)
    private final String name;
    @FieldOf(type = FieldType.SCALAR_BOOLEAN)
    private final boolean isAlive;
    @FieldOf(type = FieldType.LIST)
    private final List<Book> books = new ArrayList<>();

    public Author(long id, String name, boolean isAlive) {
        this.id = id;
        this.name = name;
        this.isAlive = isAlive;
    }

    public void addBook(Book book) {
        this.books.add(book);
    }
}
