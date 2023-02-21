package org.example.graphql.generator_component.factory_type.oop;

import graphql.schema.GraphQLEnumType;

public class ConverterEnum<T> extends ConverterAbstract<T> {
    public ConverterEnum(Class<T> javaType) {
        super(javaType);
    }

    @Override
    public void buildGraphQLAnalogue() {
        GraphQLEnumType.Builder builder = GraphQLEnumType.newEnum().name(super.getName());
        Enum<?>[] enumConstants = super.javaType.asSubclass(Enum.class).getEnumConstants();
        for (Enum<?> enumConstant : enumConstants) {
            builder.value(enumConstant.name());
        }
        super.graphQLType = builder.build();
    }
}
