package org.example.graphql.annotation;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;
import org.example.graphql.generator_component.util.MissingAnnotationException;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Type category constants to facilitate the conversion
 * between java types and GraphGl schema components
 */
@SuppressWarnings("unused")
public enum GQLType {
    ENUM(null),
    OBJECT(null),
    LIST(null),
    ARRAY(null),
    SCALAR(null),
    SCALAR_INT(Scalars.GraphQLInt),
    SCALAR_FLOAT(Scalars.GraphQLFloat),
    SCALAR_STRING(Scalars.GraphQLString),
    SCALAR_BOOLEAN(Scalars.GraphQLBoolean),
    SCALAR_ID(Scalars.GraphQLID),
    ;
    private static final String UNANNOTATED_EXCEPTION = "Parsing attempt of unannotated ";
    private final static Map<Class<?>, GraphQLScalarType> map = new HashMap<>();

    static {
        map.put(boolean.class, Scalars.GraphQLBoolean);
        map.put(Boolean.class, Scalars.GraphQLBoolean);
        map.put(byte.class, Scalars.GraphQLInt);
        map.put(Byte.class, Scalars.GraphQLInt);
        map.put(short.class, Scalars.GraphQLInt);
        map.put(Short.class, Scalars.GraphQLInt);
        map.put(int.class, Scalars.GraphQLInt);
        map.put(Integer.class, Scalars.GraphQLInt);
        map.put(long.class, Scalars.GraphQLInt);
        map.put(Long.class, Scalars.GraphQLInt);
        map.put(float.class, Scalars.GraphQLFloat);
        map.put(Float.class, Scalars.GraphQLFloat);
        map.put(double.class, Scalars.GraphQLFloat);
        map.put(Double.class, Scalars.GraphQLFloat);
        map.put(char.class, Scalars.GraphQLInt);
        map.put(Character.class, Scalars.GraphQLInt);
        map.put(String.class, Scalars.GraphQLString);
    }

    public final GraphQLScalarType graphQLScalarType;

    GQLType(GraphQLScalarType graphQLScalarType) {
        this.graphQLScalarType = graphQLScalarType;
    }

    /**
     * ShortCut method to access the GQLType of a method annotation
     */
    public static GQLType ofMethod(@Nonnull Method method) {
        if(map.containsKey(method.getReturnType())){
            return SCALAR;
        } else if (Collection.class.isAssignableFrom(method.getReturnType())) {
            return LIST;
        } else if (method.getReturnType().isEnum()) {
            return ENUM;
        } else if (method.getReturnType().isArray()) {
            return ARRAY;
        } else{
            return OBJECT;
        }
//        if (method.isAnnotationPresent(GQLQuery.class)) {
//            return method.getAnnotation(GQLQuery.class).type();
//        } else if (method.isAnnotationPresent(GQLMutation.class)) {
//            return method.getAnnotation(GQLMutation.class).type();
//        } else {
//            throw new MissingAnnotationException(UNANNOTATED_EXCEPTION + "method: " + method);
//        }
    }

    /**
     * ShortCut method to access the GQLType of a parameter annotation
     */
    public static GQLType ofParameter(@Nonnull Parameter parameter) {
        if(map.containsKey(parameter.getType())){
            return SCALAR;
        } else if (Collection.class.isAssignableFrom(parameter.getType())) {
            return LIST;
        } else if (parameter.getType().isEnum()) {
            return ENUM;
        } else if (parameter.getType().isArray()) {
            return ARRAY;
        } else{
            return OBJECT;
        }
//        if (parameter.isAnnotationPresent(GQLArg.class)) {
//            return parameter.getAnnotation(GQLArg.class).type();
//        } else {
//            throw new MissingAnnotationException(UNANNOTATED_EXCEPTION + "parameter: " + parameter);
//        }
    }

    /**
     * ShortCut method to access the GQLType of a field annotation
     */
    public static GQLType ofField(@Nonnull Field field) {
        if(map.containsKey(field.getType())){
            return SCALAR;
        } else if (Collection.class.isAssignableFrom(field.getType())) {
            return LIST;
        } else if (field.getType().isEnum()) {
            return ENUM;
        } else if (field.getType().isArray()) {
            return ARRAY;
        } else{
            return OBJECT;
        }
//        if (field.isAnnotationPresent(GQLField.class)) {
//            return field.getAnnotation(GQLField.class).type();
//        } else {
//            throw new MissingAnnotationException(UNANNOTATED_EXCEPTION + "field: " + field);
//        }
    }

    /**
     * ShortCut method to differentiate between simple and complex GraphQl schema elements
     */
    public boolean isScalar() {
//        return this.graphQLScalarType != null;
        return this == SCALAR;
    }

    public static boolean isScalar(Class<?> classType) {
        return map.containsKey(classType);
    }
    public static GraphQLScalarType getScalar(Class<?> classType) {
        return map.get(classType);
    }
}

