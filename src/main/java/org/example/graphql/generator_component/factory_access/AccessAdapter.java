package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.generator_component.dataholder.DataFactory;
import org.example.graphql.generator_component.dataholder.Details;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AccessAdapter {
    Object dataService;

    public AccessAdapter(@Nonnull Object dataService) {
        this.dataService = dataService;
    }

    public @Nonnull GraphQLCodeRegistry getFetcherRegistry(Method method, String typeName) {
        GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
        DataFetcher<?> fetcher = FetcherFactory.createFetcherFor(method, dataService);
        registry.dataFetcher(FieldCoordinates.coordinates(typeName, method.getName()), fetcher);
        return registry.build();
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
