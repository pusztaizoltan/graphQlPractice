package org.example.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Author {
    private final long id;
    private final String name;
    private final boolean isAlive;
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
