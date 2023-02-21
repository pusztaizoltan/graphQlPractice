package org.example.graphql.generator_component.factory_type.oop;

import graphql.schema.GraphQLInputObjectType;
import org.example.graphql.annotation.GQLField;

import java.lang.reflect.Field;

public class ConverterInput<T> extends ConverterAbstract<T> {
    public ConverterInput(Class<T> javaType) {
        super(javaType);
    }

    @Override
    protected void buildGraphQLAnalogue() {
        GraphQLInputObjectType.Builder builder = GraphQLInputObjectType.newInputObject().name(super.getName());
        for (Field field : javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                builder.field(FieldFactory.GQLInputFieldFrom(field));
            }
        }
        super.graphQLType = builder.build();
    }
}
