package org.example.graphql.generator_component.class_adapter;

import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLInput;

import javax.annotation.Nonnull;

public abstract class AbstractClassAdapter {
    protected AbstractClassAdapter() {
    }

    public abstract @Nonnull GraphQLType getGraphQLType();

    public boolean isFetchable() {
        return this instanceof Fetchable;
    }

    /**
     * Factory method of TypeFactory
     */
    public static <T> @Nonnull AbstractClassAdapter adapterOf(@Nonnull Class<T> javaType) {
        if (javaType.isEnum()) {
            return new EnumAdapter<>(javaType);
        } else if (javaType.isAnnotationPresent(GQLInput.class)) {
            return new InputAdapter<>(javaType);
        } else {
            return new OutputAdapter<>(javaType);
        }
    }
}
