package org.example.graphql.util_generator;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static org.example.graphql.util_adapter.MethodAdapter.createFetcherFor;
import static org.example.graphql.util_adapter.MethodAdapter.createFieldFromMethod;
import static org.example.graphql.util_adapter.ReflectionUtil.*;
import static org.example.graphql.util_adapter.TypeFactory.*;

public class GraphQLBuilder {
    private final GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
    private final GraphQLSchema.Builder graphQLSchema = GraphQLSchema.newSchema();

    /**
     * Finalize the building process
     */
    public @NotNull GraphQLSchema build() {
        return this.graphQLSchema.codeRegistry(this.registry.build()).build();
    }

    /**
     * Scans the dataService instance for methods that can be paired with GraphQl Query fields,
     * and if it finds one add it to the SchemaBuilder as GraphQLFieldDefinition and to the RegistryBuilder
     */
    public void addQueryForDataService(@NotNull Object dataService) {
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("Query");
        for (Method method : queryMethodsOf(dataService)) {
            queryType.field(createFieldFromMethod(method));
            DataFetcher<?> fetcher = createFetcherFor(method, dataService);
            this.registry.dataFetcher(FieldCoordinates.coordinates("Query", method.getName()), fetcher);
        }
        this.graphQLSchema.query(queryType);
    }

    /**
     * Scans the dataService instance for methods that can be paired with GraphQl Mutation fields,
     * and if it finds one add it to the SchemaBuilder as GraphQLFieldDefinition and to the RegistryBuilder
     */
    public void addMutationForDataService(@NotNull Object dataService) {
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("Mutation");
        for (Method method : mutationMethodsOf(dataService)) {
            queryType.field(createFieldFromMethod(method)); //
            DataFetcher<?> fetcher = createFetcherFor(method, dataService);
            this.registry.dataFetcher(FieldCoordinates.coordinates("Mutation", method.getName()), fetcher);
        }
        this.graphQLSchema.mutation(queryType);
    }

    /**
     * Scans tha argument Class types and add them to the SchemaBuilder as GraphQLFieldDefinition
     * and to the RegistryBuilder
     */
    public void addTypesForComponentClasses(@NotNull Set<Class<?>> components) {
        // todo try to reorganize later considering there is input and object types too
        for (Class<?> component : components) {
            if (component.getAnnotation(TypeOf.class).type() == GQLType.INPUT) {
                addOInputType(component);
            } else if (component.isEnum()) {
                addEnumType((Class<Enum<?>>) component);
            } else {
                addObjectType(component);
            }
        }
    }

    private void addOInputType(@NotNull Class<?> component) {
        //todo use this way now, we will see if fetcher is needed;
        this.graphQLSchema.additionalType(graphQLInputObjectTypeFromClass(component));
    }

    private void addEnumType(@NotNull Class<Enum<?>> component) {
        this.graphQLSchema.additionalType(graphQLEnumTypeFromEnum(component));
    }

    private void addObjectType(@NotNull Class<?> component) {
        String typeName = component.getSimpleName();
        for (Field field : typeFieldsOf(component)) {
            Class<?> fieldType = field.getType();
            String fieldName = fieldType.getSimpleName();
            DataFetcher<?> fetcher = env -> fieldType.cast(field.get(component.cast(env.getSource())));
            this.registry.dataFetcher(FieldCoordinates.coordinates(typeName, fieldName), fetcher);
        }
        this.graphQLSchema.additionalType(graphQLObjectTypeFromClass(component));
    }
}
