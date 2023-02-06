package org.example.graphQL;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLEnumValueDefinition;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.UseMarker;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class SchemaGeneratorImpl {
    HashSet<Class<?>> components = new HashSet<>();
    TypeDefinitionRegistry typeDefinitionRegistry = new TypeDefinitionRegistry();
    RuntimeWiring runtimeWiring;

    public SchemaGeneratorImpl(Class<?>... classes) {
        initTypesWith(classes);
        initTypeDefinitionRegistry();
        this.runtimeWiring = initRuntimeWiringFromClass();


    }

    private void initTypeDefinitionRegistry(){
        for (Class<?> component:components){
            if (component.isEnum()){
                Class<? extends Enum> enumType =  component.asSubclass(Enum.class);
                typeDefinitionRegistry.add(graphQLEnumTypeFromEnum(enumType).getDefinition());
            } else {
                typeDefinitionRegistry.add(graphQLObjectTypeFromClass(component).getDefinition());
            }
        }
    }

    private RuntimeWiring initRuntimeWiringFromClass(){
        RuntimeWiring.Builder runtimeWiring = RuntimeWiring.newRuntimeWiring();
        for (Class<?> component: components) {
            if (component.isEnum()){
                //todo enum wire
            } else{
                runtimeWiring.type(typeRuntimeWiringFromClass(component));
            }
        }
        return runtimeWiring.build();

    }

    private TypeRuntimeWiring typeRuntimeWiringFromClass(Class<?> classType) {
        TypeRuntimeWiring.Builder builder = new TypeRuntimeWiring.Builder().typeName(classType.getSimpleName());
        Field[] fields = classType.getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            String fieldName = field.getType().getSimpleName();
            builder = builder.dataFetcher(fieldName, env ->
                    fieldType.cast(field.get(classType.cast(env.getSource()))));
        }
        return builder.build();
    }

    private void initTypesWith(Class<?>... classes) {
        // todo now it is operational as get all the nested components
        // todo fix to eliminate duplicate operations but to not omit nested components
        HashSet<Class<?>> components = new HashSet<>();
        for (Class<?> cls : classes) {
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

    static GraphQLEnumType graphQLEnumTypeFromEnum(Class<? extends Enum> enumType) {
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

    static GraphQLObjectType graphQLObjectTypeFromClass(Class<?> classType) {
        GraphQLObjectType.Builder typeBuilder = GraphQLObjectType.newObject().name(classType.getSimpleName());
        Field[] fields = classType.getDeclaredFields();
        for (Field field : fields) {
            UseMarker fieldAnnotation = field.getAnnotation(UseMarker.class);
            if (fieldAnnotation.category() == GraphQlIdentifyer.TYPE) {
                String genericTypeName = field.getType().getTypeName();
                typeBuilder = typeBuilder.field(GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(field.getName())
                        .type(GraphQLTypeReference.typeRef(genericTypeName)));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.NESTED_TYPE) {
                String genericTypeName = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
                typeBuilder = typeBuilder.field(GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(field.getName())
                        .type(GraphQLList.list(GraphQLTypeReference.typeRef(genericTypeName))));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.ENUM) {
                // todo to test if typeReference works with enums
                String enumTypeName = field.getType().getTypeName();
                typeBuilder = typeBuilder.field(GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(field.getName())
                        .type(GraphQLTypeReference.typeRef(enumTypeName)));
            } else if (fieldAnnotation.category() == GraphQlIdentifyer.SCALAR) {
                GraphQLScalarType graphQLScalarType = fieldAnnotation.asScalar().graphQLScalarType;
                typeBuilder = typeBuilder.field(GraphQLFieldDefinition.newFieldDefinition()
                                                                      .name(field.getName())
                                                                      .type(graphQLScalarType));
            }
        }
        return typeBuilder.build();
    }

    // todo example of typerefferenceList
    GraphQLObjectType person = GraphQLObjectType.newObject()
                                                .name("Person")
                                                .field(GraphQLFieldDefinition.newFieldDefinition()
                                                                             .name("friends")
                                                                             .type(GraphQLList.list(GraphQLTypeReference.typeRef("Person"))))
                                                .build();
    DataFetcher<Integer> exampleDataFetcher = new DataFetcher<Integer>() {
        @Override
        public Integer get(DataFetchingEnvironment environment) {
            // environment.getSource() is the value of the surrounding
            // object. In this case described by objectType
//            T value = perhapsFromDatabase(); // Perhaps getting from a DB or whatever
            return 1;
        }
    };
    GraphQLObjectType objectType = GraphQLObjectType.newObject()
                                                    .name("ObjectType")
                                                    .field(GraphQLFieldDefinition.newFieldDefinition()
                                                                                 .name("foo")
                                                                                 .type(Scalars.GraphQLString)
                                                    )
                                                    .build();
    GraphQLCodeRegistry codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
                                                          .dataFetcher(
                                                                  FieldCoordinates.coordinates("ObjectType", "foo"),
                                                                  exampleDataFetcher)
                                                          .build();


}
