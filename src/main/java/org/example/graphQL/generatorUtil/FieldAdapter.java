package org.example.graphQL.generatorUtil;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphQL.annotation.FieldOf;
import org.example.graphQL.annotation.FieldType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class FieldAdapter {
    /**
     * Generate GraphQLFieldDefinition based on field and the required
     * FieldOf annotation on it
     */
    public static GraphQLFieldDefinition graphQLFieldFrom(Field field) {
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

    private static GraphQLFieldDefinition scalarField(Field field) {
        GraphQLScalarType scalar = field.getAnnotation(FieldOf.class).type().graphQLScalarType;
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(scalar)
                                     .build();
    }

    private static GraphQLFieldDefinition listField(Field field) {
        String typeName = genericTypeOf(field).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .build();
    }

    private static GraphQLFieldDefinition objectField(Field field) {
        String type = field.getType().getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(GraphQLTypeReference.typeRef(type))
                                     .build();
    }

    private static GraphQLFieldDefinition enumField(Field field) {
        return objectField(field);
    }

    /**
     * Determine the Generic Type of afield
     */
    public static Class<?> genericTypeOf(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }
}
