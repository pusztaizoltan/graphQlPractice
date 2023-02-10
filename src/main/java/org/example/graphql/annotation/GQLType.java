package org.example.graphql.annotation;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;

public enum GQLType {
    ENUM(null),
    OBJECT(null),
    LIST(null),
    INPUT(null),
    SCALAR_INT(Scalars.GraphQLInt),
    SCALAR_FLOAT(Scalars.GraphQLFloat),
    SCALAR_STRING(Scalars.GraphQLString),
    SCALAR_BOOLEAN(Scalars.GraphQLBoolean),
    SCALAR_ID(Scalars.GraphQLID),
    ;
    public final GraphQLScalarType graphQLScalarType;

    GQLType(GraphQLScalarType graphQLScalarType) {
        this.graphQLScalarType = graphQLScalarType;
    }

    public boolean isScalar() {
        return graphQLScalarType != null;
    }
}

