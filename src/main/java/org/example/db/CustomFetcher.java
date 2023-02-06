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

    public DataFetcher<List<TestClass>> testClassFetcher = environment -> listDbImpl.allTestClass();
    public DataFetcher<TestClass> testClassByIdFetcher = environment -> {
        long id = ((Integer) environment.getArgument("id")).intValue();
        return listDbImpl.testClassById(id);
    };
    public DataFetcher<List<Reader>> readerFetcher = environment -> listDbImpl.allReader();
    public DataFetcher<List<Book>> bookFetcher = environment -> listDbImpl.allBook();
    public DataFetcher<Integer> testIdFetcher = environment -> ((TestClass) environment.getSource()).getId();
    public DataFetcher<String> testContentFetcher = environment -> ((TestClass) environment.getSource()).getContent();
}
