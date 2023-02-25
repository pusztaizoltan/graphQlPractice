package org.example.graphql.generator_component.dataholder;

import graphql.Scalars;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;

import javax.annotation.Nonnull;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

public class TypeFactory {
    protected final static Map<Class<?>, GraphQLScalarType> OUTPUT_MAP = new HashMap<>();

    static {
        OUTPUT_MAP.put(boolean.class, Scalars.GraphQLBoolean);
        OUTPUT_MAP.put(Boolean.class, Scalars.GraphQLBoolean);
        OUTPUT_MAP.put(byte.class, ExtendedScalars.GraphQLByte);
        OUTPUT_MAP.put(Byte.class, ExtendedScalars.GraphQLByte);
        OUTPUT_MAP.put(short.class, ExtendedScalars.GraphQLShort);
        OUTPUT_MAP.put(Short.class, ExtendedScalars.GraphQLShort);
        OUTPUT_MAP.put(int.class, Scalars.GraphQLInt);
        OUTPUT_MAP.put(Integer.class, Scalars.GraphQLInt);
        OUTPUT_MAP.put(long.class, ExtendedScalars.GraphQLLong);
        OUTPUT_MAP.put(Long.class, ExtendedScalars.GraphQLLong);
        OUTPUT_MAP.put(float.class, Scalars.GraphQLFloat);
        OUTPUT_MAP.put(Float.class, Scalars.GraphQLFloat);
        OUTPUT_MAP.put(double.class, Scalars.GraphQLFloat); //todo nodouble
        OUTPUT_MAP.put(Double.class, Scalars.GraphQLFloat); //todo nodouble
        OUTPUT_MAP.put(char.class, ExtendedScalars.GraphQLChar);
        OUTPUT_MAP.put(Character.class, ExtendedScalars.GraphQLChar);
        OUTPUT_MAP.put(String.class, Scalars.GraphQLString);
    }

    public static <E extends AnnotatedElement> @Nonnull TypeData<E> dataOf(@Nonnull E element) {
        return new TypeData<>(element);
    }

    public static <E extends AnnotatedElement> @Nonnull TypeDetail<?, E> contentOf(@Nonnull E element) {
        return new TypeData<>(element).toTypeContent();
    }
}
