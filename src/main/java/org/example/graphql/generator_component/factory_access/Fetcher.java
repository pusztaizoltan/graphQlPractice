package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class Fetcher<T> implements DataFetcher<T> {
    @Override
    public T get(DataFetchingEnvironment environment) throws Exception {
        return null;
    }
}
