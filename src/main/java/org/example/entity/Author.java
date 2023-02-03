package org.example.entity;

import lombok.Getter;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.ScalarFitter;
import org.example.graphQL.annotation.UseMarker;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Author {
    @UseMarker(category = GraphQlIdentifyer.SCALAR, asScalar = ScalarFitter.INT)
    private final long id;
    @UseMarker(category = GraphQlIdentifyer.SCALAR, asScalar = ScalarFitter.STRING)
    private final String name;
    @UseMarker(category = GraphQlIdentifyer.SCALAR, asScalar = ScalarFitter.BOOLEAN)
    private final boolean isAlive;
    @UseMarker(category = GraphQlIdentifyer.NESTED_TYPE)
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
