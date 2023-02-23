package org.example.graphql.generator_component.factory_type.type_converters;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.generator_component.factory_type.TypeFactory;
import org.example.graphql.generator_component.util.TypeData;
import org.example.graphql.generator_component.util.UnimplementedException;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

import static org.example.graphql.generator_component.util.ReflectionUtil.genericTypeOfField;

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
        TypeData data = TypeData.ofField(field);
        if (data.isScalar()) {
            return scalarObjectField(field);
        } else if (data.isObject()) {
            return objectObjectField(field);
        } else if (data.isList()) {
            return listObjectField(field);
        } else if (data.isEnum()) {
            return enumObjectField(field);
        } else {
            throw new UnimplementedException("Unimplemented fieldAdapter for " + field);
        }
    }

    /**
     * Generate GraphQLInputObjectField based on field and the required
     * {@link GQLField} annotation on it
     */
    static @Nonnull GraphQLInputObjectField GQLInputFieldFrom(@Nonnull Field field) {
        TypeData data = TypeData.ofField(field);
        if (data.isScalar()) {
            return scalarInputField(field);
        } else if (data.isObject()) {
            return objectInputField(field);
        } else if (data.isList()) {
            return listInputField(field);
        } else if (data.isEnum()) {
            return enumInputField(field);
        } else {
            throw new UnimplementedException("Unimplemented fieldAdapter for " + field);
        }
    }

    private static @Nonnull GraphQLFieldDefinition scalarObjectField(@Nonnull Field field) {
        TypeData data = TypeData.ofField(field);
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type((GraphQLOutputType) data.getScalarType())
                                     .build();
    }

    private static @Nonnull GraphQLFieldDefinition listObjectField(@Nonnull Field field) {
        String typeName = genericTypeOfField(field).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .build();
    }

    private static @Nonnull GraphQLFieldDefinition objectObjectField(@Nonnull Field field) {
        String type = field.getType().getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(GraphQLTypeReference.typeRef(type))
                                     .build();
    }

    private static @Nonnull GraphQLFieldDefinition enumObjectField(@Nonnull Field field) {
        return objectObjectField(field);
    }

    private static @Nonnull GraphQLInputObjectField scalarInputField(@Nonnull Field field) {
        TypeData data = TypeData.ofField(field);
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(field.getName())
                                      .type((GraphQLInputType) data.getScalarType())
                                      .build();
    }

    private static @Nonnull GraphQLInputObjectField listInputField(@Nonnull Field field) {
        String typeName = genericTypeOfField(field).getSimpleName();
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(field.getName())
                                      .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                      .build();
    }

    private static @Nonnull GraphQLInputObjectField objectInputField(@Nonnull Field field) {
        String type = field.getType().getSimpleName();
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(field.getName())
                                      .type(GraphQLTypeReference.typeRef(type))
                                      .build();
    }

    private static @Nonnull GraphQLInputObjectField enumInputField(@Nonnull Field field) {
        return objectInputField(field);
    }
}
