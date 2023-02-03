package org.example.db;

import lombok.Getter;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.Client;
import org.example.GenreType;
import org.example.entity.Reader;
import org.example.entity.TestClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class ListDb {
    private List<TestClass> testClassDB = new ArrayList<>();
    private List<Reader> readerDB = new ArrayList<>();
    private List<Author> authorDB = new ArrayList<>();
    private List<Book> bookDB = new ArrayList<>();

    public List<Client> allClients() {
        return readerDB.stream().map(reader -> (Client) reader).collect(Collectors.toList());
    }

    public Client clientById(long id) {
        return readerDB.stream().filter((reader) -> reader.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException("invalid id"));
    }

    public Book bookById(long id) {
        return bookDB.stream().filter((book) -> book.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException("invalid id"));
    }

    public TestClass testClassById(long id) {
        return testClassDB.stream().filter((item) -> item.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException("invalid id"));
    }

    public void initDb() {
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
