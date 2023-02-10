package org.example.entity;

import lombok.Getter;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

import java.util.ArrayList;
import java.util.List;

@Getter
@TypeOf(type = GQLType.OBJECT)
public class Author {
    @FieldOf(type = GQLType.SCALAR_INT)
    private final long id;
    @FieldOf(type = GQLType.SCALAR_STRING)
    private final String name;
    @FieldOf(type = GQLType.SCALAR_BOOLEAN)
    private final boolean isAlive;
    @FieldOf(type = GQLType.LIST)
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
