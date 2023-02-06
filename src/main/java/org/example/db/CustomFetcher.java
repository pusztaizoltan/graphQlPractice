package org.example.db;

import graphql.schema.DataFetcher;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.entity.TestClass;

import java.util.List;

public class CustomFetcher {
    private ListDbImpl listDbImpl;

    public CustomFetcher(ListDbImpl listDbImpl) {
        this.listDbImpl = listDbImpl;
    }

    public DataFetcher<List<TestClass>> testClassFetcher = environment -> listDbImpl.getTestClassDB();
    public DataFetcher<TestClass> testClassByIdFetcher = environment -> {
        long id = ((Integer) environment.getArgument("id")).intValue();
        return listDbImpl.testClassById(id);
    };
//    public DataFetcher<List<Book>> booksByGenreEnum = environment -> {
//        // explanation todo: argument return as string even if enum so every cross typing variation use string as mediator
//        GenreType genre = GenreType.valueOf(environment.getArgument("genreAsEnum"));
//        //todo delegate filter to ListDb
//        return listDb.getBookDB().stream().filter((i) -> i.getGenreAsEnum().equals(genre)).collect(Collectors.toList());
//    };
//    public DataFetcher<List<Book>> booksByGenreString = environment -> {
//        String genre = (String) environment.getArgument("genreAsString");
//        //todo delegate filter to ListDb
//        return listDb.getBookDB().stream().filter((i) -> i.getGenreAsString().equals(genre)).collect(Collectors.toList());
//    };
    public DataFetcher<List<Reader>> readerFetcher = environment -> listDbImpl.getReaderDB();
    public DataFetcher<List<Book>> bookFetcher = environment -> listDbImpl.getBookDB();
    public DataFetcher<Integer> testIdFetcher = environment -> ((TestClass) environment.getSource()).getId();
    public DataFetcher<String> testContentFetcher = environment -> ((TestClass) environment.getSource()).getContent();
}
