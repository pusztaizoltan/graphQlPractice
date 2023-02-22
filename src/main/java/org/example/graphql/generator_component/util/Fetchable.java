package org.example.graphql.generator_component.util;

import graphql.schema.GraphQLCodeRegistry;

import javax.annotation.Nonnull;

public interface Fetchable {
    @Nonnull
    GraphQLCodeRegistry getRegistry();
}
