package org.example.entity;

import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLObjectType.Builder;
import graphql.schema.GraphQLScalarType;
import lombok.Getter;
import org.example.GenreType;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Book implements Schemable {
    private final long id;
    private final String title;
    private final GenreType genreAsEnum;
    private final Author author;
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
