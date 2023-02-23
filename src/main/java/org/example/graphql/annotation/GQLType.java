package org.example.graphql.annotation;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;

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
public enum GQLType {
    ENUM, OBJECT, LIST, ARRAY, SCALAR;
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


    /**
     * ShortCut method to differentiate between simple and complex GraphQl schema elements
     */
    public boolean isScalar() {
        return this == SCALAR;
    }

    public static boolean isScalar(Class<?> classType) {
        return map.containsKey(classType);
    }

    public static GraphQLScalarType getScalar(Class<?> classType) {
        return map.get(classType);
    }
}

