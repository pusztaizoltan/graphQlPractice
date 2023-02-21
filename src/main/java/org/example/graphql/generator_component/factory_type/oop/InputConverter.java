package org.example.graphql.generator_component.factory_type.oop;

import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLField;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class InputConverter<T> extends TypeConverter<T> {
    public InputConverter(Class<T> javaType) {
        super(javaType);
        super.graphQLType = buildGraphQLAnalogue();
    }

    @Override
    protected @Nonnull GraphQLType buildGraphQLAnalogue() {
        GraphQLInputObjectType.Builder inputObjectTypeBuilder = GraphQLInputObjectType.newInputObject().name(super.name());
        for (Field field : javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                inputObjectTypeBuilder.field(FieldFactory.GQLInputFieldFrom(field));
            }
        }
        return inputObjectTypeBuilder.build();
    }

    @Override
    public boolean hasFetchers() {
        return false;
    }
}
