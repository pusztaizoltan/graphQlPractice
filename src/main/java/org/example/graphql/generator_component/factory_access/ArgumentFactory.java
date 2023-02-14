package org.example.graphql.generator_component.factory_access;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.GQLType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ArgumentFactory {

    static @NotNull GraphQLArgument argumentFrom(@NotNull Parameter parameter) {
        ArgWith annotation = parameter.getAnnotation(ArgWith.class);
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
        ArgWith annotation = parameter.getAnnotation(ArgWith.class);
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(annotation.type().graphQLScalarType)
                              .build();
    }

    private static @NotNull GraphQLArgument enumArgument(@NotNull Parameter parameter) {
        ArgWith annotation = parameter.getAnnotation(ArgWith.class);
        String typeName = parameter.getType().getSimpleName();
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(GraphQLTypeReference.typeRef(typeName))
                              .build();
    }

    private static @NotNull GraphQLArgument objectArgument(@NotNull Parameter parameter) {
        ArgWith annotation = parameter.getAnnotation(ArgWith.class);
        String typeName = parameter.getType().getSimpleName();
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(GraphQLTypeReference.typeRef(typeName))
                              .build();
    }

}
