package org.example.graphQL;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
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
import org.example.graphQL.annotation.UseMarker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SchemaGeneratorImpl {
    Logger logger = Logger.getLogger("SchemaGeneratorImpl");
    Object dataService;
    HashSet<Class<?>> components = new HashSet<>();
    GraphQLObjectType query;
    GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
    GraphQLSchema.Builder graphQLSchema = GraphQLSchema.newSchema();

    public void withClasses(Class<?>... classes) {
        logger.info("Instantiate SchemaGeneratorImpl");
        initTypesWith(classes);
    }

    public SchemaGeneratorImpl(Object dataService) {
        logger.info("Instantiate SchemaGeneratorImpl");
        this.dataService = dataService;
        initTypesFromDataService();
        initQueryType();
        System.out.println(components);
    }

    public GraphQL getGraphQL() {
        initGraphQLSchema();
        GraphQLCodeRegistry r = registry.build();
        GraphQLSchema schema = graphQLSchema.codeRegistry(r).build();
        return GraphQL.newGraphQL(schema).build();
    }

    void initQueryType() {
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("Query");
//        GraphQlIdentifyer category = field.getAnnotation(UseMarker.class).category();
        Method[] methods = dataService.getClass().getDeclaredMethods();
        for (Method method : methods) {
            // todo only public menthods
            if (Modifier.isPublic(method.getModifiers())) {
                if (method.getParameters().length == 0) {
                    String typeName = ((Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]).getSimpleName();
                    System.out.println("--------typeName " + typeName);
                    queryType.field(GraphQLFieldDefinition.newFieldDefinition()
                                                          .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                                          .name(method.getName()))
                             .build();
                    DataFetcher<?> fetcher = (env) -> method.invoke(dataService);
                    registry.dataFetcher(FieldCoordinates.coordinates("Query", method.getName()), fetcher);
                }
//                GraphQlIdentifyer category = method.getAnnotation(UseMarker.class).category();
//                if (category == GraphQlIdentifyer.TYPE) {
//                String genericTypeName = method;
//                } else if (category == GraphQlIdentifyer.NESTED_TYPE) {
//                    queryType.field(GraphQLFieldDefinition.newFieldDefinition()
//                                                          .type(GraphQLList.list(GraphQLTypeReference.typeRef("TestClass")))
//                                                          .name("allTestClass"))
//                             .build();
            }
        }
//        }
        graphQLSchema.query(queryType);
    }

    private GraphQLObjectType graphQLObjectTypeFromClass(Class<?> classType) {
        GraphQLObjectType.Builder typeBuilder = GraphQLObjectType.newObject().name(classType.getSimpleName());
        Field[] fields = classType.getDeclaredFields();
        for (Field field : fields) {
            UseMarker fieldAnnotation = field.getAnnotation(UseMarker.class);
            if (fieldAnnotation.category() == GraphQlIdentifyer.TYPE) {
                typeBuilder.field(FieldAdapter.typeField(field));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.NESTED_TYPE) {
                typeBuilder.field(FieldAdapter.nestedField(field));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.ENUM) {
                // todo to test if typeReference works with enums
//                String enumTypeName = field.getType().getTypeName();
//                typeBuilder = typeBuilder.field(newFieldDefinition()
//                        .name(field.getName())
//                        .type(GraphQLTypeReference.typeRef(enumTypeName)));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.SCALAR) {
                typeBuilder.field(FieldAdapter.scalarField(field));
            }
        }
        return typeBuilder.build();
    }

    private void initGraphQLSchema() {
        for (Class<?> component : components) {
            if (component.isEnum()) {
                // todo does it need registry?
                GraphQLEnumType enumType = graphQLEnumTypeFromEnum((Class<? extends Enum<?>>) component);
//                DataFetcher<?> fetcher = (env) -> env.e enumType. .cast(field.get(component.cast(env.)));
                graphQLSchema.additionalType(enumType);
//                registry.dataFetchers()
            } else {
                GraphQLObjectType objectType = graphQLObjectTypeFromClass(component);
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

    private void initTypesFromDataService() {
        Method[] methods = dataService.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())) {
                if (method.isAnnotationPresent(UseMarker.class)) {
                    GraphQlIdentifyer category = method.getAnnotation(UseMarker.class).category();
                    if (category == GraphQlIdentifyer.TYPE) {
                        Class<?> type = method.getReturnType();
                        if (!this.components.contains(type)) {
                            components.add(type);
                            addNestedClasses(type);
                        }
                    } else if (category == GraphQlIdentifyer.NESTED_TYPE) {
                        Class<?> type = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                        if (!this.components.contains(type)) {
                            components.add(type);
                            addNestedClasses(type);
                        }
                    }
                }
            }
        }
    }

    private void initTypesWith(Class<?>... classes) {
        for (Class<?> cls : classes) {
            addNestedClasses(cls);
            components.add(cls);
        }
        System.out.println(this.components);
    }

    private void addNestedClasses(Class<?> cls) {
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(UseMarker.class)) {
                Class<?> type;
                switch (field.getAnnotation(UseMarker.class).category()) {
                    case ENUM -> {
                        type = field.getType();
                        if (!this.components.contains(type)) {
                            components.add(type);
                        }
                    }
                    case TYPE -> {
                        type = field.getType();
                        if (!this.components.contains(type)) {
                            components.add(type);
                            addNestedClasses(type);
                        }
                    }
                    case NESTED_TYPE -> {
                        type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        if (!this.components.contains(type)) {
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

    private GraphQLEnumType graphQLEnumTypeFromEnum(Class<? extends Enum<?>> enumType) {
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

        private static GraphQLFieldDefinition typeField(Field field) {
            String type = field.getType().getSimpleName();
            return GraphQLFieldDefinition.newFieldDefinition()
                                         .name(field.getName())
                                         .type(GraphQLTypeReference.typeRef(type))
                                         .build();
        }
    }
}