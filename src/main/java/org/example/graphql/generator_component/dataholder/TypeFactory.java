package org.example.graphql.generator_component.dataholder;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;

import javax.annotation.Nonnull;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

public class TypeFactory {
    protected final static Map<Class<?>, GraphQLScalarType> SCALAR_MAP = new HashMap<>();

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

    public static <E extends AnnotatedElement> @Nonnull TypeData<E> dataOf(@Nonnull E element) {
        return new TypeData<>(element);
    }

    public static <E extends AnnotatedElement> @Nonnull TypeContent<?, E> contentOf(@Nonnull E element) {
        return new TypeData<>(element).toTypeContent();
    }

    public static <E extends AnnotatedElement> @Nonnull TypeDetail<?, E> detailOf(@Nonnull E element) {
        return new TypeData<>(element).toTypeDetail();
    }
}
