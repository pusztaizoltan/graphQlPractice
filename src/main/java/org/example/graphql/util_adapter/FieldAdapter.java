package org.example.graphql.util_adapter;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.FieldType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class FieldAdapter {
    /**
     * Generate GraphQLFieldDefinition based on field and the required
     * FieldOf annotation on it
     */
    public static @NotNull GraphQLFieldDefinition graphQLFieldFrom(@NotNull Field field) {
        if (!field.isAnnotationPresent(FieldOf.class)) {
            throw new RuntimeException("Parsing attempt of unannotated field:" + field);
        }
        FieldOf fieldOf = field.getAnnotation(FieldOf.class);
        if (fieldOf.type().isScalar()) {
            return scalarField(field);
        } else if (fieldOf.type() == FieldType.OBJECT) {
            return objectField(field);
        } else if (fieldOf.type() == FieldType.LIST) {
            return listField(field);
        } else if (fieldOf.type() == FieldType.ENUM) {
            return enumField(field);
        } else {
            throw new RuntimeException("Unimplemented fieldAdapter for " + fieldOf);
        }
    }

    private static @NotNull GraphQLFieldDefinition scalarField(@NotNull Field field) {
        GraphQLScalarType scalar = field.getAnnotation(FieldOf.class).type().graphQLScalarType;
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(scalar)
                                     .build();
    }

    private static @NotNull GraphQLFieldDefinition listField(@NotNull Field field) {
        String typeName = ReflectionUtil.genericTypeOfField(field).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .build();
    }

    private static @NotNull GraphQLFieldDefinition objectField(@NotNull Field field) {
        String type = field.getType().getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(GraphQLTypeReference.typeRef(type))
                                     .build();
    }

    private static @NotNull GraphQLFieldDefinition enumField(@NotNull Field field) {
        return objectField(field);
    }
}
