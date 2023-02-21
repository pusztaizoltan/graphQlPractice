package org.example.graphql.generator_component.factory_type.type_converters;

import graphql.schema.GraphQLCodeRegistry;

import javax.annotation.Nonnull;

public interface Fetchable {
    @Nonnull
    GraphQLCodeRegistry getRegistry();
}
