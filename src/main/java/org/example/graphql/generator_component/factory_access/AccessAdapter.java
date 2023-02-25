package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.generator_component.dataholder.TypeFactory;
import org.example.graphql.generator_component.dataholder.TypeDetail;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AccessAdapter {
    FetcherFactory fetcherFactory;

    public AccessAdapter(@Nonnull Object dataService) {
        fetcherFactory = new FetcherFactory(dataService);

    }

    public @Nonnull GraphQLCodeRegistry getFetcherRegistry(Method method, String typeName) {
        GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
        DataFetcher<?> fetcher = fetcherFactory.createFetcherFor(method);
        registry.dataFetcher(FieldCoordinates.coordinates(typeName, method.getName()), fetcher);
        return registry.build();
    }

    public @Nonnull GraphQLFieldDefinition getAccessorOf(@Nonnull Method method) {
        GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition().name(method.getName());
        TypeDetail<?, Method> methodData = TypeFactory.contentOf(method);
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(GQLArg.class)) {
                TypeDetail<?, Parameter> parameterData = TypeFactory.contentOf(parameter);
                builder.argument(createArgumentFor(parameterData));
            }
        }
        return builder.type((GraphQLOutputType) methodData.getGraphQLType()).build();
    }

    private @Nonnull GraphQLArgument createArgumentFor(@Nonnull TypeDetail<?, Parameter> data) {
        return GraphQLArgument.newArgument()
                              .name(data.getName())
                              .type((GraphQLInputType) data.getGraphQLType())
                              .build();
    }
}
