package org.example.test_entity;

import lombok.Getter;
import org.example.graphql.annotation.GGLField;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

import java.util.ArrayList;
import java.util.List;

@Getter
@TypeOf(type = GQLType.OBJECT)
public class Reader {
    @GGLField(type = GQLType.SCALAR_INT)
    private final long id;
    @GGLField(type = GQLType.SCALAR_STRING)
    private final String fullName;
    @GGLField(type = GQLType.SCALAR_STRING)
    private final String email;
    @GGLField(type = GQLType.LIST)
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

