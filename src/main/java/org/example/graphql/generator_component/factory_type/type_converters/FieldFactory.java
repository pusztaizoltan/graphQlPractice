package org.example.graphql.generator_component.factory_type.type_converters;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.generator_component.factory_type.TypeFactory;
import org.example.graphql.generator_component.util.TypeData;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

/**
 * Used to create {@link GraphQLFieldDefinition} and {@link GraphQLInputObjectField),
 * only should be used in {@link TypeFactory } so its visibility shold be minimized
 * according to that, to package-private.
 */
class FieldFactory {
    private FieldFactory() {
    }

    /**
     * Generate GraphQLFieldDefinition based on field and the required
     * {@link GQLField} annotation on it.
     */
    static @Nonnull GraphQLFieldDefinition GQLObjectFieldFrom(@Nonnull Field field) {
        TypeData<Field> data = TypeData.ofField(field);
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(data.getName())
                                     .type((GraphQLOutputType) data.getGraphQLType())
                                     .build();
    }

    /**
     * Generate GraphQLInputObjectField based on field and the required
     * {@link GQLField} annotation on it
     */
    static @Nonnull GraphQLInputObjectField GQLInputFieldFrom(@Nonnull Field field) {
        TypeData<Field> data = TypeData.ofField(field);
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(data.getName())
                                      .type((GraphQLInputType) data.getGraphQLType())
                                      .build();
    }
}
