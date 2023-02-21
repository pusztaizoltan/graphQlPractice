package org.example.graphql.generator_component;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.example.graphql.annotation.GQLQuery;
import org.example.graphql.generator_component.factory_access.DataAccessFactory;
import org.example.graphql.generator_component.factory_access.FetcherFactory;
import org.example.graphql.generator_component.factory_type.TypeFactory;
import org.example.graphql.generator_component.factory_type.type_converters.Fetchable;
import org.example.graphql.generator_component.factory_type.type_converters.ConverterAbstract;

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
        GraphQLFieldDefinition accessField = DataAccessFactory.createDataAccessorFor(method);
        DataFetcher<?> fetcher = FetcherFactory.createFetcherFor(method, dataService);
        if (method.isAnnotationPresent(GQLQuery.class)) {
            this.queryType.field(accessField);
            this.registry.dataFetcher(FieldCoordinates.coordinates(QUERY_NAME, method.getName()), fetcher);
        } else {
            this.mutationType.field(accessField);
            this.registry.dataFetcher(FieldCoordinates.coordinates(MUTATION_NAME, method.getName()), fetcher);
        }
    }

    /**
     * Scans tha argument Class types and add them to the SchemaBuilder and to the RegistryBuilder
     * as GraphQLObjectType, GraphQLEnumType or GraphQLOInputObjectType, using the methods
     * of {@link org.example.graphql.generator_component.factory_type.TypeFactory}
     */
    public void addTypesForComponentClasses(@Nonnull Set<Class<?>> components) {
        for (Class<?> component : components) {
            ConverterAbstract<?> converter = TypeFactory.getTypeConverter(component);
            this.graphQLSchema.additionalType(converter.getGraphQLType());
            if (converter.isFetchable()) {
                this.registry.dataFetchers(((Fetchable) converter).getRegistry());
            }
        }
    }
}
