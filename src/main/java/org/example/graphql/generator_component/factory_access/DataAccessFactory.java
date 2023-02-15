package org.example.graphql.generator_component.factory_access;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLType;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.example.graphql.generator_component.util.ReflectionUtil.genericTypeOfMethod;

// TODO: missing class level javadoc

/**
 * Static Utility class used in{@link org.example.graphql.generator_component.GraphQLBuilder}
 * to automatically create GraphQLFieldDefinitions for the Query and Mutation type methods
 * of data-service based on method signature witch is processed by the class private factory
 * methods
 */
public class DataAccessFactory {
    // TODO: the method level javadocs are more helpful if they describe the intention in the first place,
    // then the implementation details. In this case I could imagine something like
    // "Checks if the method should be included into the schema as a list field"
    // then the rules also can be detailed

    /**
     * Only entry point of the class that generate the GraphQLFieldDefinition for Mutation or Query.
     * to determine the particulars of the returned object (argument, type) it implicitly uses
     * the static factory methods of the class
     */
    public static @Nonnull GraphQLFieldDefinition createDataAccessorFor(@Nonnull Method method) {
        GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition();
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(GQLArg.class)) {
                builder.argument(argumentFrom(parameter));
            }
        }
        return builder.name(method.getName()).type(returnTypeFrom(method)).build();
    }

    private static @Nonnull GraphQLOutputType returnTypeFrom(@Nonnull Method method) {
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

    private static @Nonnull GraphQLArgument argumentFrom(@Nonnull Parameter parameter) {
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

    private static @Nonnull GraphQLArgument scalarArgument(@Nonnull Parameter parameter) {
        GQLArg annotation = parameter.getAnnotation(GQLArg.class);
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(annotation.type().graphQLScalarType)
                              .build();
    }

    private static @Nonnull GraphQLArgument enumArgument(@Nonnull Parameter parameter) {
        GQLArg annotation = parameter.getAnnotation(GQLArg.class);
        String typeName = parameter.getType().getSimpleName();
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(GraphQLTypeReference.typeRef(typeName))
                              .build();
    }

    private static @Nonnull GraphQLArgument objectArgument(@Nonnull Parameter parameter) {
        GQLArg annotation = parameter.getAnnotation(GQLArg.class);
        String typeName = parameter.getType().getSimpleName();
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(GraphQLTypeReference.typeRef(typeName))
                              .build();
    }
}
