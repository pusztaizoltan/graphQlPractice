package org.example.entity;

import lombok.Getter;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.ScalarFitter;
import org.example.graphQL.annotation.UseMarker;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Reader {
    @UseMarker(category = GraphQlIdentifyer.SCALAR, asScalar = ScalarFitter.INT)
    private final long id;
    @UseMarker(category = GraphQlIdentifyer.SCALAR, asScalar = ScalarFitter.STRING)
    private final String fullName;
    @UseMarker(category = GraphQlIdentifyer.SCALAR, asScalar = ScalarFitter.STRING)
    private final String email;
    @UseMarker(category = GraphQlIdentifyer.NESTED_TYPE)
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

