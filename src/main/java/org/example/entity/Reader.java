package org.example.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Reader extends Client {
    @Getter
    private final long id;
    @Getter
    private final List<Book> books = new ArrayList<>();

    public Reader(long id, String fullName, String email) {
        super(fullName, email);
        this.id = id;
    }

    public void addBook(Book book) {
        this.books.add(book);
    }
}

