package org.example.graphql.util_adapter;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLTypeReference;
import lombok.NonNull;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.FieldType;

import java.lang.reflect.Method;

public class MethodAdapter {
    /**
     * Test for potential signature specifications of GraphQl Query field
     */
    public static boolean hasListReturnWithoutArg(@NonNull Method method) {
        return method.getParameters().length == 0 && method.getAnnotation(FieldOf.class).type() == FieldType.LIST;
    }

    /**
     * Test for potential signature specifications of GraphQl Query field
     */
    public static boolean hasObjectReturnByOneArg(@NonNull Method method) {
        return method.getParameters().length == 1 &&
               method.getAnnotation(FieldOf.class).type() == FieldType.OBJECT &&
               method.getParameters()[0].isAnnotationPresent(ArgWith.class);
    }

    /**
     * Generate GraphQLFieldDefinition for a specific type of dataSource method
     */
    @NonNull
    public static GraphQLFieldDefinition listReturnWithoutArg(@NonNull Method method) {
        String typeName = ReflectionUtil.genericTypeOfMethod(method).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .build();
    }

    /**
     * Generate GraphQLFieldDefinition for a specific type of dataSource method
     */
    @NonNull
    public static GraphQLFieldDefinition objectReturnByOneArg(@NonNull Method method) {
        String type = method.getReturnType().getSimpleName();
        ArgWith annotation = method.getParameters()[0].getAnnotation(ArgWith.class);
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLTypeReference.typeRef(type))
                                     .argument(argumentFrom(annotation))
                                     .build();
    }
    @NonNull
    private static GraphQLArgument argumentFrom(@NonNull ArgWith annotation) {
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(annotation.type().graphQLScalarType)
                              .build();
    }
}
