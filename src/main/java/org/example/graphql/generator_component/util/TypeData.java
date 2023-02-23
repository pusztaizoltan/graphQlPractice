package org.example.graphql.generator_component.util;

import graphql.Scalars;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLArg;

import javax.annotation.Nonnull;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.example.graphql.generator_component.util.ReflectionUtil.*;

public abstract class TypeData<T extends AnnotatedElement> {
    protected enum Type {ENUM, OBJECT, LIST, ARRAY, SCALAR;}
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



    public static TypeData<Method> ofMethod(@Nonnull Method method) {
        Class<?> returnType = method.getReturnType();
        Type dataType;
        Class<?> contentType;
//        GraphQLType graphQLType;
        if (SCALAR_MAP.containsKey(returnType)) {
            dataType = Type.SCALAR;
            contentType = returnType;
//            graphQLType = SCALAR_MAP.get(contentType);
        } else if (Collection.class.isAssignableFrom(returnType)) {
            dataType = Type.LIST;
            contentType = genericTypeOfMethod(method);
//            graphQLType = GraphQLList.list();
        } else if (returnType.isEnum()) {
            dataType = Type.ENUM;
            contentType = returnType;
        } else if (returnType.isArray()) {
            dataType = Type.ARRAY;
            contentType = returnType.componentType();
        } else {
            dataType = Type.OBJECT;
            contentType = returnType;
        }
        return new TypeDetails<>(dataType, contentType,method);
    }

    public static TypeData<Parameter> ofParameter(@Nonnull Parameter parameter) {
        Class<?> parameterType = parameter.getType();
        Type dataType;
        Class<?> contentType;
        if (SCALAR_MAP.containsKey(parameterType)) {
            dataType = Type.SCALAR;
            contentType = parameterType;
        } else if (Collection.class.isAssignableFrom(parameterType)) {
            dataType = Type.LIST;
            contentType = genericTypeOfParameter(parameter);
        } else if (parameterType.isEnum()) {
            dataType = Type.ENUM;
            contentType = parameterType;
        } else if (parameterType.isArray()) {
            dataType = Type.ARRAY;
            contentType = parameterType.componentType();
        } else {
            dataType = Type.OBJECT;
            contentType = parameterType;
        }
        return new TypeDetails<>(dataType, contentType,parameter);
    }

    public static TypeData<Field> ofField(@Nonnull Field field) {
        Class<?> parameterType = field.getType();
        Type dataType;
        Class<?> contentType;
        if (SCALAR_MAP.containsKey(parameterType)) {
            dataType = Type.SCALAR;
            contentType = parameterType;
        } else if (Collection.class.isAssignableFrom(parameterType)) {
            dataType = Type.LIST;
            contentType = genericTypeOfField(field);
        } else if (parameterType.isEnum()) {
            dataType = Type.ENUM;
            contentType = parameterType;
        } else if (parameterType.isArray()) {
            dataType = Type.ARRAY;
            contentType = parameterType.componentType();
        } else {
            dataType = Type.OBJECT;
            contentType = parameterType;
        }
        return new TypeDetails<>(dataType, contentType, field);
    }

    public static boolean isScalar(Class<?> classType) {
        return SCALAR_MAP.containsKey(classType);
    }

    public abstract Class<?> getContentType();

    public abstract GraphQLType getGraphQLType();

    public abstract boolean hasScalarContent();

    public String getName(){
        if (origin.isAnnotationPresent(GQLArg.class)){
            return origin.getAnnotation(GQLArg.class).name();
        } else if (origin instanceof Field){
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


}
