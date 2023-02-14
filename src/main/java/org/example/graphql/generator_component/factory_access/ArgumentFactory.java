package org.example.graphql.generator_component.factory_access;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.GQLType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public class ArgumentFactory {

    static @NotNull GraphQLArgument argumentFrom(@NotNull Method method) {
        ArgWith annotation = method.getParameters()[0].getAnnotation(ArgWith.class);
        if (annotation.type().isScalar()) {
            return scalarArgument(annotation);
        } else if (annotation.type() == GQLType.ENUM) {
            return enumArgument(method);
        } else if (annotation.type() == GQLType.OBJECT) {
            return objectArgument(method);
        } else {
            throw new RuntimeException("(Unimplemented argument type for " + annotation.type());
        }
    }

    private static @NotNull GraphQLArgument scalarArgument(@NotNull ArgWith annotation) {
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(annotation.type().graphQLScalarType)
                              .build();
    }

    private static @NotNull GraphQLArgument enumArgument(@NotNull Method method) {
        ArgWith annotation = method.getParameters()[0].getAnnotation(ArgWith.class);
        String typeName = method.getParameters()[0].getType().getSimpleName();
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(GraphQLTypeReference.typeRef(typeName))
                              .build();
    }

    private static @NotNull GraphQLArgument objectArgument(@NotNull Method method) {
        ArgWith annotation = method.getParameters()[0].getAnnotation(ArgWith.class);
        String typeName = method.getParameters()[0].getType().getSimpleName();
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(GraphQLTypeReference.typeRef(typeName))
                              .build();
    }

}
