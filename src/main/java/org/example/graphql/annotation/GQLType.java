package org.example.graphql.annotation;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public enum GQLType {
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

    GQLType(GraphQLScalarType graphQLScalarType) {
        this.graphQLScalarType = graphQLScalarType;
    }

    public boolean isScalar() {
        return graphQLScalarType != null;
    }

    public static GQLType ofMethod(Method method) {
        if (method.isAnnotationPresent(GQLQuery.class)) {
            return method.getAnnotation(GQLQuery.class).type();
        } else {
            return method.getAnnotation(GQLMutation.class).type();
        }
    }

    public static GQLType ofParameter(Parameter parameter) {
        return parameter.getAnnotation(ArgWith.class).type();
    }

    public static GQLType ofField(Field field) {
        return field.getAnnotation(GGLField.class).type();
    }
}

