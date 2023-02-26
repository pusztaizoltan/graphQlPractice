package org.example.graphql.generator_component.class_adapter;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLType;

public class EnumAdapter<T> extends AbstractClassAdapter<T> {
    GraphQLEnumType.Builder enumBuilder = GraphQLEnumType.newEnum().name(super.javaType.getSimpleName());

    public EnumAdapter(Class<T> javaType) {
        super(javaType);
    }

    @Override
    public GraphQLType getGraphQLType() {
        Enum<?>[] enumConstants = super.javaType.asSubclass(Enum.class).getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            enumBuilder.value(enumConstant.name());
        }
        return enumBuilder.build();
    }
}
