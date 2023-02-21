package org.example.graphql.generator_component.factory_type.oop;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLType;

import javax.annotation.Nonnull;

public class EnumConverter<T> extends TypeConverter<T> {
    public EnumConverter(Class<T> javaType) {
        super(javaType);
        super.graphQLType = buildGraphQLAnalogue();
    }

    @Override
    public @Nonnull GraphQLType buildGraphQLAnalogue() {
        GraphQLEnumType.Builder builder = GraphQLEnumType.newEnum().name(super.getName());
        Enum<?>[] enumConstants = super.javaType.asSubclass(Enum.class).getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            builder.value(enumConstant.name());
        }
        return builder.build();
    }

    @Override
    public boolean hasFetchers() {
        return false;
    }
}
