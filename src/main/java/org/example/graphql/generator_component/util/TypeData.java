package org.example.graphql.generator_component.util;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;
import org.example.graphql.annotation.GQLType;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.example.graphql.generator_component.util.ReflectionUtil.*;

public abstract class TypeData {
    private final static Map<Class<?>, GraphQLScalarType> SCALAR_MAP = new HashMap<>();

    static {
        SCALAR_MAP.put(boolean.class, Scalars.GraphQLBoolean);
        SCALAR_MAP.put(Boolean.class, Scalars.GraphQLBoolean);
        SCALAR_MAP.put(byte.class, Scalars.GraphQLInt);
        SCALAR_MAP.put(Byte.class, Scalars.GraphQLInt);
        SCALAR_MAP.put(short.class, Scalars.GraphQLInt);
        SCALAR_MAP.put(Short.class, Scalars.GraphQLInt);
        SCALAR_MAP.put(int.class, Scalars.GraphQLInt);
        SCALAR_MAP.put(Integer.class, Scalars.GraphQLInt);
        SCALAR_MAP.put(long.class, Scalars.GraphQLInt);
        SCALAR_MAP.put(Long.class, Scalars.GraphQLInt);
        SCALAR_MAP.put(float.class, Scalars.GraphQLFloat);
        SCALAR_MAP.put(Float.class, Scalars.GraphQLFloat);
        SCALAR_MAP.put(double.class, Scalars.GraphQLFloat);
        SCALAR_MAP.put(Double.class, Scalars.GraphQLFloat);
        SCALAR_MAP.put(char.class, Scalars.GraphQLInt);
        SCALAR_MAP.put(Character.class, Scalars.GraphQLInt);
        SCALAR_MAP.put(String.class, Scalars.GraphQLString);
    }

    public final GQLType gqlType;

    public abstract Class<?> getContentType();

    public static TypeDetails<?> ofMethod(@Nonnull Method method) {
        Class<?> returnType = method.getReturnType();
        GQLType gqlType;
        Class<?> contentType;
        if (SCALAR_MAP.containsKey(returnType)) {
            gqlType = GQLType.SCALAR;
            contentType = returnType;
        } else if (Collection.class.isAssignableFrom(returnType)) {
            gqlType = GQLType.LIST;
            contentType = genericTypeOfMethod(method);
        } else if (returnType.isEnum()) {
            gqlType = GQLType.ENUM;
            contentType = returnType;
        } else if (returnType.isArray()) {
            gqlType = GQLType.ARRAY;
            contentType = returnType.componentType();
        } else {
            gqlType = GQLType.OBJECT;
            contentType = returnType;
        }
        return new TypeDetails<>(gqlType, contentType);
    }

    public static TypeData ofParameter(@Nonnull Parameter parameter) {
        Class<?> parameterType = parameter.getType();
        GQLType gqlType;
        Class<?> contentType;
        if (SCALAR_MAP.containsKey(parameterType)) {
            gqlType = GQLType.SCALAR;
            contentType = parameterType;
        } else if (Collection.class.isAssignableFrom(parameterType)) {
            gqlType = GQLType.LIST;
            contentType = genericTypeOfParameter(parameter);
        } else if (parameterType.isEnum()) {
            gqlType = GQLType.ENUM;
            contentType = parameterType;
        } else if (parameterType.isArray()) {
            gqlType = GQLType.ARRAY;
            contentType = parameterType.componentType();
        } else {
            gqlType = GQLType.OBJECT;
            contentType = parameterType;
        }
        return new TypeDetails<>(gqlType, contentType);
    }

    public static TypeData ofField(@Nonnull Field field) {
        Class<?> parameterType = field.getType();
        GQLType gqlType;
        Class<?> contentType;
        if (SCALAR_MAP.containsKey(parameterType)) {
            gqlType = GQLType.SCALAR;
            contentType = parameterType;
        } else if (Collection.class.isAssignableFrom(parameterType)) {
            gqlType = GQLType.LIST;
            contentType = genericTypeOfField(field);
        } else if (parameterType.isEnum()) {
            gqlType = GQLType.ENUM;
            contentType = parameterType;
        } else if (parameterType.isArray()) {
            gqlType = GQLType.ARRAY;
            contentType = parameterType.componentType();
        } else {
            gqlType = GQLType.OBJECT;
            contentType = parameterType;
        }
        return new TypeDetails<>(gqlType, contentType);
    }
    public TypeData(GQLType gqlType) {
        this.gqlType = gqlType;
    }

}
