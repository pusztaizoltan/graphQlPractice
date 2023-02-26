package org.example.graphql.generator_component.class_adapter;

import graphql.schema.GraphQLCodeRegistry;

import javax.annotation.Nonnull;

public interface Fetchable {
    @Nonnull
    GraphQLCodeRegistry getFetcherRegistry();
}
