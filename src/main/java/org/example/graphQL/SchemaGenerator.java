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
import graphql.schema.idl.TypeDefinitionRegistry;


import java.lang.reflect.Field;


public class SchemaGenerator {
    TypeDefinitionRegistry typeDefinitionRegistry;

    public SchemaGenerator(TypeDefinitionRegistry typeDefinitionRegistry) {
        this.typeDefinitionRegistry = typeDefinitionRegistry;
    }



    void test(){

        SDLDefinition aa;

    }

    static GraphQLObjectType graphQLObjectTypeFromClass(Class<?> classType){
        GraphQLObjectType.Builder typeBuilder = GraphQLObjectType.newObject().name(classType.getSimpleName());
        Field[] fields = classType.getDeclaredFields();
        for (Field field: fields) {
            GraphQLScalarType fieldType = typeAdapter(field.getType().getSimpleName());
            typeBuilder = typeBuilder.field(GraphQLFieldDefinition.newFieldDefinition()
                                                                  .name(field.getName())
                                                                  .type(fieldType));
        }
        return typeBuilder.build();
    }
    private static GraphQLScalarType typeAdapter(String javaType){
        switch (javaType) {
            case "long" -> {return Scalars.GraphQLInt;}
            case "String" -> {return Scalars.GraphQLString;}
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
