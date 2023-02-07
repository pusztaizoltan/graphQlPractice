package org.example.entity;

import lombok.Getter;
import org.example.graphQL.annotation.FieldType;
import org.example.graphQL.annotation.FieldOf;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Reader {
    @FieldOf(type = FieldType.SCALAR_INT)
    private final long id;
    @FieldOf(type = FieldType.SCALAR_STRING)
    private final String fullName;
    @FieldOf(type = FieldType.SCALAR_STRING)
    private final String email;
    @FieldOf(type = FieldType.LIST)
    private final List<Book> books = new ArrayList<>();

    public Reader(long id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }

    public void addBook(Book book) {
        this.books.add(book);
    }
}

