package org.example.entity;

import lombok.Getter;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

import java.util.ArrayList;
import java.util.List;

@Getter
@TypeOf(type = GQLType.OBJECT)
public class Reader {
    @FieldOf(type = GQLType.SCALAR_INT)
    private final long id;
    @FieldOf(type = GQLType.SCALAR_STRING)
    private final String fullName;
    @FieldOf(type = GQLType.SCALAR_STRING)
    private final String email;
    @FieldOf(type = GQLType.LIST)
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

