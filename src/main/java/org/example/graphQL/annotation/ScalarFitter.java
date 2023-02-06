package org.example.graphQL.annotation;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;

public enum ScalarFitter {
    INT(Scalars.GraphQLInt),
    FLOAT(Scalars.GraphQLFloat),
    STRING(Scalars.GraphQLString),
    BOOLEAN(Scalars.GraphQLBoolean),
    ID(Scalars.GraphQLID),
    DEFAULT(null);
    public GraphQLScalarType graphQLScalarType;

    ScalarFitter(GraphQLScalarType graphQLScalarType) {
        this.graphQLScalarType = graphQLScalarType;
    }
}

