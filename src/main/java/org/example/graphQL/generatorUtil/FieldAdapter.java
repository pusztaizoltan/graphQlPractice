package org.example.graphQL.generatorUtil;

import graphql.Scalars;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphQL.annotation.FieldOf;
import org.example.graphQL.annotation.FieldType;
import org.example.graphQL.annotation.UseAsInt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public class FieldAdapter {
    // todo OK
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
        String type = ((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(type)))
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

    //--------------
    //todo public privat
    public static GraphQLFieldDefinition nestedReturn(Method method) {
        String type = ((Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(type)))
                                     .build();
    }

    public static GraphQLFieldDefinition argumentedReturn(Method method) {
        String type = method.getReturnType().getSimpleName();
        UseAsInt marker = method.getParameters()[0].getAnnotation(UseAsInt.class);
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLTypeReference.typeRef(type))
                                     .argument(GraphQLArgument.newArgument()
                                                              .name(marker.name())
                                                              .type(Scalars.GraphQLInt)
                                                              .build())
                                     .build();
    }
}
