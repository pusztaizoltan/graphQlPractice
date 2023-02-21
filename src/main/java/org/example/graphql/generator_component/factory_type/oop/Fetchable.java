package org.example.graphql.generator_component.factory_type.oop;

import graphql.schema.GraphQLCodeRegistry;

import javax.annotation.Nonnull;

public interface Fetchable {
    @Nonnull
    GraphQLCodeRegistry getRegistry();
}
