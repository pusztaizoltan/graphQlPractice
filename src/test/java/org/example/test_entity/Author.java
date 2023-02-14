package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.QGLField;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

import java.util.ArrayList;
import java.util.List;

@Getter
@TypeOf(type = GQLType.OBJECT)
public class Author {
    @QGLField(type = GQLType.SCALAR_INT)
    private final long id;
    @QGLField(type = GQLType.SCALAR_STRING)
    private final String name;
    @QGLField(type = GQLType.SCALAR_BOOLEAN)
    private final boolean isAlive;
    @QGLField(type = GQLType.LIST)
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
