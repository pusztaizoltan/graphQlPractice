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
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static org.example.graphql.generator_component.factory_access.DataAccessFactory.createDataAccessFor;
import static org.example.graphql.generator_component.factory_access.FetcherFactory.createFetcherFor;
import static org.example.graphql.generator_component.factory_type.TypeFactory.*;

public class GraphQLBuilder {
    private final GraphQLSchema.Builder graphQLSchema = GraphQLSchema.newSchema();
    private final GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
    private final GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("Query");
    private final GraphQLObjectType.Builder mutationType = GraphQLObjectType.newObject().name("Mutation");

    /**
     * Finalize the building process
     */
    public @NotNull GraphQLSchema build() {
        this.graphQLSchema.query(queryType.build());
        this.graphQLSchema.mutation(mutationType.build());
        this.graphQLSchema.codeRegistry(registry.build());
        return this.graphQLSchema.build();
    }

    public void addDataAccessFieldForMethod(Method method, Object dataService) {
        GraphQLFieldDefinition accessField = createDataAccessFor(method);
        DataFetcher<?> fetcher = createFetcherFor(method, dataService);
        String typeCoordinate;
        if (method.isAnnotationPresent(GQLQuery.class)) {
            typeCoordinate = "Query";
            this.queryType.field(accessField);
        } else {
            typeCoordinate = "Mutation";
            this.mutationType.field(accessField);
        }
        this.registry.dataFetcher(FieldCoordinates.coordinates(typeCoordinate, method.getName()), fetcher);
    }
    /**
     * Scans the dataService instance for methods that can be paired with GraphQl Query fields,
     * and if it finds one add it to the SchemaBuilder as GraphQLFieldDefinition and to the RegistryBuilder
     */
    /**
     * Scans the dataService instance for methods that can be paired with GraphQl Mutation fields,
     * and if it finds one add it to the SchemaBuilder as GraphQLFieldDefinition and to the RegistryBuilder
     */
    /**
     * Scans tha argument Class types and add them to the SchemaBuilder as GraphQLFieldDefinition
     * and to the RegistryBuilder
     */
    public void addTypesForComponentClasses(@NotNull Set<Class<?>> components) {
        // todo try to reorganize later considering there is input and object types too
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

    private void addInputType(@NotNull Class<?> component) {
        //todo use this way now, we will see if fetcher is needed;
        this.graphQLSchema.additionalType(graphQLInputObjectTypeFromClass(component));
    }

    private void addEnumType(@NotNull Class<Enum<?>> component) {
        this.graphQLSchema.additionalType(graphQLEnumTypeFromEnum(component));
    }

    private void addObjectType(@NotNull Class<?> component) {
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
