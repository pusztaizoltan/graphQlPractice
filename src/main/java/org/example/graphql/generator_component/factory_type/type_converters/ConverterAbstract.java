package org.example.graphql.generator_component.factory_type.type_converters;

import graphql.schema.GraphQLType;

import javax.annotation.Nonnull;

public abstract class ConverterAbstract<T> {
    protected final Class<T> javaType;
    protected GraphQLType graphQLType;

    protected ConverterAbstract(@Nonnull Class<T> javaType) {
        this.javaType = javaType;
        buildGraphQLAnalogue();
    }

    protected abstract void buildGraphQLAnalogue();

    public String getName() {
        return javaType.getSimpleName();
    }

    public GraphQLType getGraphQLType() {
        return graphQLType;
    }

    public boolean isFetchable() {
        return this instanceof Fetchable;
    }
}
