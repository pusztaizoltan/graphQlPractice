package org.example.graphql.util_adapter;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.FieldType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

// TODO: missing class level javadoc
public class MethodAdapter {
    /**
     * Test for potential signature specifications of GraphQl Query field
     * // TODO: the method level javadocs are more helpful if they describe the intention in the first place,
     * // then the implementation details. In this case I could imagine something like
     * // "Checks if the method should be included into the schema as a list field"
     * // then the rules also can be detailed
     */
    public static boolean hasListReturnWithoutArg(@NotNull Method method) {
        return method.getParameters().length == 0 && method.getAnnotation(FieldOf.class).type() == FieldType.LIST;
    }

    /**
     * Test for potential signature specifications of GraphQl Query field
     */
    public static boolean hasObjectReturnByOneArg(@NotNull Method method) {
        return method.getParameters().length == 1 &&
               method.getAnnotation(FieldOf.class).type() == FieldType.OBJECT &&
               method.getParameters()[0].isAnnotationPresent(ArgWith.class);
    }

    /**
     * Generate GraphQLFieldDefinition for a specific type of dataSource method
     */
    public static @NotNull GraphQLFieldDefinition listReturnWithoutArg(@NotNull Method method) {
        String typeName = ReflectionUtil.genericTypeOfMethod(method).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .build();
    }

    /**
     * Generate GraphQLFieldDefinition for a specific type of dataSource method
     */
    public static @NotNull GraphQLFieldDefinition objectReturnByOneArg(@NotNull Method method) {
        String type = method.getReturnType().getSimpleName();
        ArgWith annotation = method.getParameters()[0].getAnnotation(ArgWith.class);
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLTypeReference.typeRef(type))
                                     .argument(argumentFrom(annotation))
                                     .build();
    }

    private static @NotNull GraphQLArgument argumentFrom(@NotNull ArgWith annotation) {
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(annotation.type().graphQLScalarType)
                              .build();
    }
}
