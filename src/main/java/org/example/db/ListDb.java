package org.example.db;

import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.entity.TestClass;

import java.util.List;

public interface ListDb {
    List<TestClass> allTestClass();

    TestClass testClassById(long id);

    List<Reader> allClients();

    Reader clientById(long id);

    Book bookById(long id);
}
