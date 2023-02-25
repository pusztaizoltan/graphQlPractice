package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLMutation;
import org.example.graphql.generator_component.dataholder.DataFactory;
import org.example.graphql.generator_component.dataholder.Details;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AccessAdapter{
    private static final String QUERY_NAME = "Query";
    private static final String MUTATION_NAME = "Mutation";
    private final boolean isMutation;
    Object dataService;

    public AccessAdapter(@Nonnull Method method, @Nonnull Object dataService) {
        this.dataService = dataService;
        isMutation = method.isAnnotationPresent(GQLMutation.class);
    }


    public @Nonnull GraphQLCodeRegistry getFetcherRegistry(Method method) {
        GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
        DataFetcher<?> fetcher = FetcherFactory.createFetcherFor(method, dataService);
        registry.dataFetcher(FieldCoordinates.coordinates(getTypeName(), method.getName()), fetcher);

        return registry.build();
    }

    String getTypeName() {
        return isMutation ? MUTATION_NAME : QUERY_NAME;
    }

    public boolean isMutation() {
        return isMutation;
    }

    public @Nonnull GraphQLFieldDefinition getAccessorOf(@Nonnull Method method) {
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

    private @Nonnull GraphQLArgument createArgumentFor(@Nonnull Details<?, Parameter> data) {
        return GraphQLArgument.newArgument()
                              .name(data.getName())
                              .type((GraphQLInputType) data.getGraphQLType())
                              .build();
    }
}
