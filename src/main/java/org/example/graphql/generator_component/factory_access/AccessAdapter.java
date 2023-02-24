package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import lombok.Getter;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLMutation;
import org.example.graphql.generator_component.util.Fetchable;
import org.example.graphql.generator_component.dataholder.DataFactory;
import org.example.graphql.generator_component.dataholder.Details;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AccessAdapter implements Fetchable {
    private static final String QUERY_NAME = "Query";
    private static final String MUTATION_NAME = "Mutation";
    private final GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
    @Getter
    private final GraphQLFieldDefinition accessField;
    @Getter
    private final boolean isMutation;

    public AccessAdapter(@Nonnull Method method, @Nonnull Object dataService) {
        isMutation = method.isAnnotationPresent(GQLMutation.class);
        this.accessField = createDataAccessorFor(method);
        DataFetcher<?> fetcher = FetcherFactory.createFetcherFor(method, dataService);
        this.registry.dataFetcher(FieldCoordinates.coordinates(getTypeName(), method.getName()), fetcher);
    }

    @Override
    public @Nonnull GraphQLCodeRegistry getRegistry() {
        return registry.build();
    }

    String getTypeName() {
        return isMutation ? MUTATION_NAME : QUERY_NAME;
    }

    public static @Nonnull GraphQLFieldDefinition createDataAccessorFor(@Nonnull Method method) {
        GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition().name(method.getName());
        Details<?, Method> methodData = DataFactory.detailOf(method);
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(GQLArg.class)) {
                Details<?, Parameter> parameterData = DataFactory.detailOf(parameter);
                builder.argument(createArgumentFor(parameterData));
            }
        }
        return builder.type((GraphQLOutputType) methodData.getGraphQLType()).build();
    }

    private static @Nonnull GraphQLArgument createArgumentFor(@Nonnull Details<?, Parameter> data) {
        return GraphQLArgument.newArgument()
                              .name(data.getName())
                              .type((GraphQLInputType) data.getGraphQLType())
                              .build();
    }
}
