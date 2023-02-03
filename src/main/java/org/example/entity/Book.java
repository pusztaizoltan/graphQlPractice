package org.example.entity;

import lombok.Getter;
import org.example.GenreType;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.ScalarFitter;
import org.example.graphQL.annotation.UseMarker;

import java.util.ArrayList;
import java.util.List;


@Getter
public class Book implements Schemable {
    @UseMarker(category = GraphQlIdentifyer.SCALAR, asScalar = ScalarFitter.INT)
    private final long id;

    @UseMarker(category = GraphQlIdentifyer.SCALAR, asScalar = ScalarFitter.STRING)
    private final String title;
    @UseMarker(category = GraphQlIdentifyer.ENUM)
    private final GenreType genreAsEnum;

    @UseMarker(category = GraphQlIdentifyer.TYPE)
    private final Author author;
    @UseMarker(category = GraphQlIdentifyer.NESTED_TYPE)
    private List<Reader> readers = new ArrayList<>();

    public Book(long id, String title, GenreType genreAsEnum, Author author) {
        this.id = id;
        this.title = title;
        this.genreAsEnum = genreAsEnum;
        this.author = author;
    }

    public void addReader(Reader reader) {
        this.readers.add(reader);
    }
}
