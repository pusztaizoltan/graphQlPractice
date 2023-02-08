package org.example.graphql.util_generator;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.example.graphql.annotation.ArgWith;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static org.example.graphql.util_adapter.MethodAdapter.*;
import static org.example.graphql.util_adapter.ReflectionUtil.queryMethodsOf;
import static org.example.graphql.util_adapter.ReflectionUtil.typeFieldsOf;
import static org.example.graphql.util_adapter.TypeAdapter.graphQLEnumTypeFromEnum;
import static org.example.graphql.util_adapter.TypeAdapter.graphQLObjectTypeFromClass;

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
            DataFetcher<?> fetcher;
            if (hasListReturnWithoutArg(method)) {
                queryType.field(listReturnWithoutArg(method));
                fetcher = env -> method.invoke(dataService);
            } else if (hasObjectReturnByOneArg(method)) {
                queryType.field(objectReturnByOneArg(method));
                String argName = method.getParameters()[0].getAnnotation(ArgWith.class).name();
                fetcher = env -> method.invoke(dataService, env.getArguments().get(argName));
            } else {
                throw new RuntimeException("Not implemented type of Query field for " + method);
            }
            this.registry.dataFetcher(FieldCoordinates.coordinates("Query", method.getName()), fetcher);
        }
        this.graphQLSchema.query(queryType);
    }

    /**
     * Scans tha argument Class types and add them to the SchemaBuilder as GraphQLFieldDefinition
     * and to the RegistryBuilder
     */
    public void addTypesForComponentClasses(@NotNull Set<Class<?>> components) {
        for (Class<?> component : components) {
            if (component.isEnum()) {
                addEnumType((Class<? extends Enum<?>>) component);
            } else {
                addObjectType(component);
            }
        }
    }

    private void addEnumType(@NotNull Class<? extends Enum<?>> component) {
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
