package org.example.graphql.generator_component;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLInput;
import org.example.graphql.annotation.GQLQuery;
import org.example.graphql.generator_component.factory_access.DataAccessFactory;
import org.example.graphql.generator_component.factory_access.FetcherFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static org.example.graphql.generator_component.factory_type.TypeFactory.*;

/**
 * Class responsible for generating SchemaComponents based on provided Classes and
 * data-service methods.
 */
public class GraphQLBuilder {
    private final String queryName = "Query";
    private final String mutationName = "Mutation";
    private final GraphQLSchema.Builder graphQLSchema = GraphQLSchema.newSchema();
    private final GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
    private final GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name(queryName);
    private final GraphQLObjectType.Builder mutationType = GraphQLObjectType.newObject().name(mutationName);

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
            this.registry.dataFetcher(FieldCoordinates.coordinates(queryName, method.getName()), fetcher);
        } else {
            this.mutationType.field(accessField);
            this.registry.dataFetcher(FieldCoordinates.coordinates(mutationName, method.getName()), fetcher);
        }
    }

    /**
     * Scans tha argument Class types and add them to the SchemaBuilder and to the RegistryBuilder
     * as GraphQLObjectType, GraphQLEnumType or GraphQLOInputObjectType, using the methods
     * of {@link org.example.graphql.generator_component.factory_type.TypeFactory}
     */
    public void addTypesForComponentClasses(@Nonnull Set<Class<?>> components) {
        for (Class<?> component : components) {
            if (component.isEnum()) {
                addEnumType((Class<Enum<?>>) component);
            } else if (component.isAnnotationPresent(GQLInput.class)) {
                addInputType(component);
            } else {
                addObjectType(component);
            }
        }
    }

    private void addInputType(@Nonnull Class<?> component) {
        this.graphQLSchema.additionalType(graphQLInputObjectTypeFromClass(component));
    }

    private void addEnumType(@Nonnull Class<Enum<?>> component) {
        this.graphQLSchema.additionalType(graphQLEnumTypeFromEnum(component));
    }

    private void addObjectType(@Nonnull Class<?> component) {
        String typeName = component.getSimpleName();
        for (Field field : component.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                Class<?> fieldType = field.getType();
                String fieldName = fieldType.getSimpleName();
                DataFetcher<?> fetcher = env -> fieldType.cast(field.get(component.cast(env.getSource())));
                this.registry.dataFetcher(FieldCoordinates.coordinates(typeName, fieldName), fetcher);
            }
        }
        this.graphQLSchema.additionalType(graphQLObjectTypeFromClass(component));
    }
}
