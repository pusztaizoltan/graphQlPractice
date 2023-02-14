package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.QGLField;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

import java.util.ArrayList;
import java.util.List;

@Getter
@TypeOf(type = GQLType.OBJECT)
public class Reader {
    @QGLField(type = GQLType.SCALAR_INT)
    private final long id;
    @QGLField(type = GQLType.SCALAR_STRING)
    private final String fullName;
    @QGLField(type = GQLType.SCALAR_STRING)
    private final String email;
    @QGLField(type = GQLType.LIST)
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

