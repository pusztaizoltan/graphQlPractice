package org.example.graphQL.annotation;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;

public enum FieldType {
    ENUM(null),
    OBJECT(null),
    LIST(null),
    SCALAR_INT(Scalars.GraphQLInt),
    SCALAR_FLOAT(Scalars.GraphQLFloat),
    SCALAR_STRING(Scalars.GraphQLString),
    SCALAR_BOOLEAN(Scalars.GraphQLBoolean),
    SCALAR_ID(Scalars.GraphQLID),
    ;
    public final GraphQLScalarType graphQLScalarType;

    FieldType(GraphQLScalarType graphQLScalarType) {
        this.graphQLScalarType = graphQLScalarType;
    }
    public boolean isScalar(){
        return graphQLScalarType !=null;
    }
}

