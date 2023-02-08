package org.example.graphql.generator_util;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.example.graphql.annotation.ArgWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

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
     * and if it finds one add it to the SchemaBuilder as GraphQLFieldDefinition and to the RegistryBuilder
     */
    public void addQueryForDataService(Object dataService) {
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("Query");
        for (Method method : MethodAdapter.queryMethodsOf(dataService)) {
            DataFetcher<?> fetcher;
            if (MethodAdapter.hasListReturnWithoutArg(method)) {
                queryType.field(MethodAdapter.listReturnWithoutArg(method));
                fetcher = env -> method.invoke(dataService);
            } else if (MethodAdapter.hasObjectReturnByOneArg(method)) {
                queryType.field(MethodAdapter.objectReturnByOneArg(method));
                String argName = method.getParameters()[0].getAnnotation(ArgWith.class).name();
                fetcher = env -> method.invoke(dataService, env.getArguments().get(argName));
            } else {
                throw new RuntimeException("Not implemented type of Query field for " + method);
            }
            registry.dataFetcher(FieldCoordinates.coordinates("Query", method.getName()), fetcher);
        }
        graphQLSchema.query(queryType);
    }

    /**
     * Scans tha argument Class types and add them to the SchemaBuilder as GraphQLFieldDefinition
     * and to the RegistryBuilder
     */
    public void addTypesForComponentClasses(Set<Class<?>> components) {
        for (Class<?> component : components) {
            if (component.isEnum()) {
                addEnumType((Class<? extends Enum<?>>) component);
            } else {
                addObjectType(component);
            }
        }
    }

    private void addEnumType(Class<? extends Enum<?>> component) {
        graphQLSchema.additionalType(TypeAdapter.graphQLEnumTypeFromEnum(component));
    }

    private void addObjectType(Class<?> component) {
        String typeName = component.getSimpleName();
        for (Field field : FieldAdapter.typeFieldsOf(component)) {
            Class<?> fieldType = field.getType();
            String fieldName = fieldType.getSimpleName();
            DataFetcher<?> fetcher = env -> fieldType.cast(field.get(component.cast(env.getSource())));
            registry.dataFetcher(FieldCoordinates.coordinates(typeName, fieldName), fetcher);
        }
        graphQLSchema.additionalType(TypeAdapter.graphQLObjectTypeFromClass(component));
    }
}
