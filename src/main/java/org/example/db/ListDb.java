package org.example.db;

import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.entity.TestClass;

import java.util.List;

// TODO: missing JavaDocs & nullity annotations; please apply for all classes
public interface ListDb {
    List<TestClass> allTestClass();

    TestClass testClassById(long id);

    Reader readerById(long id);

    Book bookById(long id);

    List<Reader> allReader();

    List<Book> allBook();
}
