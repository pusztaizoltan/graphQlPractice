package org.example.db;

import org.example.dto.ReaderDTO;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.GenreType;
import org.example.entity.Reader;
import org.example.entity.TestClass;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.FieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ListDbTestImpl implements ListDb {
    private static final String EXCEPTION_MESSAGE = "invalid id";
    private final List<TestClass> testClassDB = new ArrayList<>();
    private final List<Reader> readerDB = new ArrayList<>();
    private final List<Author> authorDB = new ArrayList<>();
    private final List<Book> bookDB = new ArrayList<>();

    public ListDbTestImpl() {
        initDb();
    }

    @Override
    @FieldOf(type = FieldType.LIST)
    public List<TestClass> allTestClass() {
        return testClassDB;
    }

    @Override
    @FieldOf(type = FieldType.OBJECT)
    public TestClass testClassById(@ArgWith(name = "id", type = FieldType.SCALAR_INT) long id) {
        return testClassDB.stream().filter(item -> item.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    @Override
    @FieldOf(type = FieldType.OBJECT)
    public Reader readerById(@ArgWith(name = "id", type = FieldType.SCALAR_INT) long id) {
        return readerDB.stream().filter(reader -> reader.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    @Override
    @FieldOf(type = FieldType.OBJECT)
    public Book bookById(@ArgWith(name = "id", type = FieldType.SCALAR_INT) long id) {
        return bookDB.stream().filter(book -> book.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    @Override
    @FieldOf(type = FieldType.LIST)
    public List<Reader> allReader() {
        return readerDB;
    }

    @Override
    @FieldOf(type = FieldType.LIST)
    public List<Book> allBook() {
        return bookDB;
    }

    @Override
    @FieldOf(type = FieldType.LIST)
    public List<Book> bookByGenre(@ArgWith(name = "genre", type = FieldType.ENUM) GenreType genre) {
        return bookDB.stream().filter(book -> book.getGenre() == genre).collect(Collectors.toList());
    }

    @Override
    @FieldOf(type = FieldType.LIST)
    public List<Author> authorByIsAlive(@ArgWith(name = "isAlive", type = FieldType.SCALAR_BOOLEAN) boolean isAlive) {
        return authorDB.stream().filter(author -> author.isAlive() == isAlive).collect(Collectors.toList());
    }

    @Override
    @FieldOf(type = FieldType.LIST)
    public List<Book> bookByTitleContent(@ArgWith(name = "titleContent", type = FieldType.SCALAR_STRING) String titleContent) {
        return bookDB.stream().filter(book -> book.getTitle().contains(titleContent)).collect(Collectors.toList());
    }
    // todo mutation marker annotation
    public long newReader(ReaderDTO readerDTO){
        long newId = this.readerDB.stream().mapToLong(Reader::getId).max().orElse(0);
        this.readerDB.add(readerDTO.toReaderOfId(newId));
        return newId;
    }

    private void initDb() {
        Random rng = new Random();
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
            int authorId = (i + rng.nextInt(7)) % 10;
            Book book = new Book(i, String.format("BookTitle_%s", i), GenreType.getById(i % 4), authorDB.get(authorId));
            authorDB.get(authorId).addBook(book);
            bookDB.add(book);
        }
        // init 20 generated reader with books
        for (int i = 0; i < 20; i++) {
            Reader reader = new Reader(i, String.format("ReaderName_%s", i), String.format("ReaderMail_%s", i));
            for (int j = 0; j <= rng.nextInt(5); j++) {
                Book book = bookDB.get(rng.nextInt(100));
                reader.addBook(book);
                book.addReader(reader);
            }
            readerDB.add(reader);
        }
    }
}
