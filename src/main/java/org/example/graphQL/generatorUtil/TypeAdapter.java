package org.example.graphQL.generatorUtil;

import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLEnumValueDefinition;
import graphql.schema.GraphQLObjectType;
import org.example.graphQL.annotation.FieldOf;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TypeAdapter {
    // todo OK
    public static GraphQLObjectType graphQLObjectTypeFromClass(Class<?> classType) {
        GraphQLObjectType.Builder objectTypeBuilder = GraphQLObjectType.newObject().name(classType.getSimpleName());
        for (Field field : classType.getDeclaredFields()) {
            if (field.isAnnotationPresent(FieldOf.class)) {
                objectTypeBuilder.field(FieldAdapter.graphQLFieldFrom(field));
            }
        }
        return objectTypeBuilder.build();
    }

    public static GraphQLEnumType graphQLEnumTypeFromEnum(Class<? extends Enum<?>> enumType) {
        return GraphQLEnumType.newEnum()
                              .name(enumType.getSimpleName())
                              .values(graphQLEnumValues(enumType))
                              .build();
    }

    private static List<GraphQLEnumValueDefinition> graphQLEnumValues(Class<? extends Enum<?>> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
                     .map(TypeAdapter::graphQLEnumValueFrom)
                     .collect(Collectors.toList());
    }

    private static GraphQLEnumValueDefinition graphQLEnumValueFrom(Enum<?> enumConstant) {
        return GraphQLEnumValueDefinition.newEnumValueDefinition()
                                         .value(enumConstant.name())
                                         .name(enumConstant.name())
                                         .build();
    }
}
