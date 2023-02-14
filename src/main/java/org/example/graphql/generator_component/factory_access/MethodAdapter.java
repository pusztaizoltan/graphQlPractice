package org.example.graphql.generator_component.factory_access;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.Mutation;
import org.example.graphql.generator_component.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

import static org.example.graphql.generator_component.factory_access.ArgumentFactory.argumentFrom;

public class MethodAdapter {

    /**
     * Generate GraphQLFieldDefinition for a dataSource method based on the detected method signature
     */
    public static @NotNull GraphQLFieldDefinition createFieldFromMethod(@NotNull Method method) {
        if (hasListReturnWithoutArg(method)) {
            return listReturnWithoutArg(method);
        } else if (hasObjectReturnByOneArg(method)) {
            return objectReturnByOneArg(method);
        } else if (hasListReturnByOneArg(method)) {
            return listReturnByOneArg(method);
        } else if (hasScalarReturnByOneObject(method)) {
            return scalarReturnByOneObject(method);
        } else {
            throw new RuntimeException("Not implemented type of Query field for " + method);
        }
    }

    private static boolean hasListReturnWithoutArg(@NotNull Method method) {
        return method.getParameters().length == 0 &&
               method.isAnnotationPresent(FieldOf.class) &&
               method.getAnnotation(FieldOf.class).type() == GQLType.LIST;
    }

    private static boolean hasObjectReturnByOneArg(@NotNull Method method) {
        return method.getParameters().length == 1 &&
               method.isAnnotationPresent(FieldOf.class) &&
               method.getAnnotation(FieldOf.class).type() == GQLType.OBJECT &&
               method.getParameters()[0].isAnnotationPresent(ArgWith.class);
    }

    private static boolean hasListReturnByOneArg(@NotNull Method method) {
        return method.getParameters().length == 1 &&
               method.isAnnotationPresent(FieldOf.class) &&
               method.getAnnotation(FieldOf.class).type() == GQLType.LIST &&
               method.getParameters()[0].isAnnotationPresent(ArgWith.class);
    }

    private static @NotNull GraphQLFieldDefinition listReturnWithoutArg(@NotNull Method method) {
        String typeName = ReflectionUtil.genericTypeOfMethod(method).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .build();
    }

    private static @NotNull GraphQLFieldDefinition listReturnByOneArg(@NotNull Method method) {
        String typeName = ReflectionUtil.genericTypeOfMethod(method).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .argument(argumentFrom(method))
                                     .build();
    }

    private static @NotNull GraphQLFieldDefinition objectReturnByOneArg(@NotNull Method method) {
        String typeName = method.getReturnType().getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLTypeReference.typeRef(typeName))
                                     .argument(argumentFrom(method))
                                     .build();
    }

    private static boolean hasScalarReturnByOneObject(@NotNull Method method) {
        return method.getParameters().length == 1 &&
               method.isAnnotationPresent(Mutation.class) &&
               method.getAnnotation(Mutation.class).type().isScalar() &&
               method.getParameters()[0].isAnnotationPresent(ArgWith.class) &&
               method.getParameters()[0].getAnnotation(ArgWith.class).type() == GQLType.OBJECT;
    }

    private static @NotNull GraphQLFieldDefinition scalarReturnByOneObject(@NotNull Method method) {
        GQLType gqlType = method.getAnnotation(Mutation.class).type();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(gqlType.graphQLScalarType)
                                     .argument(argumentFrom(method))
                                     .build();
    }
}
