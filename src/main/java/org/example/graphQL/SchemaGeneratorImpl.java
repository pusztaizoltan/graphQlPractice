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
import graphql.schema.TypeResolver;
import org.example.db.ListDb;
import org.example.db.ListDbImpl;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.entity.TestClass;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.UseMarker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

public class SchemaGeneratorImpl {
    ListDbImpl listDbImpl = new ListDbImpl();
    HashSet<Class<?>> components = new HashSet<>();
    GraphQLObjectType query;
    GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();
    GraphQLSchema.Builder graphQLSchema = GraphQLSchema.newSchema();

    public GraphQL getGraphQL() {
        GraphQLCodeRegistry r = registry.build();

        GraphQLSchema schema = graphQLSchema.codeRegistry(r).build();

        return GraphQL.newGraphQL(schema).build();
    }

    public SchemaGeneratorImpl(Class<?>... classes) {
        initTypesWith(classes);
        initQueryType(ListDb.class);
        initGraphQLSchema();
    }

    void initQueryType(Class<?> datasourceImplementation) {
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("Query");
//        GraphQlIdentifyer category = field.getAnnotation(UseMarker.class).category();
        Method[] methods = listDbImpl.getClass().getDeclaredMethods();
        for (Method method : methods) {
            // todo only public menthods
            if (Modifier.isPublic(method.getModifiers())) {
                if (method.getParameters().length == 0) {
                    String typeName = ((Class<?>)((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]).getSimpleName();
                    System.out.println("--------typeName "+ typeName);
                    queryType.field(GraphQLFieldDefinition.newFieldDefinition()
                                                          .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                                          .name(method.getName()))
                             .build();
                    DataFetcher<?> fetcher = (env) -> method.invoke(listDbImpl) ;
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
                String genericTypeName = field.getType().getSimpleName();
                System.out.println("--------genericTypeName "+ genericTypeName);
                typeBuilder = typeBuilder.field(newFieldDefinition()
                        .name(field.getName())
                        .type(GraphQLTypeReference.typeRef(genericTypeName)));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.NESTED_TYPE) {
                String genericTypeName = ((Class<?>)((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]).getSimpleName();
                System.out.println("--------genericTypeName_Nested "+ genericTypeName);
                typeBuilder = typeBuilder.field(newFieldDefinition()
                        .name(field.getName())
                        .type(GraphQLList.list(GraphQLTypeReference.typeRef(genericTypeName))));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.ENUM) {
                // todo to test if typeReference works with enums
//                String enumTypeName = field.getType().getTypeName();
//                typeBuilder = typeBuilder.field(newFieldDefinition()
//                        .name(field.getName())
//                        .type(GraphQLTypeReference.typeRef(enumTypeName)));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.SCALAR) {
                GraphQLScalarType graphQLScalarType = fieldAnnotation.asScalar().graphQLScalarType;
                typeBuilder = typeBuilder.field(newFieldDefinition()
                        .name(field.getName())
                        .type(graphQLScalarType));
            }
        }
        return typeBuilder.build();
    }

    private void initGraphQLSchema() {
        for (Class<?> component : components) {
            if (component.isEnum()) {
                // todo find better casting mechanism
                // todo does it need registry?
                GraphQLEnumType enumType = graphQLEnumTypeFromEnum((Class<? extends Enum>) component);
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

    private void initTypesWith(Class<?>... classes) {
        // todo now it is operational as get all the nested components
        // todo fix to eliminate duplicate operations but to not omit nested components
        HashSet<Class<?>> components = new HashSet<>();
        for (Class<?> cls : classes) {
            components.add(cls);
            components = addNestedClasses(cls, components);
        }
        this.components.addAll(components);
        this.components.add(Reader.class); // todo hachetJob
        System.out.println(this.components);
    }

    private HashSet<Class<?>> addNestedClasses(Class<?> cls, HashSet<Class<?>> components) {
        // todo refactor extract conditionals
        for (Field field : cls.getDeclaredFields()) {
            GraphQlIdentifyer category = field.getAnnotation(UseMarker.class).category();
            if (category == GraphQlIdentifyer.TYPE) {
                Class<?> type = field.getType();
                if (components.contains(type)) {
                    break;
                }
                components.add(type);
                components = addNestedClasses(type, components);
            } else if (category == GraphQlIdentifyer.NESTED_TYPE) {
                Type generic = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                if (components.contains((Class<?>) generic)) {
                    break;
                }
                components.add((Class<?>) generic);
                components = addNestedClasses((Class<?>) generic, components);
            } else if (category == GraphQlIdentifyer.ENUM) {
                Class<?> type = field.getType();
                if (components.contains(type)) {
                    break;
                }
                components.add(type);
            }
        }
        return components;
    }

    private GraphQLEnumType graphQLEnumTypeFromEnum(Class<? extends Enum> enumType) {
        // todo raw use of enum fix it
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
}
