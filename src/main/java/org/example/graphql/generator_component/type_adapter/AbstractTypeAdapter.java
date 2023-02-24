package org.example.graphql.generator_component.type_adapter;

import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLInput;
import org.example.graphql.generator_component.util.Fetchable;

import javax.annotation.Nonnull;

public abstract class AbstractTypeAdapter<T> {
    protected final Class<T> javaType;
    protected GraphQLType graphQLType;

    protected AbstractTypeAdapter(@Nonnull Class<T> javaType) {
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

    /**
     * Factory method of TypeFactory
     */
    public static <T> @Nonnull AbstractTypeAdapter<T> adapterOf(@Nonnull Class<T> javaType) {
        if (javaType.isEnum()) {
            return new EnumAdapter<>(javaType);
        } else if (javaType.isAnnotationPresent(GQLInput.class)) {
            return new InputAdapter<>(javaType);
        } else {
            return new OutputAdapter<>(javaType);
        }
    }
}
