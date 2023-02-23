package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.GQLField;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Author {
    @GQLField
    private final long id;
    @GQLField
    private final String name;
    @GQLField
    private final boolean isAlive;
    @GQLField
    private final List<org.example.test_entity.Book> books = new ArrayList<>();

    public Author(long id, String name, boolean isAlive) {
        this.id = id;
        this.name = name;
        this.isAlive = isAlive;
    }

    public void addBook(Book book) {
        this.books.add(book);
    }
}
