package org.example.graphql.generator_component;

import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLAccess;
import org.example.graphql.generator_component.dataholder.TypeFactory;
import org.example.graphql.generator_component.factory_access.AccessAdapter;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Class responsible for generating SchemaComponents based on provided Classes and
 * data-service methods.
 */
public class GraphQLBuilder {
    private final GraphQLSchema.Builder graphQLSchema = GraphQLSchema.newSchema();
    private final GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
    private final GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name(TypeFactory.AccessType.QUERY.accessName);
    private final GraphQLObjectType.Builder mutationType = GraphQLObjectType.newObject().name(TypeFactory.AccessType.MUTATION.accessName);
    private final AccessAdapter adapter;

    public GraphQLBuilder(Object dataService) {
        adapter = new AccessAdapter(dataService);
    }

    /**
     * Responsible for populating the Mutation and Query entry points of the Schema
     * with fields each wired to the respective method of the data-service.
     */
    public void addDataAccessFieldForMethod(@Nonnull Method method) {
        if (method.getAnnotation(GQLAccess.class).type() == TypeFactory.AccessType.MUTATION) {
            this.mutationType.field(adapter.getAccessorOf(method));
        } else {
            this.queryType.field(adapter.getAccessorOf(method));
        }
        this.registry.dataFetchers(adapter.getFetcherRegistry(method));
    }

    public void addAdditionalTypes(Set<GraphQLType> additionalTypes) {
        this.graphQLSchema.additionalTypes(additionalTypes);
    }

    public void addFetchers(GraphQLCodeRegistry fetcherRegistry) {
        this.registry.dataFetchers(fetcherRegistry);
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
