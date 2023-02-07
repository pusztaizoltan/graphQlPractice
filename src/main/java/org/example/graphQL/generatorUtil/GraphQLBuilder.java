package org.example.graphQL.generatorUtil;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.example.graphQL.annotation.FieldOf;
import org.example.graphQL.annotation.FieldType;
import org.example.graphQL.annotation.UseAsInt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;

public class GraphQLBuilder {
    GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
    GraphQLSchema.Builder graphQLSchema = GraphQLSchema.newSchema();

    public GraphQLSchema build() {
        return this.graphQLSchema.codeRegistry(this.registry.build()).build();
    }

    public void addQueryForDataService(Object dataService) {
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("Query");
        Method[] methods = dataService.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers()) || !method.isAnnotationPresent(FieldOf.class)) {
                continue;
            }
            FieldType category = method.getAnnotation(FieldOf.class).type();
            if (method.getParameters().length == 0 && category == FieldType.LIST) {
                queryType.field(FieldAdapter.nestedReturn(method)); //todo
                DataFetcher<?> fetcher = (env) -> method.invoke(dataService);
                registry.dataFetcher(FieldCoordinates.coordinates("Query", method.getName()), fetcher);
            } else if (method.getParameters().length == 1 &&
                       category == FieldType.OBJECT &&
                       method.getParameters()[0].isAnnotationPresent(UseAsInt.class)) {
                // todo rewrite if you can, to not being a hatchetJob
                GraphQLFieldDefinition field = FieldAdapter.argumentedReturn(method); //todo
                UseAsInt marker = method.getParameters()[0].getAnnotation(UseAsInt.class);
                queryType.field(field);
                DataFetcher<?> fetcher = (env) -> method.invoke(dataService, env.getArguments().get(marker.name()));
                registry.dataFetcher(FieldCoordinates.coordinates("Query", field.getName()), fetcher);
            } else {
                throw new UnsupportedOperationException("Not implemented yet");
            }
        }
        graphQLSchema.query(queryType);
    }

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
        Field[] fields = component.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(FieldOf.class)) {
                Class<?> fieldType = field.getType();
                String fieldName = field.getType().getSimpleName();
                DataFetcher<?> fetcher = (env) -> fieldType.cast(field.get(component.cast(env.getSource())));
                registry.dataFetcher(FieldCoordinates.coordinates(objectType.getName(), fieldName), fetcher);
            }
        }
    }
}
