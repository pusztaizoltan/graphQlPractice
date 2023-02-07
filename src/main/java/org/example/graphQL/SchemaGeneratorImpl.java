package org.example.graphQL;

import graphql.GraphQL;
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
import org.example.graphQL.generatorUtil.FieldAdapter;
import org.example.graphQL.generatorUtil.TypeAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;

public class SchemaGeneratorImpl {
    ClassParser classParser = new ClassParser();
    GraphQLBuilder builder = new GraphQLBuilder();

    public SchemaGeneratorImpl(Object dataService) {
        this.classParser.parseClassesFromDataService(dataService);
        this.builder.addQueryForDataService(dataService);
        {
            System.out.println("----------------------------------------");
            System.out.println("- Extracted components form dataService:\n" + classParser.components);
            System.out.println();
        }
    }

    public void withAdditionalClasses(Class<?>... classes) {
        this.classParser.parseAdditionalClasses(classes);
    }

    public GraphQL getGraphQL() {
        builder.addTypesForComponentClasses(this.classParser.components);
        return GraphQL.newGraphQL(builder.build()).build();
    }

    private static class GraphQLBuilder {
        GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
        GraphQLSchema.Builder graphQLSchema = GraphQLSchema.newSchema();

        private GraphQLSchema build() {
            return this.graphQLSchema.codeRegistry(this.registry.build()).build();
        }

        private void addQueryForDataService(Object dataService) {
            GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("Query");
            Method[] methods = dataService.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (!Modifier.isPublic(method.getModifiers()) || !method.isAnnotationPresent(FieldOf.class)) {
                    continue;
                }
                FieldType category = method.getAnnotation(FieldOf.class).type();
                if (method.getParameters().length == 0 && category == FieldType.LIST) {
                    queryType.field(FieldAdapter.nestedReturn(method));
                    DataFetcher<?> fetcher = (env) -> method.invoke(dataService);
                    registry.dataFetcher(FieldCoordinates.coordinates("Query", method.getName()), fetcher);
                } else if (method.getParameters().length == 1 &&
                           category == FieldType.OBJECT &&
                           method.getParameters()[0].isAnnotationPresent(UseAsInt.class)) {
                    // todo rewrite if you can, to not being a hatchetJob
                    GraphQLFieldDefinition field = FieldAdapter.argumentedReturn(method);
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

        private void addTypesForComponentClasses(HashSet<Class<?>> components) {
            for (Class<?> component : components) {
                if (component.isEnum()) {
                    // experimental result: todo seems like enum in this development phase doesn't need registry?
                    GraphQLEnumType enumType = TypeAdapter.graphQLEnumTypeFromEnum((Class<? extends Enum<?>>) component);
                    graphQLSchema.additionalType(enumType);
                } else {
                    GraphQLObjectType objectType = TypeAdapter.graphQLObjectTypeFromClass(component);
                    graphQLSchema.additionalType(objectType);
                    Field[] fields = component.getDeclaredFields();
                    for (Field field : fields) {
                        Class<?> fieldType = field.getType();
                        String fieldName = field.getType().getSimpleName();
                        DataFetcher<?> fetcher = (env) -> fieldType.cast(field.get(component.cast(env.getSource())));
                        registry.dataFetcher(FieldCoordinates.coordinates(objectType.getName(), fieldName), fetcher);
                    }
                }
            }
        }
    }

    private static class ClassParser {
        private final HashSet<Class<?>> components = new HashSet<>();

        private void parseAdditionalClasses(Class<?>... classes) {
            for (Class<?> classType : classes) {
                parseNestedClasses(classType);
                components.add(classType);
            }
        }

        private void parseClassesFromDataService(Object dataService) {
            Method[] methods = dataService.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (!Modifier.isPublic(method.getModifiers()) || !method.isAnnotationPresent(FieldOf.class)) {
                    continue;
                }
                FieldType category = method.getAnnotation(FieldOf.class).type();
                if (category == FieldType.LIST || category == FieldType.OBJECT) {
                    Class<?> type;
                    if (category == FieldType.LIST) {
                        type = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                    } else {
                        type = method.getReturnType();
                    }
                    if (!components.contains(type)) {
                        components.add(type);
                        parseNestedClasses(type);
                    }
                }
            }
        }

        private void parseNestedClasses(Class<?> classType) {
            for (Field field : classType.getDeclaredFields()) {
                if (!field.isAnnotationPresent(FieldOf.class)) {
                    continue;
                }
                Class<?> type;
                switch (field.getAnnotation(FieldOf.class).type()) {
                    case ENUM -> {
                        type = field.getType();
                        components.add(type);
                    }
                    case OBJECT -> {
                        type = field.getType();
                        if (!components.contains(type)) {
                            components.add(type);
                            parseNestedClasses(type);
                        }
                    }
                    case LIST -> {
                        type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        if (!components.contains(type)) {
                            components.add(type);
                            parseNestedClasses(type);
                        }
                    }
                    case SCALAR_INT, SCALAR_BOOLEAN, SCALAR_FLOAT, SCALAR_STRING, SCALAR_ID -> {
                    }
                    default ->
                            throw new IllegalStateException("Unexpected: " + field.getAnnotation(FieldOf.class).type());
                }
            }
        }
    }
}