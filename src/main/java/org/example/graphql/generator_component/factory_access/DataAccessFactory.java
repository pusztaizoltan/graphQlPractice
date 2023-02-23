package org.example.graphql.generator_component.factory_access;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.generator_component.util.TypeData;
import org.example.graphql.generator_component.util.UnimplementedException;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
// TODO: missing class level javadoc
// todo done also in other Classes

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
    // todo done also javadoc are rewrote

    private DataAccessFactory() {
    }

    /**
     * Only entry point of the class that generate the GraphQLFieldDefinition for Mutation or Query.
     * to determine the particulars of the returned object (argument, type) it implicitly uses
     * the static factory methods of the class
     */
    public static @Nonnull GraphQLFieldDefinition createDataAccessorFor(@Nonnull Method method) {
        GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition();
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(GQLArg.class)) {
                builder.argument(createArgumentFor(parameter));
            }
        }
        return builder.name(method.getName()).type(returnTypeFrom(method)).build();
    }

    private static @Nonnull GraphQLArgument createArgumentFor(@Nonnull Parameter parameter) {
        GQLArg annotation = parameter.getAnnotation(GQLArg.class);
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(argumentTypeFrom(parameter))
                              .build();
    }

    private static @Nonnull GraphQLOutputType returnTypeFrom(@Nonnull Method method) {
        TypeData data = TypeData.ofMethod(method);
        if (data.isScalar()) {
            return (GraphQLOutputType) data.getScalarType();
        } else if (data.isEnum()) {
            return GraphQLTypeReference.typeRef(data.getContentType().getSimpleName());
        } else if (data.isList()) {
            if (data.hasScalarContent()) {
                return GraphQLList.list(data.getScalarType());
            } else {
                return GraphQLList.list(GraphQLTypeReference.typeRef(data.getContentType().getSimpleName()));
            }
        } else if (data.isArray()) {
            if (data.hasScalarContent()) {
                return GraphQLList.list(data.getScalarType());
            } else {
                return GraphQLList.list(GraphQLTypeReference.typeRef(data.getContentType().getSimpleName()));
            }
        } else if (data.isObject()) {
            return GraphQLTypeReference.typeRef(data.getContentType().getSimpleName());
        } else {
            throw new UnimplementedException("Not implemented output-type for Data-Access field of " + method);
        }
    }

    private static @Nonnull GraphQLInputType argumentTypeFrom(@Nonnull Parameter parameter) {
        TypeData data = TypeData.ofParameter(parameter);
        if (data.isScalar()) {
            return (GraphQLInputType) data.getScalarType();
        } else if (data.isEnum()) {
            return GraphQLTypeReference.typeRef(data.getContentType().getSimpleName());
        } else if (data.isObject()) {
            return GraphQLTypeReference.typeRef(data.getContentType().getSimpleName());
        } else if (data.isList()) {
            // todo problem if scalar return type in list
            if (data.hasScalarContent()) {
                return GraphQLList.list(data.getScalarType());
            } else {
                return GraphQLList.list(GraphQLTypeReference.typeRef(data.getContentType().getSimpleName()));
            }
        } else {
            throw new UnimplementedException("(Unimplemented argument type for " + parameter);
        }
    }
}
