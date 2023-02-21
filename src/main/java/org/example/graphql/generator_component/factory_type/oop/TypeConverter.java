package org.example.graphql.generator_component.factory_type.oop;

import graphql.schema.GraphQLType;
import org.example.graphql.generator_component.factory_type.oop.Fetchable;

import javax.annotation.Nonnull;

public abstract class TypeConverter<T> {
    Class<T> javaType;
    GraphQLType graphQLType;

    public TypeConverter(Class<T> javaType) {
        this.javaType = javaType;
    }

    protected abstract @Nonnull GraphQLType buildGraphQLAnalogue();

    String getName(){
        return javaType.getSimpleName();
    }

    public GraphQLType getGraphQLType() {
        return graphQLType;
    }

    public boolean  isFetchable(){
        return this instanceof Fetchable;
    }
}
