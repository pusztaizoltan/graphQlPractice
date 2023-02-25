package org.example.graphql.generator_component;

import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLMutation;
import org.example.graphql.generator_component.factory_access.AccessAdapter;
import org.example.graphql.generator_component.type_adapter.AbstractTypeAdapter;
import org.example.graphql.generator_component.util.Fetchable;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Class responsible for generating SchemaComponents based on provided Classes and
 * data-service methods.
 */
public class GraphQLBuilder {
    private static final String QUERY_NAME = "Query";
    private static final String MUTATION_NAME = "Mutation";
    private final GraphQLSchema.Builder graphQLSchema = GraphQLSchema.newSchema();
    private final GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
    private final GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name(QUERY_NAME);
    private final GraphQLObjectType.Builder mutationType = GraphQLObjectType.newObject().name(MUTATION_NAME);
    AccessAdapter adapter;

    public GraphQLBuilder(Object dataService) {
        adapter = new AccessAdapter(dataService);

    }

    /**
     * Responsible for populating the Mutation and Query entry points of the Schema
     * with fields each wired to the respective method of the data-service.
     */
    public void addDataAccessFieldForMethod(@Nonnull Method method) {
        if (method.isAnnotationPresent(GQLMutation.class)) {
            this.mutationType.field(adapter.getAccessorOf(method));
            this.registry.dataFetchers(adapter.getFetcherRegistry(method,MUTATION_NAME));
        } else {
            this.queryType.field(adapter.getAccessorOf(method));
            this.registry.dataFetchers(adapter.getFetcherRegistry(method,QUERY_NAME));
        }

    }

    public void addAdditionalTypes(Set<GraphQLType> additionalTypes) {
        this.graphQLSchema.additionalTypes(additionalTypes);
    }

    public void addFetchers(GraphQLCodeRegistry fetcherRegistry) {
        this.registry.dataFetchers( fetcherRegistry);
    }
    /**
     * Finalize the building process
     */
    public @Nonnull GraphQLSchema build() {
        this.graphQLSchema.query(queryType.build());
        this.graphQLSchema.mutation(mutationType.build());
        this.graphQLSchema.codeRegistry(registry.build());
        return this.graphQLSchema.build();
    }


}
