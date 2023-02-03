package org.example.graphQL.annotation;

import graphql.Scalars;
import graphql.scalar.GraphqlBooleanCoercing;
import graphql.scalar.GraphqlFloatCoercing;
import graphql.scalar.GraphqlIDCoercing;
import graphql.scalar.GraphqlIntCoercing;
import graphql.scalar.GraphqlStringCoercing;
import graphql.schema.GraphQLScalarType;

public enum ScalarFitter {
    INT(Scalars.GraphQLInt),
    FLOAT(Scalars.GraphQLFloat),
    STRING(Scalars.GraphQLString),
    BOOLEAN(Scalars.GraphQLBoolean),
    ID(Scalars.GraphQLID),
    DEFAULT(null);
    GraphQLScalarType graphQLScalarType;

    ScalarFitter(GraphQLScalarType graphQLScalarType) {
        this.graphQLScalarType = graphQLScalarType;
    }
}

