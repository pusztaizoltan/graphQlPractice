package org.example.graphql.generator_component.util;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLArg;

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

    public static <T extends AnnotatedElement> TypeData<T> of(@Nonnull T annotatedElement) {
        return null;
    }

    public static TypeData<Method> ofMethod(@Nonnull Method method) {
        Class<?> simpleType = getSimpleType(method);
        TypeData typeData;
        Class<?> contentType;
        if (SCALAR_MAP.containsKey(simpleType)) {
            typeData = new TypeData(Type.SCALAR, method);
            contentType = simpleType;
        } else if (Collection.class.isAssignableFrom(simpleType)) {
            typeData = new TypeData(Type.LIST, method);
            contentType = genericTypeOfMethod(method);
        } else if (simpleType.isEnum()) {
            typeData = new TypeData(Type.ENUM, method);
            contentType = simpleType;
        } else if (simpleType.isArray()) {
            typeData = new TypeData(Type.ARRAY, method);
            contentType = simpleType.componentType();
        } else {
            typeData = new TypeData(Type.OBJECT, method);
            contentType = simpleType;
        }
        return new TypeDetails<>(typeData.dataType, contentType, method);
    }

    public static TypeData<Parameter> ofParameter(@Nonnull Parameter parameter) {
        Class<?> simpleType = getSimpleType(parameter);
        Type dataType;
        Class<?> contentType;
        if (SCALAR_MAP.containsKey(simpleType)) {
            dataType = Type.SCALAR;
            contentType = simpleType;
        } else if (Collection.class.isAssignableFrom(simpleType)) {
            dataType = Type.LIST;
            contentType = genericTypeOfParameter(parameter);
        } else if (simpleType.isEnum()) {
            dataType = Type.ENUM;
            contentType = simpleType;
        } else if (simpleType.isArray()) {
            dataType = Type.ARRAY;
            contentType = simpleType.componentType();
        } else {
            dataType = Type.OBJECT;
            contentType = simpleType;
        }
        return new TypeDetails<>(dataType, contentType, parameter);
    }

    public static TypeData<Field> ofField(@Nonnull Field field) {
        Class<?> simpleType = getSimpleType(field);
        Type dataType;
        Class<?> contentType;
        if (SCALAR_MAP.containsKey(simpleType)) {
            dataType = Type.SCALAR;
            contentType = simpleType;
        } else if (Collection.class.isAssignableFrom(simpleType)) {
            dataType = Type.LIST;
            contentType = genericTypeOfField(field);
        } else if (simpleType.isEnum()) {
            dataType = Type.ENUM;
            contentType = simpleType;
        } else if (simpleType.isArray()) {
            dataType = Type.ARRAY;
            contentType = simpleType.componentType();
        } else {
            dataType = Type.OBJECT;
            contentType = simpleType;
        }
        return new TypeDetails<>(dataType, contentType, field);
    }

    public static boolean isScalar(Class<?> classType) {
        return SCALAR_MAP.containsKey(classType);
    }

    public Class<?> getContentType() {
        return ((TypeDetails<?, T>) this).getContentType();
    }

    public GraphQLType getGraphQLType(){
        return ((TypeDetails<?, T>) this).getGraphQLType();
    }


    public boolean hasScalarContent(){
        return ((TypeDetails<?, T>) this).hasScalarContent();
    };

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

    /**
     * Shortcut method  to determine the Generic Type of afield
     */
    private static Class<?> genericTypeOfField(@Nonnull Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    /**
     * Shortcut method  to determine the Generic Type of the return of a method
     */
    public static Class<?> genericTypeOfMethod(@Nonnull Method method) {
        return (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
    }

    /**
     * Shortcut method  to determine the Generic Type of the return of aan argument
     */
    public static Class<?> genericTypeOfParameter(@Nonnull Parameter parameter) {
        return (Class<?>) ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
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
}
