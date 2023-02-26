package org.example.graphql.generator_component.class_adapter;

import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLInput;

import javax.annotation.Nonnull;

public abstract class AbstractClassAdapter<T> {
    protected final Class<T> javaType;

    protected AbstractClassAdapter(@Nonnull Class<T> javaType) {
        this.javaType = javaType;
    }

    public abstract GraphQLType getGraphQLType();

    public String getName() {
        return javaType.getSimpleName();
    }



    public boolean isFetchable() {
        return this instanceof Fetchable;
    }

    /**
     * Factory method of TypeFactory
     */
    public static <T> @Nonnull AbstractClassAdapter<T> adapterOf(@Nonnull Class<T> javaType) {
        if (javaType.isEnum()) {
            return new EnumAdapter<>(javaType);
        } else if (javaType.isAnnotationPresent(GQLInput.class)) {
            return new InputAdapter<>(javaType);
        } else {
            return new OutputAdapter<>(javaType);
        }
    }
}
