package org.example.test_db;

import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLMutation;
import org.example.graphql.annotation.GQLQuery;
import org.example.graphql.annotation.GQLType;
import org.example.test_dto.AuthorDTO;
import org.example.test_dto.ReaderDTO;
import org.example.test_entity.Author;
import org.example.test_entity.Book;
import org.example.test_entity.GenreType;
import org.example.test_entity.Reader;
import org.example.test_entity.TestClass;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ListDbTestImpl {
    private static final String EXCEPTION_MESSAGE = "invalid id";
    private final List<TestClass> testClassDB = new ArrayList<>();
    private final List<Reader> readerDB = new ArrayList<>();
    private final List<Author> authorDB = new ArrayList<>();
    private final List<Book> bookDB = new ArrayList<>();

    @Nonnull
    @GQLQuery(type = GQLType.LIST)
    public List<TestClass> allTestClass() {
        return testClassDB;
    }

    @Nonnull
    @GQLQuery(type = GQLType.ARRAY)
    public TestClass[] allTestClassAsArray() {
        return testClassDB.toArray(testClassDB.toArray(TestClass[]::new));
    }

    @Nonnull
    @GQLQuery(type = GQLType.ARRAY)
    // todo
    public long[] allTestClassIdAsArray() {
        return testClassDB.stream().mapToLong(TestClass::getId).toArray();
    }

    @Nonnull
    @GQLQuery(type = GQLType.LIST)
    // todo
    public List<Long> allTestClassIdAsList() {
        return testClassDB.stream().mapToLong(TestClass::getId).boxed().collect(Collectors.toList());
    }

    @Nonnull
    @GQLQuery(type = GQLType.OBJECT)
    public TestClass testClassById(@GQLArg(name = "id", type = GQLType.SCALAR_INT) long id) {
        // TODO: within our project we ar not using streams because of the side effects produced by them:
        // - lots of unwanted small objects are created which in a big scale (millions of occurrences)
        //   may cause server garbage collection breakdown
        // TODO 2: using lists for lookup has not the best efficiency considering a possible larger
        // set of data, the code will iterate through the whole set. For such cases we are using HashMaps
        // todo done no longer part of production code so cannot cause issues because size is controlled
        // and  stream solutions removed from production code not testing relevant too
        return testClassDB.stream().filter(item -> item.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    @Nonnull
    @GQLQuery(type = GQLType.OBJECT)
    public Reader readerById(@GQLArg(name = "id", type = GQLType.SCALAR_INT) long id) {
        return readerDB.stream().filter(reader -> reader.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    @Nonnull
    @GQLQuery(type = GQLType.OBJECT)
    public Book bookById(@GQLArg(name = "id", type = GQLType.SCALAR_INT) long id) {
        return bookDB.stream().filter(book -> book.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    @Nonnull
    @GQLQuery(type = GQLType.OBJECT)
    public Author authorById(@GQLArg(name = "id", type = GQLType.SCALAR_INT) long id) {
        return authorDB.stream().filter(book -> book.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    // TODO: in this case the annotation doesn't hold too much info, because it is obvious that it's list
    // todo for human reader No but for JVM it is a useful shortcut
    @Nonnull
    @GQLQuery(type = GQLType.LIST)
    public List<Reader> allReader() {
        return readerDB;
    }

    @Nonnull
    @GQLQuery(type = GQLType.LIST)
    public List<Book> allBook() {
        return bookDB;
    }

    @Nonnull
    @GQLQuery(type = GQLType.LIST)
    public List<Author> allAuthor() {
        return authorDB;
    }

    @Nonnull
    @GQLQuery(type = GQLType.LIST)
    public List<Book> bookByGenre(@GQLArg(name = "genre", type = GQLType.ENUM) GenreType genre) {
        return bookDB.stream().filter(book -> book.getGenre() == genre).collect(Collectors.toList());
    }

    @Nonnull
    @GQLQuery(type = GQLType.LIST)
    public List<Author> authorByIsAlive(@GQLArg(name = "isAlive", type = GQLType.SCALAR_BOOLEAN) boolean isAlive) {
        return authorDB.stream().filter(author -> author.isAlive() == isAlive).collect(Collectors.toList());
    }

    @Nonnull
    @GQLQuery(type = GQLType.LIST)
    public List<Book> bookByTitleContent(@GQLArg(name = "titleContent", type = GQLType.SCALAR_STRING) String titleContent) {
        return bookDB.stream().filter(book -> book.getTitle().contains(titleContent)).collect(Collectors.toList());
    }

    @GQLMutation(type = GQLType.SCALAR_INT)
    public long newReaderByInputObject(@GQLArg(name = "readerDTO", type = GQLType.OBJECT) @Nonnull ReaderDTO readerDTO) {
        if (readerDTO.getId() == null) {
            long newId = this.readerDB.stream().mapToLong(Reader::getId).max().orElse(0);
            this.readerDB.add(readerDTO.toReaderOfId(newId));
            return newId;
        } else {
            if (this.readerDB.stream().anyMatch(reader -> reader.getId() == readerDTO.getId().longValue())) {
                throw new IllegalArgumentException(EXCEPTION_MESSAGE);
            }
            this.readerDB.add(readerDTO.toReaderOfId());
            return readerDTO.getId().longValue();
        }
    }

    @GQLMutation(type = GQLType.SCALAR_INT)
    public long newReaderByFieldArgsWithId(
            @GQLArg(name = "id", type = GQLType.SCALAR_INT) int id,
            @GQLArg(name = "fullName", type = GQLType.SCALAR_STRING) @Nonnull String fullName,
            @GQLArg(name = "email", type = GQLType.SCALAR_STRING) @Nonnull String email) {
        if (this.readerDB.stream().anyMatch(reader -> reader.getId() == id)) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE);
        } else {
            this.readerDB.add(new Reader(id, fullName, email));
        }
        return id;
    }

    @GQLMutation(type = GQLType.SCALAR_INT)
    public long newReaderByFieldArgsWithoutId(
            @GQLArg(name = "fullName", type = GQLType.SCALAR_STRING) @Nonnull String fullName,
            @GQLArg(name = "email", type = GQLType.SCALAR_STRING) @Nonnull String email) {
        long newId = this.readerDB.stream().mapToLong(Reader::getId).max().orElse(0);
        this.readerDB.add(new Reader(newId, fullName, email));
        return newId;
    }

    @GQLMutation(type = GQLType.SCALAR_INT)
    public long newAuthorByInputObject(@GQLArg(name = "authorDTO", type = GQLType.OBJECT) @Nonnull AuthorDTO authorDTO) {
        if (authorDTO.getId() == null) {
            long newId = this.authorDB.stream().mapToLong(Author::getId).max().orElse(0);
            this.authorDB.add(authorDTO.toAuthorOfId(newId));
            return newId;
        } else {
            if (this.authorDB.stream().anyMatch(reader -> reader.getId() == authorDTO.getId().longValue())) {
                throw new IllegalArgumentException(EXCEPTION_MESSAGE);
            }
            this.authorDB.add(authorDTO.toAuthorOfId());
            return authorDTO.getId().longValue();
        }
    }

    @GQLQuery(type = GQLType.ENUM)
    public GenreType genreOfBookById(@GQLArg(name = "id", type = GQLType.SCALAR_INT) long id) {
        return bookById(id).getGenre();
    }

    @GQLQuery(type = GQLType.SCALAR_BOOLEAN)
    public boolean isAliveOfAuthorById(@GQLArg(name = "id", type = GQLType.SCALAR_INT) long id) {
        return authorById(id).isAlive();
    }

//    @GQLQuery(type = GQLType.LIST)
//    public List<Book> booksByIdList(@GQLArg(name = "ids", type = GQLType.LIST) List<Long> ids) {
//        return bookDB.stream().filter(book -> ids.contains(book.getId())).collect(Collectors.toList());
//    }

    public void initDb() {
        // init 10 testClass
        for (int i = 0; i < 10; i++) {
            testClassDB.add(new TestClass(i, String.format("TestContent_%s", i)));
        }
        // init 10 generated author without books
        for (int i = 0; i < 10; i++) {
            authorDB.add(new Author(i, String.format("AuthorName_%s", i), new boolean[]{true, false}[i % 2]));
        }
        // init 100 generated book with authors 10 book for each author
        for (int i = 0; i < 100; i++) {
            int authorId = i % 10;
            Book book = new Book(i, String.format("BookTitle_%s", i), GenreType.getById(i % 4), authorDB.get(authorId));
            authorDB.get(authorId).addBook(book);
            bookDB.add(book);
        }
        // init 20 generated reader with books 5 book each
        for (int i = 0; i < 20; i++) {
            Reader reader = new Reader(i, String.format("ReaderName_%s", i), String.format("ReaderMail_%s", i));
            for (int j = 0; j < 5; j++) {
                Book book = bookDB.get((i * 5) + j);
                reader.addBook(book);
                book.addReader(reader);
            }
            readerDB.add(reader);
        }
    }
}
