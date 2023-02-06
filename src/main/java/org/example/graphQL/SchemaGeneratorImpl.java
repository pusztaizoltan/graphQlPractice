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
import org.example.db.ListDb;
import org.example.db.ListDbImpl;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.UseMarker;

import java.lang.reflect.Field;
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
        GraphQLSchema schema = graphQLSchema.codeRegistry(registry.build()).build();
        return GraphQL.newGraphQL(schema).build();
    }

    public SchemaGeneratorImpl(Class<?>... classes) {
        initTypesWith(classes);
        initQueryType(ListDb.class);
        initGraphQLSchema();
    }

    void initQueryType(Class<?> datasourceInterface){
        GraphQLObjectType.Builder queryType = GraphQLObjectType.newObject().name("Query");

        queryType.field(GraphQLFieldDefinition.newFieldDefinition()
                                              .type(GraphQLList.list(GraphQLTypeReference.typeRef("TestClass")))
                                              .name("allTestClass"))
                                              .build();
        registry.dataFetcher(FieldCoordinates.coordinates("Query", "allTestClass"), (DataFetcher<?>) (env) -> listDbImpl.getTestClassDB());
        graphQLSchema.query(queryType);
    }

    private void initGraphQLSchema() {
        // todo this is temp query rewrite
        for (Class<?> component : components) {
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

    private void initTypesWith(Class<?>... classes) {
        // todo now it is operational as get all the nested components
        // todo fix to eliminate duplicate operations but to not omit nested components
        HashSet<Class<?>> components = new HashSet<>();
        for (Class<?> cls : classes) {
            components.add(cls);
            components = addNestedClasses(cls, components);
        }
        this.components.addAll(components);
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
                                                    .build())
                                            .collect(Collectors.toList()))
                              .build();
    }

    private GraphQLObjectType graphQLObjectTypeFromClass(Class<?> classType) {
        GraphQLObjectType.Builder typeBuilder = GraphQLObjectType.newObject().name(classType.getSimpleName());
        Field[] fields = classType.getDeclaredFields();
        for (Field field : fields) {
            UseMarker fieldAnnotation = field.getAnnotation(UseMarker.class);
            if (fieldAnnotation.category() == GraphQlIdentifyer.TYPE) {
                String genericTypeName = field.getType().getTypeName();
                typeBuilder = typeBuilder.field(newFieldDefinition()
                        .name(field.getName())
                        .type(GraphQLTypeReference.typeRef(genericTypeName)));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.NESTED_TYPE) {
                String genericTypeName = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getClass().getSimpleName();
                typeBuilder = typeBuilder.field(newFieldDefinition()
                        .name(field.getName())
                        .type(GraphQLList.list(GraphQLTypeReference.typeRef(genericTypeName))));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.ENUM) {
                // todo to test if typeReference works with enums
                String enumTypeName = field.getType().getTypeName();
                typeBuilder = typeBuilder.field(newFieldDefinition()
                        .name(field.getName())
                        .type(GraphQLTypeReference.typeRef(enumTypeName)));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.SCALAR) {
                GraphQLScalarType graphQLScalarType = fieldAnnotation.asScalar().graphQLScalarType;
                typeBuilder = typeBuilder.field(newFieldDefinition()
                        .name(field.getName())
                        .type(graphQLScalarType));
            }
        }
        return typeBuilder.build();
    }
}
