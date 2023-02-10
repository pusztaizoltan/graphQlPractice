package org.example.db;

import org.example.dto.ReaderDTO;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.GenreType;
import org.example.entity.Reader;
import org.example.entity.TestClass;

import java.util.List;

public interface ListDb {
    List<TestClass> allTestClass();

    TestClass testClassById(long id);

    Reader readerById(long id);

    Book bookById(long id);

    List<Reader> allReader();

    List<Book> allBook();

    List<Book> bookByGenre(GenreType genre);

    List<Author> authorByIsAlive(boolean isAlive);

    List<Book> bookByTitleContent(String titleContent);

    long newReader(ReaderDTO readerDTO);
}
