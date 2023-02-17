package org.example.graphql.annotation;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;
import org.example.graphql.generator_component.util.MissingAnnotationException;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Type category constants to facilitate the conversion
 * between java types and GraphGl schema components
 */
@SuppressWarnings("unused")
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
    private static final String UNANNOTATED_EXCEPTION = "Parsing attempt of unannotated ";
    public final GraphQLScalarType graphQLScalarType;

    GQLType(GraphQLScalarType graphQLScalarType) {
        this.graphQLScalarType = graphQLScalarType;
    }

    /**
     * ShortCut method to access the GQLType of a method annotation
     */
    public static GQLType ofMethod(@Nonnull Method method) {
        if (method.isAnnotationPresent(GQLQuery.class)) {
            return method.getAnnotation(GQLQuery.class).type();
        } else if (method.isAnnotationPresent(GQLMutation.class)) {
            return method.getAnnotation(GQLMutation.class).type();
        } else {
            throw new MissingAnnotationException(UNANNOTATED_EXCEPTION + "method: " + method);
        }
    }

    /**
     * ShortCut method to access the GQLType of a parameter annotation
     */
    public static GQLType ofParameter(@Nonnull Parameter parameter) {
        if (parameter.isAnnotationPresent(GQLArg.class)) {
            return parameter.getAnnotation(GQLArg.class).type();
        } else {
            throw new MissingAnnotationException(UNANNOTATED_EXCEPTION + "parameter: " + parameter);
        }
    }

    /**
     * ShortCut method to access the GQLType of a field annotation
     */
    public static GQLType ofField(@Nonnull Field field) {
        if (field.isAnnotationPresent(GQLField.class)) {
            return field.getAnnotation(GQLField.class).type();
        } else {
            throw new MissingAnnotationException(UNANNOTATED_EXCEPTION + "field: " + field);
        }
    }

    /**
     * ShortCut method to differentiate between simple and complex GraphQl schema elements
     */
    public boolean isScalar() {
        return graphQLScalarType != null;
    }
}

