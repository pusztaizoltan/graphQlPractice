package org.example.graphql.generator_component.factory_access;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.generator_component.dataholder.TypeFactory;
import org.example.graphql.generator_component.dataholder.TypeDetail;

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
        GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition().name(method.getName());
        TypeDetail<?, Method> methodData = TypeFactory.detailOf(method);
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(GQLArg.class)) {
                TypeDetail<?, Parameter> parameterData = TypeFactory.detailOf(parameter);
                builder.argument(createArgumentFor(parameterData));
            }
        }
        return builder.type((GraphQLOutputType) methodData.getGraphQLType()).build();
    }

    private static @Nonnull GraphQLArgument createArgumentFor(@Nonnull TypeDetail<?, Parameter> data) {
        return GraphQLArgument.newArgument()
                              .name(data.getName())
                              .type((GraphQLInputType) data.getGraphQLType())
                              .build();
    }
}
