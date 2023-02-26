package org.example.graphql.generator_component.class_adapter;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLType;

import javax.annotation.Nonnull;

public class EnumAdapter<T> extends AbstractClassAdapter {
    private final Class<T> javaType;
    private final GraphQLEnumType.Builder enumBuilder = GraphQLEnumType.newEnum();

    protected EnumAdapter(@Nonnull Class<T> javaType) {
        super();
        this.javaType = javaType;
        this.enumBuilder.name(javaType.getSimpleName());
    }

    @Override
    public @Nonnull GraphQLType getGraphQLType() {
        Enum<?>[] enumConstants = javaType.asSubclass(Enum.class).getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            enumBuilder.value(enumConstant.name());
        }
        return enumBuilder.build();
    }
}
