package org.example.db;

import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.entity.TestClass;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.UseMarker;

import java.util.List;

public interface ListDb {
    @UseMarker(category = GraphQlIdentifyer.NESTED_TYPE)
    List<TestClass> allTestClass();

    @UseMarker(category = GraphQlIdentifyer.TYPE)
    TestClass testClassById(long id);

    @UseMarker(category = GraphQlIdentifyer.NESTED_TYPE)
    List<Reader> allClients();

    @UseMarker(category = GraphQlIdentifyer.TYPE)
    Reader clientById(long id);

    @UseMarker(category = GraphQlIdentifyer.TYPE)
    Book bookById(long id);
}
