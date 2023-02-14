package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLType;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Reader {
    @GQLField(type = GQLType.SCALAR_INT)
    private final long id;
    @GQLField(type = GQLType.SCALAR_STRING)
    private final String fullName;
    @GQLField(type = GQLType.SCALAR_STRING)
    private final String email;
    @GQLField(type = GQLType.LIST)
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

