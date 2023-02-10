package org.example.graphql.util_adapter;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLEnumValueDefinition;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLNamedSchemaElement;
import graphql.schema.GraphQLObjectType;
import org.example.graphql.annotation.FieldOf;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

public class TypeAdapter {
//    GraphQLInputObjectType graphQLiNPUTObjectTypeFromClass(@NotNull Class<?> classType) {
//        GraphQLInputObjectType.Builder inputObjectTypeBuilder = GraphQLInputObjectType.newInputObject().name(classType.getSimpleName());
//        for (Field field : classType.getDeclaredFields()) {
//            if (field.isAnnotationPresent(FieldOf.class)) {
//                inputObjectTypeBuilder.field();
////                inputObjectTypeBuilder.field(FieldFactory.GQLObjectFieldFrom(field));
//            }
//        }
//        return inputObjectTypeBuilder.build();
//    }
//    {
//        var a1 =  GraphQLInputObjectField.newInputObjectField();
//        var a2 = GraphQLFieldDefinition.newFieldDefinition();
//        GraphQLNamedSchemaElement a11 = a1.build();
//        GraphQLNamedSchemaElement a22 = a2.build();
//
//        GraphQLInputObjectType inputObjectType = newInputObject()
//                .name("inputObjectType")
//                .field(GraphQLInputObjectField.newInputObjectField()
//                                              .name("field")
//                                              .type(GraphQLString))
//                .build();
//    }
    /**
     * Utility method to create GraphQLObjectType for provided Class type
     */
    public static @NotNull GraphQLObjectType graphQLObjectTypeFromClass(@NotNull Class<?> classType) {
        GraphQLObjectType.Builder objectTypeBuilder = GraphQLObjectType.newObject().name(classType.getSimpleName());
        for (Field field : classType.getDeclaredFields()) {
            if (field.isAnnotationPresent(FieldOf.class)) {
                objectTypeBuilder.field(FieldFactory.GQLObjectFieldFrom(field));
            }
        }
        return objectTypeBuilder.build();
    }

    /**
     * Utility method to create GraphQLEnumType for provided Enum type
     */
    public static @NotNull GraphQLEnumType graphQLEnumTypeFromEnum(@NotNull Class<Enum<?>> enumType) {
        return GraphQLEnumType.newEnum()
                              .name(enumType.getSimpleName())
                              .values(graphQLEnumValues(enumType))
                              .build();
    }

    private static @NotNull List<GraphQLEnumValueDefinition> graphQLEnumValues(@NotNull Class<Enum<?>> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
                     .map(TypeAdapter::graphQLEnumValueFrom)
                     .collect(Collectors.toList());
    }

    private static @NotNull GraphQLEnumValueDefinition graphQLEnumValueFrom(@NotNull Enum<?> enumConstant) {
        return GraphQLEnumValueDefinition.newEnumValueDefinition()
                                         .value(enumConstant.name())
                                         .name(enumConstant.name())
                                         .build();
    }
}
