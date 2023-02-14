package org.example.graphql.generator_component.factory_type;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLEnumValueDefinition;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLObjectType;
import org.example.graphql.annotation.QGLField;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TypeFactory {
    /**
     * Utility method to create GraphQLInputObjectType for provided Class type
     */
    public static @NotNull GraphQLInputObjectType graphQLInputObjectTypeFromClass(@NotNull Class<?> classType) {
        GraphQLInputObjectType.Builder inputObjectTypeBuilder = GraphQLInputObjectType.newInputObject().name(classType.getSimpleName());
        for (Field field : classType.getDeclaredFields()) {
            if (field.isAnnotationPresent(QGLField.class)) {
                inputObjectTypeBuilder.field(FieldFactory.GQLInputFieldFrom(field));
            }
        }
        return inputObjectTypeBuilder.build();
    }

    /**
     * Utility method to create GraphQLObjectType for provided Class type
     */
    public static @NotNull GraphQLObjectType graphQLObjectTypeFromClass(@NotNull Class<?> classType) {
        GraphQLObjectType.Builder objectTypeBuilder = GraphQLObjectType.newObject().name(classType.getSimpleName());
        for (Field field : classType.getDeclaredFields()) {
            if (field.isAnnotationPresent(QGLField.class)) {
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
                     .map(TypeFactory::graphQLEnumValueFrom)
                     .collect(Collectors.toList());
    }

    private static @NotNull GraphQLEnumValueDefinition graphQLEnumValueFrom(@NotNull Enum<?> enumConstant) {
        return GraphQLEnumValueDefinition.newEnumValueDefinition()
                                         .value(enumConstant.name())
                                         .name(enumConstant.name())
                                         .build();
    }
}
