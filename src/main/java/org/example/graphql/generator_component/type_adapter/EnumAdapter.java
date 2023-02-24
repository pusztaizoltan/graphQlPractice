package org.example.graphql.generator_component.type_adapter;

import graphql.schema.GraphQLEnumType;

public class EnumAdapter<T> extends AbstractTypeAdapter<T> {
    public EnumAdapter(Class<T> javaType) {
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
