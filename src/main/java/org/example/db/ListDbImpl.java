package org.example.db;

import lombok.Getter;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.GenreType;
import org.example.entity.Reader;
import org.example.entity.TestClass;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.UseMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class ListDbImpl implements ListDb {
    private final List<TestClass> testClassDB = new ArrayList<>();
    private final List<Reader> readerDB = new ArrayList<>();
    private final List<Author> authorDB = new ArrayList<>();
    private final List<Book> bookDB = new ArrayList<>();

    public ListDbImpl() {
        initDb();
    }

    @UseMarker(category = GraphQlIdentifyer.NESTED_TYPE)
    public List<TestClass> allTestClass() {
        return testClassDB;
    }

    @UseMarker(category = GraphQlIdentifyer.TYPE)
    public TestClass testClassById(long id) {
        return testClassDB.stream().filter((item) -> item.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException("invalid id"));
    }

    @UseMarker(category = GraphQlIdentifyer.NESTED_TYPE)
    public List<Reader> allClients() {
        return readerDB;
    }

    @UseMarker(category = GraphQlIdentifyer.TYPE)
    public Reader clientById(long id) {
        return readerDB.stream().filter((reader) -> reader.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException("invalid id"));
    }

    @UseMarker(category = GraphQlIdentifyer.TYPE)
    public Book bookById(long id) {
        return bookDB.stream().filter((book) -> book.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException("invalid id"));
    }

    private void initDb() {
        // init 10 testClass
        for (int i = 0; i < 10; i++) {
            testClassDB.add(new TestClass(i, String.format("TestContent_%s", i)));
        }
        // init 10 generated author without books
        for (int i = 0; i < 10; i++) {
            authorDB.add(new Author(i, String.format("AuthorName_%s", i), new boolean[]{true, false}[i % 2]));
        }
        // init 100 generated book with authors
        for (int i = 0; i < 100; i++) {
            int authorId = (i + new Random().nextInt(7)) % 10;
            Book book = new Book(i, String.format("BookTitle_%s", i), GenreType.getById(i % 4), authorDB.get(authorId));
            authorDB.get(authorId).addBook(book);
            bookDB.add(book);
        }
        // init 20 generated reader with books
        for (int i = 0; i < 20; i++) {
            Reader reader = new Reader(i, String.format("ReaderName_%s", i), String.format("ReaderMail_%s", i));
            for (int j = 0; j <= new Random().nextInt(5); j++) {
                Book book = bookDB.get(new Random().nextInt(100));
                reader.addBook(book);
                book.addReader(reader);
            }
            readerDB.add(reader);
        }
    }
}