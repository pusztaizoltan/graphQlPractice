package org.example.graphQL;

import graphql.GraphQL;
import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLEnumValueDefinition;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLTypeReference;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.UseAsInt;
import org.example.graphQL.annotation.UseMarker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class SchemaGeneratorImpl {
    ClassParser classParser = new ClassParser();
    GraphQLBuilder builder = new GraphQLBuilder();

    public SchemaGeneratorImpl(Object dataService) {
        this.classParser.parseClassesFromDataService(dataService);
        this.builder.buildQueryForDataService(dataService);
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
        GraphQLCodeRegistry registry = builder.registry.build();
        GraphQLSchema schema = builder.graphQLSchema.codeRegistry(registry).build();
        return GraphQL.newGraphQL(schema).build();
    }



    private static class GraphQLBuilder{
        GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
        GraphQLSchema.Builder graphQLSchema = GraphQLSchema.newSchema();

        private void buildQueryForDataService(Object dataService) {
            GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("Query");
            Method[] methods = dataService.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (!Modifier.isPublic(method.getModifiers()) || !method.isAnnotationPresent(UseMarker.class)) {
                    continue;
                }
                GraphQlIdentifyer category = method.getAnnotation(UseMarker.class).category();
                if (method.getParameters().length == 0 && category == GraphQlIdentifyer.NESTED_TYPE) {
                    queryType.field(FieldAdapter.nestedReturn(method));
                    DataFetcher<?> fetcher = (env) -> method.invoke(dataService);
                    registry.dataFetcher(FieldCoordinates.coordinates("Query", method.getName()), fetcher);
                } else if (method.getParameters().length == 1 &&
                           category == GraphQlIdentifyer.TYPE &&
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
    private static class TypeAdapter {
        private static GraphQLEnumType graphQLEnumTypeFromEnum(Class<? extends Enum<?>> enumType) {
            String typeName = enumType.getSimpleName();
            return GraphQLEnumType.newEnum()
                                  .name(typeName)
                                  .values(Arrays.stream(enumType.getEnumConstants())
                                                .map((eConst) -> GraphQLEnumValueDefinition
                                                        .newEnumValueDefinition()
                                                        .value(eConst.name())
                                                        .name(eConst.name())
                                                        .build())
                                                .collect(Collectors.toList()))
                                  .build();
        }

        private static GraphQLObjectType graphQLObjectTypeFromClass(Class<?> classType) {
            GraphQLObjectType.Builder typeBuilder = GraphQLObjectType.newObject().name(classType.getSimpleName());
            Field[] fields = classType.getDeclaredFields();
            for (Field field : fields) {
                UseMarker fieldAnnotation = field.getAnnotation(UseMarker.class);
                if (fieldAnnotation.category() == GraphQlIdentifyer.TYPE) {
                    typeBuilder.field(FieldAdapter.typeField(field));
                } else if (fieldAnnotation.category() == GraphQlIdentifyer.NESTED_TYPE) {
                    typeBuilder.field(FieldAdapter.nestedField(field));
                } else if (fieldAnnotation.category() == GraphQlIdentifyer.ENUM) {
                    // experimental result: todo  yes typeReference works with enums
                    typeBuilder.field(FieldAdapter.typeField(field));
                } else if (fieldAnnotation.category() == GraphQlIdentifyer.SCALAR) {
                    typeBuilder.field(FieldAdapter.scalarField(field));
                }
            }
            return typeBuilder.build();
        }
    }

    private static class ClassParser {
        private final HashSet<Class<?>> components = new HashSet<>();

        private void parseAdditionalClasses(Class<?>... classes) {
            for (Class<?> cls : classes) {
                addNestedClasses(cls);
                components.add(cls);
            }
        }

        private void parseClassesFromDataService(Object dataService) {
            Method[] methods = dataService.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (!Modifier.isPublic(method.getModifiers()) || !method.isAnnotationPresent(UseMarker.class)) {
                    continue;
                }
                GraphQlIdentifyer category = method.getAnnotation(UseMarker.class).category();
                if (category == GraphQlIdentifyer.NESTED_TYPE || category == GraphQlIdentifyer.TYPE) {
                    Class<?> type;
                    if (category == GraphQlIdentifyer.NESTED_TYPE) {
                        type = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                    } else {
                        type = method.getReturnType();
                    }
                    if (!components.contains(type)) {
                        components.add(type);
                        addNestedClasses(type);
                    }
                }
            }
        }

        private void addNestedClasses(Class<?> cls) {
            for (Field field : cls.getDeclaredFields()) {
                if (!field.isAnnotationPresent(UseMarker.class)) {
                    continue;
                }
                Class<?> type;
                switch (field.getAnnotation(UseMarker.class).category()) {
                    case ENUM -> {
                        type = field.getType();
                        components.add(type);
                    }
                    case TYPE -> {
                        type = field.getType();
                        if (!components.contains(type)) {
                            components.add(type);
                            addNestedClasses(type);
                        }
                    }
                    case NESTED_TYPE -> {
                        type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        if (!components.contains(type)) {
                            components.add(type);
                            addNestedClasses(type);
                        }
                    }
                    case SCALAR -> {
                    }
                    default ->
                            throw new IllegalStateException("Unexpected: " + field.getAnnotation(UseMarker.class).category());
                }
            }
        }
    }

    private static class FieldAdapter {
        private static GraphQLFieldDefinition scalarField(Field field) {
            GraphQLScalarType scalar = field.getAnnotation(UseMarker.class).asScalar().graphQLScalarType;
            return GraphQLFieldDefinition.newFieldDefinition()
                                         .name(field.getName())
                                         .type(scalar)
                                         .build();
        }

        private static GraphQLFieldDefinition nestedField(Field field) {
            String type = ((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]).getSimpleName();
            return GraphQLFieldDefinition.newFieldDefinition()
                                         .name(field.getName())
                                         .type(GraphQLList.list(GraphQLTypeReference.typeRef(type)))
                                         .build();
        }

        private static GraphQLFieldDefinition nestedReturn(Method method) {
            String type = ((Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]).getSimpleName();
            return GraphQLFieldDefinition.newFieldDefinition()
                                         .name(method.getName())
                                         .type(GraphQLList.list(GraphQLTypeReference.typeRef(type)))
                                         .build();
        }

        private static GraphQLFieldDefinition typeField(Field field) {
            String type = field.getType().getSimpleName();
            return GraphQLFieldDefinition.newFieldDefinition()
                                         .name(field.getName())
                                         .type(GraphQLTypeReference.typeRef(type))
                                         .build();
        }

        private static GraphQLFieldDefinition argumentedReturn(Method method) {
            String type = method.getReturnType().getSimpleName();
            UseAsInt marker = method.getParameters()[0].getAnnotation(UseAsInt.class);
            return GraphQLFieldDefinition.newFieldDefinition()
                                         .name(method.getName())
                                         .type(GraphQLTypeReference.typeRef(type))
                                         .argument(GraphQLArgument.newArgument()
                                                                  .name(marker.name())
                                                                  .type(Scalars.GraphQLInt)
                                                                  .build())
                                         .build();
        }
    }
}