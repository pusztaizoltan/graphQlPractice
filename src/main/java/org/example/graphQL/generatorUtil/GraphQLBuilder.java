package org.example.graphQL.generatorUtil;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.example.graphQL.annotation.ArgWith;
import org.example.graphQL.annotation.FieldOf;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

public class GraphQLBuilder {
    private final GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
    private final GraphQLSchema.Builder graphQLSchema = GraphQLSchema.newSchema();

    /**
     * Finalize the building process
     */
    public GraphQLSchema build() {
        return this.graphQLSchema.codeRegistry(this.registry.build()).build();
    }

    /**
     * Scans the dataService instance for methods that can be paired with GraphQl Query fields,
     * and if finds one add it to the SchemaBuilder as GraphQLFieldDefinition and to the RegistryBuilder
     */
    public void addQueryForDataService(Object dataService) {
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("Query");
        for (Method method : dataService.getClass().getDeclaredMethods()) {
            if (MethodAdapter.isQueryField(method)) {
                if (MethodAdapter.hasListReturnWithoutArg(method)) {
                    queryType.field(MethodAdapter.listReturnWithoutArg(method));
                    DataFetcher<?> fetcher = (env) -> method.invoke(dataService);
                    registry.dataFetcher(FieldCoordinates.coordinates("Query", method.getName()), fetcher);
                } else if (MethodAdapter.hasObjectReturnByOneArg(method)) {
                    queryType.field(MethodAdapter.objectReturnByOneArg(method));
                    String argName = method.getParameters()[0].getAnnotation(ArgWith.class).name();
                    DataFetcher<?> fetcher = (env) -> method.invoke(dataService, env.getArguments().get(argName));
                    registry.dataFetcher(FieldCoordinates.coordinates("Query", method.getName()), fetcher);
                } else {
                    throw new RuntimeException("Not implemented type of Query field");
                }
            }
        }
        graphQLSchema.query(queryType);
    }

    /**
     * Scans tha argument Class types and add them to the SchemaBuilder as GraphQLFieldDefinition
     * and to the RegistryBuilder
     */
    public void addTypesForComponentClasses(HashSet<Class<?>> components) {
        for (Class<?> component : components) {
            if (component.isEnum()) {
                addEnumType((Class<? extends Enum<?>>) component);
            } else {
                addObjectType(component);
            }
        }
    }

    private void addEnumType(Class<? extends Enum<?>> component) {
        GraphQLEnumType enumType = TypeAdapter.graphQLEnumTypeFromEnum(component);
        graphQLSchema.additionalType(enumType);
    }

    private void addObjectType(Class<?> component) {
        GraphQLObjectType objectType = TypeAdapter.graphQLObjectTypeFromClass(component);
        graphQLSchema.additionalType(objectType);
        for (Field field : component.getDeclaredFields()) {
            if (field.isAnnotationPresent(FieldOf.class)) {
                Class<?> fieldType = field.getType();
                String fieldName = field.getType().getSimpleName();
                DataFetcher<?> fetcher = (env) -> fieldType.cast(field.get(component.cast(env.getSource())));
                registry.dataFetcher(FieldCoordinates.coordinates(objectType.getName(), fieldName), fetcher);
            }
        }
    }
}
