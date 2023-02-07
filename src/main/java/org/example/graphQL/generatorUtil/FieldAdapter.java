package org.example.graphQL.generatorUtil;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphQL.annotation.ArgWith;
import org.example.graphQL.annotation.FieldOf;
import org.example.graphQL.annotation.FieldType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;

public class FieldAdapter {
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

    public static boolean isQueryField(Method method) {
        return Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(FieldOf.class);
    }

    public static boolean hasListReturnWithoutArg(Method method) {
        return method.getParameters().length == 0 && method.getAnnotation(FieldOf.class).type() == FieldType.LIST;
    }

    public static boolean hasObjectReturnByOneArg(Method method) {
        return method.getParameters().length == 1 &&
               method.getAnnotation(FieldOf.class).type() == FieldType.OBJECT &&
               method.getParameters()[0].isAnnotationPresent(ArgWith.class);
    }

    public static GraphQLFieldDefinition listReturnWithoutArg(Method method) {
        String typeName = genericTypeOf(method).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .build();
    }

    public static GraphQLFieldDefinition objectReturnByOneArg(Method method) {
        String type = method.getReturnType().getSimpleName();
        ArgWith annotation = method.getParameters()[0].getAnnotation(ArgWith.class);
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLTypeReference.typeRef(type))
                                     .argument(argumentFrom(annotation))
                                     .build();
    }

    private static GraphQLArgument argumentFrom(ArgWith annotation) {
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(annotation.type().graphQLScalarType)
                              .build();
    }

    public static Class<?> genericTypeOf(Method method) {
        return (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
    }

    public static Class<?> genericTypeOf(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }
}
