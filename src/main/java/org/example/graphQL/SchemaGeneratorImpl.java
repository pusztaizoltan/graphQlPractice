package org.example.graphQL;

import graphql.Scalars;
import graphql.language.SDLDefinition;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphQL.annotation.GraphQlIdentifyer;
import org.example.graphQL.annotation.UseMarker;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;

public class SchemaGeneratorImpl {
    HashSet<Class<?>> components;
//    TypeDefinitionRegistry typeDefinitionRegistry;

    public SchemaGeneratorImpl() {
    }

    public void initWith(Class<?>... classes) {
        // todo now it is operational as get all the nested components
        // todo fix to eliminate duplicate operations but to not omit nested components

        HashSet<Class<?>> components = new HashSet<>();

        for (Class<?> cls : classes) {
            components = getUniqueClasses(cls, components);
        }
        this.components = components;
        System.out.println(this.components);
    }

    private HashSet<Class<?>> getUniqueClasses(Class<?> cls, HashSet<Class<?>> components) {
        System.out.println(cls);
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            GraphQlIdentifyer category = field.getAnnotation(UseMarker.class).category();
            System.out.println("- " + category + " " + field.getName());
            if (category == GraphQlIdentifyer.TYPE) {
                Class<?> type = field.getType();
                if (components.contains(type)) {
                    break;
                }
                components.add(type);
                components = getUniqueClasses(type, components); // recursive usage
            } else if (category == GraphQlIdentifyer.NESTED_TYPE) {
                Type generic = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                if (components.contains((Class<?>) generic)) {
                    break;
                }
                components.add((Class<?>) generic);
                components = getUniqueClasses((Class<?>) generic, components);
            } else if (category == GraphQlIdentifyer.ENUM){
                Class<?> type = field.getType();
                if (components.contains(type)) {
                    break;
                }
                components.add(type);
            }
        }
        return components;
    }

    void test() {
        SDLDefinition aa;
    }

    static GraphQLObjectType graphQLObjectTypeFromClass(Class<?> classType) {
        GraphQLObjectType.Builder typeBuilder = GraphQLObjectType.newObject().name(classType.getSimpleName());
        Field[] fields = classType.getDeclaredFields();
        for (Field field : fields) {
            GraphQLScalarType fieldType = typeAdapter(field.getType().getSimpleName());
            typeBuilder = typeBuilder.field(GraphQLFieldDefinition.newFieldDefinition()
                                                                  .name(field.getName())
                                                                  .type(fieldType));
        }
        return typeBuilder.build();
    }

    private static GraphQLScalarType typeAdapter(String javaType) {
        switch (javaType) {
            case "long" -> {
                return Scalars.GraphQLInt;
            }
            case "String" -> {
                return Scalars.GraphQLString;
            }
            // ...
        }
        return null;
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
