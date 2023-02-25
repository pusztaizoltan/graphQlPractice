package org.example.graphql.generator_component.dataholder;

import org.example.graphql.annotation.GQLArg;
import org.example.graphql.generator_component.util.MissingAnnotationException;
import org.example.graphql.generator_component.util.UnimplementedException;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import static org.example.graphql.generator_component.dataholder.DataFactory.SCALAR_MAP;
import static org.example.graphql.generator_component.dataholder.DataFactory.Type;

public class Data<E extends AnnotatedElement> {
    protected final Type dataType;
    private final E origin;

    public Data(Data<E> data) {
        origin = data.origin;
        dataType = data.dataType;
    }

    public Data(E element) {
        this.origin = element;
        Class<?> simpleType = getSimpleType();
        if (SCALAR_MAP.containsKey(simpleType)) {
            dataType = Type.SCALAR;
        } else if (Collection.class.isAssignableFrom(simpleType)) {
            dataType = Type.LIST;
        } else if (simpleType.isEnum()) {
            dataType = Type.ENUM;
        } else if (simpleType.isArray()) {
            dataType = Type.ARRAY;
        } else {
            dataType = Type.OBJECT;
        }
    }

    public Class<?> getContentType() {
        if (dataType == Type.ARRAY) {
            return getSimpleType().componentType();
        } else if (dataType == Type.LIST) {
            return getGenericType();
        } else {
            return getSimpleType();
        }
    }

    public boolean hasScalarContent() {
        return DataFactory.SCALAR_MAP.containsKey(getContentType());
    }

    private Class<?> getGenericType() {
        ParameterizedType type;
        if (origin instanceof Method) {
            type = (ParameterizedType) ((Method) origin).getGenericReturnType();
        } else if (origin instanceof Field) {
            type = (ParameterizedType) ((Field) origin).getGenericType();
        } else if (origin instanceof Parameter) {
            type = (ParameterizedType) ((Parameter) origin).getParameterizedType();
        } else {
            throw new UnimplementedException("");// todo give message
        }
        return (Class<?>) type.getActualTypeArguments()[0];
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

    private Class<?> getSimpleType() {
        if (origin instanceof Method) {
            return ((Method) origin).getReturnType();
        } else if (origin instanceof Field) {
            return ((Field) origin).getType();
        } else if (origin instanceof Parameter) {
            return ((Parameter) origin).getType();
        } else {
            throw new UnimplementedException("");// todo give message
        }
    }
}
