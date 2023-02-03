package org.example.entity;

import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.idl.TypeRuntimeWiring;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Schemable {

    default void experimentMethod(){

        System.out.println(this.getClass().getSimpleName());
    }



    static TypeRuntimeWiring TypeRuntimeWiringFromClass(Class<?> classType) {
        TypeRuntimeWiring.Builder builder = new TypeRuntimeWiring.Builder().typeName(classType.getSimpleName());
        Field[] fields = classType.getDeclaredFields();
        for (Field field: fields) {
            Class<?> fieldType = field.getType();
            String fieldName = field.getType().getSimpleName();
            builder = builder.dataFetcher(fieldName, env ->
                    fieldType.cast(field.get(classType.cast(env.getSource()))));
        }
        return builder.build();
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


}
