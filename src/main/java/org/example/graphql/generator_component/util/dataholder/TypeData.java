package org.example.graphql.generator_component.util.dataholder;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.generator_component.util.MissingAnnotationException;
import org.example.graphql.generator_component.util.UnimplementedException;

import javax.annotation.Nonnull;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TypeData<T extends AnnotatedElement> {
    protected enum Type {ENUM, OBJECT, LIST, ARRAY, SCALAR}

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

    private final Type dataType;
    private final T origin;

    public TypeData(Type dataType, T annotatedElement) {
        this.dataType = dataType;
        this.origin = annotatedElement;
    }

    public static <A extends AnnotatedElement>TypeData<A> of(@Nonnull A element) {
        Class<?> simpleType = getSimpleType(element);
        TypeData<A> typeData;
        Class<?> contentType;
        if (SCALAR_MAP.containsKey(simpleType)) {
            typeData = new TypeData<>(Type.SCALAR, element);
            contentType = simpleType;
        } else if (Collection.class.isAssignableFrom(simpleType)) {
            typeData = new TypeData<>(Type.LIST, element);
            contentType = getGenericType(element);
        } else if (simpleType.isEnum()) {
            typeData = new TypeData<>(Type.ENUM, element);
            contentType = simpleType;
        } else if (simpleType.isArray()) {
            typeData = new TypeData<>(Type.ARRAY, element);
            contentType = simpleType.componentType();
        } else {
            typeData = new TypeData<>(Type.OBJECT, element);
            contentType = simpleType;
        }
        return new TypeDetails<>(typeData.dataType, contentType, element);
    }

    public static boolean isScalar(Class<?> classType) {
        return SCALAR_MAP.containsKey(classType);
    }

    public Class<?> getContentType() {
        return ((TypeDetails<?, T>) this).getContentType();
    }

    public GraphQLType getGraphQLType() {
        return this.getGraphQLType();
    }

    public boolean hasScalarContent() {
        return this.hasScalarContent();
    }

    public String getName() {
        if (origin.isAnnotationPresent(GQLArg.class)) {
            return origin.getAnnotation(GQLArg.class).name();
        } else if (origin instanceof Field) {
            return ((Field) origin).getName();
        } else if (origin instanceof Method) {
            return ((Method) origin).getName();
        } else {
            throw new MissingAnnotationException("");
        }
    }

    public T getOrigin() {
        return origin;
    }

    public boolean isScalar() {
        return this.dataType == Type.SCALAR;
    }

    public boolean isEnum() {
        return this.dataType == Type.ENUM;
    }

    public boolean isList() {
        return this.dataType == Type.LIST;
    }

    public boolean isArray() {
        return this.dataType == Type.ARRAY;
    }

    public boolean isObject() {
        return this.dataType == Type.OBJECT;
    }

    public static Class<?> getSimpleType(AnnotatedElement element) {
        if (element instanceof Method) {
            return ((Method) element).getReturnType();
        } else if (element instanceof Field) {
            return ((Field) element).getType();
        } else if (element instanceof Parameter) {
            return ((Parameter) element).getType();
        } else {
            throw new UnimplementedException("");// todo give message
        }
    }

    public static Class<?> getGenericType(AnnotatedElement element) {
        ParameterizedType type;
        if (element instanceof Method) {
            type = (ParameterizedType) ((Method) element).getGenericReturnType();
        } else if (element instanceof Field) {
            type = (ParameterizedType) ((Field) element).getGenericType();
        } else if (element instanceof Parameter) {
            type = (ParameterizedType) ((Parameter) element).getParameterizedType();
        } else {
            throw new UnimplementedException("");// todo give message
        }
        return (Class<?>) type.getActualTypeArguments()[0];
    }
}
