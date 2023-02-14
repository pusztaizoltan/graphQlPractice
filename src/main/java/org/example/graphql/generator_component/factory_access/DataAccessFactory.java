package org.example.graphql.generator_component.factory_access;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.example.graphql.generator_component.util.ReflectionUtil.genericTypeOfMethod;

public class DataAccessFactory {
    public static @NotNull GraphQLFieldDefinition createDataAccessFor(@NotNull Method method) {
        GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition();
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(GQLArg.class)) {
                builder.argument(argumentFrom(parameter));
            }
        }
        return builder.name(method.getName()).type(returnTypeFrom(method)).build();
    }

    private static GraphQLOutputType returnTypeFrom(Method method) {
        GQLType returnType = GQLType.ofMethod(method);
        if (returnType.isScalar()) {
            return returnType.graphQLScalarType;
        } else if (returnType == GQLType.LIST) {
            String typeName = genericTypeOfMethod(method).getSimpleName();
            return GraphQLList.list(GraphQLTypeReference.typeRef(typeName));
        } else if (returnType == GQLType.OBJECT) {
            String typeName = method.getReturnType().getSimpleName();
            return GraphQLTypeReference.typeRef(typeName);
        } else {
            throw new RuntimeException("Not implemented output-type for Data-Access field of " + method);
        }
    }

    private static @NotNull GraphQLArgument argumentFrom(@NotNull Parameter parameter) {
        GQLArg annotation = parameter.getAnnotation(GQLArg.class);
        GQLType argumentType = annotation.type();
        if (argumentType.isScalar()) {
            return scalarArgument(parameter);
        } else if (argumentType == GQLType.ENUM) {
            return enumArgument(parameter);
        } else if (argumentType == GQLType.OBJECT) {
            return objectArgument(parameter);
        } else {
            throw new RuntimeException("(Unimplemented argument type for " + annotation.type());
        }
    }

    private static @NotNull GraphQLArgument scalarArgument(@NotNull Parameter parameter) {
        GQLArg annotation = parameter.getAnnotation(GQLArg.class);
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(annotation.type().graphQLScalarType)
                              .build();
    }

    private static @NotNull GraphQLArgument enumArgument(@NotNull Parameter parameter) {
        GQLArg annotation = parameter.getAnnotation(GQLArg.class);
        String typeName = parameter.getType().getSimpleName();
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(GraphQLTypeReference.typeRef(typeName))
                              .build();
    }

    private static @NotNull GraphQLArgument objectArgument(@NotNull Parameter parameter) {
        GQLArg annotation = parameter.getAnnotation(GQLArg.class);
        String typeName = parameter.getType().getSimpleName();
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(GraphQLTypeReference.typeRef(typeName))
                              .build();
    }
}
