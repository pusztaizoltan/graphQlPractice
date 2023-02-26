package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import org.example.graphql.annotation.GQLAccess;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.generator_component.dataholder.TypeDetail;
import org.example.graphql.generator_component.dataholder.TypeFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AccessAdapter {
    FetcherFactory fetcherFactory;

    public AccessAdapter(@Nonnull Object dataService) {
        fetcherFactory = new FetcherFactory(dataService);
    }

    public @Nonnull GraphQLCodeRegistry getFetcherRegistry(Method method) {
        String typeName = method.getAnnotation(GQLAccess.class).type().accessName;
        DataFetcher<?> fetcher = fetcherFactory.createFetcherFor(method);
        FieldCoordinates coordinates = FieldCoordinates.coordinates(typeName, method.getName());
        return GraphQLCodeRegistry.newCodeRegistry()
                                  .dataFetcher(coordinates, fetcher)
                                  .build();

    }

    public @Nonnull GraphQLFieldDefinition getAccessorOf(@Nonnull Method method) {
        GraphQLFieldDefinition.Builder accessBuilder = GraphQLFieldDefinition.newFieldDefinition();
        TypeDetail<?, Method> detail = TypeFactory.detailOf(method);
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(GQLArg.class)) {
                TypeDetail<?, Parameter> parameterData = TypeFactory.detailOf(parameter);
                accessBuilder.argument(createArgumentFor(parameterData));
            }
        }
        return accessBuilder.name(detail.getName())
                            .type((GraphQLOutputType) detail.getGraphQLType())
                            .build();
    }

    private @Nonnull GraphQLArgument createArgumentFor(@Nonnull TypeDetail<?, Parameter> data) {
        return GraphQLArgument.newArgument()
                              .name(data.getName())
                              .type((GraphQLInputType) data.getGraphQLType())
                              .build();
    }
}
