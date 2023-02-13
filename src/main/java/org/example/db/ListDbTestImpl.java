package org.example.db;

import org.example.dto.ReaderDTO;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.GenreType;
import org.example.entity.Reader;
import org.example.entity.TestClass;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.Mutate;
import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    @FieldOf(type = GQLType.LIST)
    public List<TestClass> allTestClass() {
        return testClassDB;
    }

    @NotNull
    @Override
    @FieldOf(type = GQLType.OBJECT)
    public TestClass testClassById(@ArgWith(name = "id", type = GQLType.SCALAR_INT) long id) {
        return testClassDB.stream().filter(item -> item.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    @NotNull
    @Override
    @FieldOf(type = GQLType.OBJECT)
    public Reader readerById(@ArgWith(name = "id", type = GQLType.SCALAR_INT) long id) {
        return readerDB.stream().filter(reader -> reader.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    @NotNull
    @Override
    @FieldOf(type = GQLType.OBJECT)
    public Book bookById(@ArgWith(name = "id", type = GQLType.SCALAR_INT) long id) {
        return bookDB.stream().filter(book -> book.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    @NotNull
    @Override
    @FieldOf(type = GQLType.LIST)
    public List<Reader> allReader() {
        return readerDB;
    }

    @NotNull
    @Override
    @FieldOf(type = GQLType.LIST)
    public List<Book> allBook() {
        return bookDB;
    }

    @NotNull
    @Override
    @FieldOf(type = GQLType.LIST)
    public List<Book> bookByGenre(@ArgWith(name = "genre", type = GQLType.ENUM) GenreType genre) {
        return bookDB.stream().filter(book -> book.getGenre() == genre).collect(Collectors.toList());
    }

    @NotNull
    @Override
    @FieldOf(type = GQLType.LIST)
    public List<Author> authorByIsAlive(@ArgWith(name = "isAlive", type = GQLType.SCALAR_BOOLEAN) boolean isAlive) {
        return authorDB.stream().filter(author -> author.isAlive() == isAlive).collect(Collectors.toList());
    }

    @NotNull
    @Override
    @FieldOf(type = GQLType.LIST)
    public List<Book> bookByTitleContent(@ArgWith(name = "titleContent", type = GQLType.SCALAR_STRING) String titleContent) {
        return bookDB.stream().filter(book -> book.getTitle().contains(titleContent)).collect(Collectors.toList());
    }

    @Override
    @Mutate(type = GQLType.SCALAR_INT)
    public long newReader(@ArgWith(name = "readerDTO", type = GQLType.INPUT) @NotNull ReaderDTO readerDTO) {
        System.out.println("- ListDbTestImpl");
        if(readerDTO.getId() == null) {
            long newId = this.readerDB.stream().mapToLong(Reader::getId).max().orElse(0);
            this.readerDB.add(readerDTO.toReaderOfId(newId));
            return newId;
        } else {
            if(this.readerDB.stream().anyMatch(reader-> reader.getId() == readerDTO.getId().longValue())){
                throw new IllegalArgumentException(EXCEPTION_MESSAGE);
            }
            this.readerDB.add(readerDTO.toReaderOfId());
            return readerDTO.getId().longValue();

        }
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
