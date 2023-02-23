package org.example.graphql.generator_component.util;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;
import org.example.graphql.annotation.GQLType;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class TypeHolder<T> {
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


//    public static <T> TypeHolder<T> ofMethod(@Nonnull Method method){
//        if (SCALAR_MAP.containsKey(method.getReturnType())) {
//            var aa = method.getReturnType();
//            return new TypeScalar<T>(aa);
//        } else {
//            throw new UnimplementedException("");
//        }
//    }



    public TypeHolder(Class<T> contentType) {
        this.gqlType = GQLType.ofClass(contentType);
    }

    public boolean isScalar() {
        return gqlType == GQLType.SCALAR;
    }

    public boolean isEnum() {
        return gqlType == GQLType.ENUM;
    }

    public boolean isObject() {
        return gqlType == GQLType.OBJECT;
    }

    public boolean isList() {
        return gqlType == GQLType.LIST;
    }

    public boolean isArray() {
        return gqlType == GQLType.ARRAY;
    }
}
