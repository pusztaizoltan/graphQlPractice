package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import lombok.Getter;
import org.example.graphql.annotation.GQLMutation;
import org.example.graphql.generator_component.util.Fetchable;
import org.example.graphql.generator_component.util.TypeData;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

public class ServiceConverter implements Fetchable {
    private static final String QUERY_NAME = "Query";
    private static final String MUTATION_NAME = "Mutation";
    private final GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
    @Getter
    private final GraphQLFieldDefinition accessField;
    @Getter
    private final boolean isMutation;

    public ServiceConverter(@Nonnull Method method, @Nonnull Object dataService) {
        isMutation = method.isAnnotationPresent(GQLMutation.class);
        this.accessField = DataAccessFactory.createDataAccessorFor(method);
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
}
