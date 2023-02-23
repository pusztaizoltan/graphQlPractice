package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.GQLField;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Reader {
    @GQLField
    private final long id;
    @GQLField
    private final String fullName;
    @GQLField
    private final String email;
    @GQLField
    private final List<org.example.test_entity.Book> books = new ArrayList<>();

    public Reader(long id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }

    public void addBook(Book book) {
        this.books.add(book);
    }
}

