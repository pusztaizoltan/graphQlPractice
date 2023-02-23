package org.example.test_db;

import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLMutation;
import org.example.graphql.annotation.GQLQuery;
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
    @GQLQuery
    public List<TestClass> allTestClass() {
        return testClassDB;
    }

    @Nonnull
    @GQLQuery
    public TestClass[] allTestClassAsArray() {
        return testClassDB.toArray(testClassDB.toArray(TestClass[]::new));
    }

    @Nonnull
    @GQLQuery
    public long[] allTestClassIdAsArray() {
        return testClassDB.stream().mapToLong(TestClass::getId).toArray();
    }

    @Nonnull
    @GQLQuery
    public List<Long> allTestClassIdAsList() {
        return testClassDB.stream().mapToLong(TestClass::getId).boxed().collect(Collectors.toList());
    }

    @Nonnull
    @GQLQuery
    public TestClass testClassById(@GQLArg(name = "id") long id) {
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
    @GQLQuery
    public Reader readerById(@GQLArg(name = "id") long id) {
        return readerDB.stream().filter(reader -> reader.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    @Nonnull
    @GQLQuery
    public Book bookById(@GQLArg(name = "id") long id) {
        return bookDB.stream().filter(book -> book.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    @Nonnull
    @GQLQuery
    public Author authorById(@GQLArg(name = "id") long id) {
        return authorDB.stream().filter(book -> book.getId() == id).findFirst().orElseThrow(() -> new IllegalArgumentException(EXCEPTION_MESSAGE));
    }

    // TODO: in this case the annotation doesn't hold too much info, because it is obvious that it's list
    // todo for human reader No but for JVM it is a useful shortcut
    @Nonnull
    @GQLQuery
    public List<Reader> allReader() {
        return readerDB;
    }

    @Nonnull
    @GQLQuery
    public List<Book> allBook() {
        return bookDB;
    }

    @Nonnull
    @GQLQuery
    public List<Author> allAuthor() {
        return authorDB;
    }

    @Nonnull
    @GQLQuery
    public List<Book> bookByGenre(@GQLArg(name = "genre") GenreType genre) {
        return bookDB.stream().filter(book -> book.getGenre() == genre).collect(Collectors.toList());
    }

    @Nonnull
    @GQLQuery
    public List<Author> authorByIsAlive(@GQLArg(name = "isAlive") boolean isAlive) {
        return authorDB.stream().filter(author -> author.isAlive() == isAlive).collect(Collectors.toList());
    }

    @Nonnull
    @GQLQuery
    public List<Book> bookByTitleContent(@GQLArg(name = "titleContent") String titleContent) {
        return bookDB.stream().filter(book -> book.getTitle().contains(titleContent)).collect(Collectors.toList());
    }

    @GQLMutation
    public long newReaderByInputObject(@GQLArg(name = "readerDTO") @Nonnull ReaderDTO readerDTO) {
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

    @GQLMutation
    public long newReaderByFieldArgsWithId(
            @GQLArg(name = "id") int id,
            @GQLArg(name = "fullName") @Nonnull String fullName,
            @GQLArg(name = "email") @Nonnull String email) {
        if (this.readerDB.stream().anyMatch(reader -> reader.getId() == id)) {
            throw new IllegalArgumentException(EXCEPTION_MESSAGE);
        } else {
            this.readerDB.add(new Reader(id, fullName, email));
        }
        return id;
    }

    @GQLMutation
    public long newReaderByFieldArgsWithoutId(
            @GQLArg(name = "fullName") @Nonnull String fullName,
            @GQLArg(name = "email") @Nonnull String email) {
        long newId = this.readerDB.stream().mapToLong(Reader::getId).max().orElse(0);
        this.readerDB.add(new Reader(newId, fullName, email));
        return newId;
    }

    @GQLMutation
    public long newAuthorByInputObject(@GQLArg(name = "authorDTO") @Nonnull AuthorDTO authorDTO) {
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

    @GQLQuery
    public GenreType genreOfBookById(@GQLArg(name = "id") long id) {
        return bookById(id).getGenre();
    }

    @GQLQuery
    public boolean isAliveOfAuthorById(@GQLArg(name = "id") long id) {
        return authorById(id).isAlive();
    }

    @GQLQuery
    public List<Book> booksByIdList(@GQLArg(name = "ids") List<Long> ids) {
        System.out.println("----------");
        System.out.println("booksByIdList called");
        System.out.println("ids:   " + ids);
        System.out.println("ids.class:   " + ids.getClass());
        System.out.println("List.class:   " + new ArrayList<>(List.of(1,2,3)).getClass());
        System.out.println("ids.get(0):   " + ids.get(0));
        System.out.println("ids.get(0).class:   " + ids.get(0).getClass());
//        System.out.println(bookDB.stream().mapToLong(book -> book.getId()).boxed().collect(Collectors.toList()));
//        System.out.println(ids.get(0));
//        bookDB.forEach(book -> System.out.println(
//                ids + " " + ids.getClass() +" "+ ids.get(0).getClass()));
//        ids.contains(Long.valueOf(book.getId()))
        return bookDB.stream().filter(book -> ids.contains(book.getId())).collect(Collectors.toList());
    }

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
