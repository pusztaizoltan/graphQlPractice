package org.example.graphql.generator_component.factory_type;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.generator_component.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * Used to create {@link GraphQLFieldDefinition} and {@link GraphQLInputObjectField),
 * only should be used in {@link TypeFactory } so its visibility shold be minimized
 * according to that, to package-private.
 */
class FieldFactory {
    /**
     * Generate GraphQLFieldDefinition based on field and the required
     * FieldOf annotation on it
     */
    static @NotNull GraphQLFieldDefinition GQLObjectFieldFrom(@NotNull Field field) {
        if (!field.isAnnotationPresent(FieldOf.class)) {
            throw new RuntimeException("Parsing attempt of unannotated field:" + field);
        }
        FieldOf fieldOf = field.getAnnotation(FieldOf.class);
        if (fieldOf.type().isScalar()) {
            return scalarObjectField(field);
        } else if (fieldOf.type() == GQLType.OBJECT) {
            return objectObjectField(field);
        } else if (fieldOf.type() == GQLType.LIST) {
            return listObjectField(field);
        } else if (fieldOf.type() == GQLType.ENUM) {
            return enumObjectField(field);
        } else {
            throw new RuntimeException("Unimplemented fieldAdapter for " + fieldOf);
        }
    }

    /**
     * Generate GraphQLInputObjectField based on field and the required
     * FieldOf annotation on it
     */
    static @NotNull GraphQLInputObjectField GQLInputFieldFrom(@NotNull Field field) {
        if (!field.isAnnotationPresent(FieldOf.class)) {
            throw new RuntimeException("Parsing attempt of unannotated field:" + field);
        }
        FieldOf fieldOf = field.getAnnotation(FieldOf.class);
        if (fieldOf.type().isScalar()) {
            return scalarInputField(field);
        } else if (fieldOf.type() == GQLType.OBJECT) {
            return objectInputField(field);
        } else if (fieldOf.type() == GQLType.LIST) {
            return listInputField(field);
        } else if (fieldOf.type() == GQLType.ENUM) {
            return enumInputField(field);
        } else {
            throw new RuntimeException("Unimplemented fieldAdapter for " + fieldOf);
        }
    }

    private static @NotNull GraphQLFieldDefinition scalarObjectField(@NotNull Field field) {
        GraphQLScalarType scalar = field.getAnnotation(FieldOf.class).type().graphQLScalarType;
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(scalar)
                                     .build();
    }

    private static @NotNull GraphQLFieldDefinition listObjectField(@NotNull Field field) {
        String typeName = ReflectionUtil.genericTypeOfField(field).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .build();
    }

    private static @NotNull GraphQLFieldDefinition objectObjectField(@NotNull Field field) {
        String type = field.getType().getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(GraphQLTypeReference.typeRef(type))
                                     .build();
    }

    private static @NotNull GraphQLFieldDefinition enumObjectField(@NotNull Field field) {
        return objectObjectField(field);
    }

    private static @NotNull GraphQLInputObjectField scalarInputField(@NotNull Field field) {
        GraphQLScalarType scalar = field.getAnnotation(FieldOf.class).type().graphQLScalarType;
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(field.getName())
                                      .type(scalar)
                                      .build();
    }

    private static @NotNull GraphQLInputObjectField listInputField(@NotNull Field field) {
        String typeName = ReflectionUtil.genericTypeOfField(field).getSimpleName();
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(field.getName())
                                      .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                      .build();
    }

    private static @NotNull GraphQLInputObjectField objectInputField(@NotNull Field field) {
        String type = field.getType().getSimpleName();
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(field.getName())
                                      .type(GraphQLTypeReference.typeRef(type))
                                      .build();
    }

    private static @NotNull GraphQLInputObjectField enumInputField(@NotNull Field field) {
        return objectInputField(field);
    }
}
