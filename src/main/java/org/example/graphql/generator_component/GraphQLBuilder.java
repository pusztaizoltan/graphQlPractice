package org.example.graphql.generator_component;

import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.example.graphql.generator_component.factory_access.AccessAdapter;
import org.example.graphql.generator_component.type_adapter.AbstractTypeAdapter;
import org.example.graphql.generator_component.util.Fetchable;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
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

    /**
     * Finalize the building process
     */
    public @Nonnull GraphQLSchema build() {
        this.graphQLSchema.query(queryType.build());
        this.graphQLSchema.mutation(mutationType.build());
        this.graphQLSchema.codeRegistry(registry.build());
        return this.graphQLSchema.build();
    }

    /**
     * Responsible for populating the Mutation and Query entry points of the Schema
     * with fields each wired to the respective method of the data-service.
     */
    public void addDataAccessFieldForMethod(@Nonnull Method method, @Nonnull Object dataService) {
        AccessAdapter adapter = new AccessAdapter(method, dataService);
        if (adapter.isMutation()) {
            this.mutationType.field(adapter.getAccessField());
        } else {
            this.queryType.field(adapter.getAccessField());
        }
        this.registry.dataFetchers(adapter.getRegistry());
    }

    /**
     * Scans tha argument Class types and add them to the SchemaBuilder and to the RegistryBuilder
     * as GraphQLObjectType, GraphQLEnumType or GraphQLOInputObjectType, using the methods
     * of {@link }
     */
    public void addTypesForComponentClasses(@Nonnull Set<Class<?>> components) {
        for (Class<?> component : components) {
            AbstractTypeAdapter<?> adapter = AbstractTypeAdapter.adapterOf(component);
            this.graphQLSchema.additionalType(adapter.getGraphQLType());
            if (adapter.isFetchable()) {
                this.registry.dataFetchers(((Fetchable) adapter).getRegistry());
            }
        }
    }
}
