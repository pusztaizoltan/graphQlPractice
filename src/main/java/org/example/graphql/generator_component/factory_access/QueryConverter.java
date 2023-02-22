package org.example.graphql.generator_component.factory_access;


import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;

import java.lang.reflect.Method;

public class QueryConverter<T> {
    GraphQLFieldDefinition accessField;
    DataFetcher<?> fetcher;
    Method method;
    Class<T> returnType;

    public QueryConverter(Method method) {
        Class<?> returnType = method.getReturnType();
        method.isBridge();
        method.isSynthetic();
        method.getDefaultValue();
    }





    QueryConverter(Method method, Class<T> returnType, Object dataService) {
        System.out.println("1.) " + method.getReturnType());
        this.method = method;
        this.returnType = returnType;
        this.accessField = DataAccessFactory.createDataAccessorFor(method);
        this.fetcher = FetcherFactory.createFetcherFor(method, dataService);
    }


}
