package org.example.graphql.generator_component.dataholder;

import lombok.Getter;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.generator_component.util.MissingAnnotationException;
import org.example.graphql.generator_component.util.UnimplementedException;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import static org.example.graphql.generator_component.dataholder.TypeFactory.OUTPUT_MAP;

public class TypeData<E extends AnnotatedElement> {
    Class<?> simpleType;
    @Getter
    private final E origin;

    public TypeData(E element) {
        this.origin = element;
        this.simpleType = getSimpleType();
    }

    public TypeDetail<?, E> toTypeDetail() {
        return new TypeDetail<>(this.origin, getContentClass());
    }

    public Class<?> getContentClass() {
        if (this.isArray()) {
            return getSimpleType().componentType();
        } else if (this.isList()) {
            return getGenericType();
        } else {
            return getSimpleType();
        }
    }

    public boolean hasScalarContent() {
        return TypeFactory.OUTPUT_MAP.containsKey(getContentClass());
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

    public boolean isScalar() {
        return OUTPUT_MAP.containsKey(simpleType);
    }

    public boolean isEnum() {
        return this.simpleType.isEnum();
    }

    public boolean isList() {
        return Collection.class.isAssignableFrom(simpleType);
    }

    public boolean isArray() {
        return this.simpleType.isArray();
    }

    public boolean isObject() {
        // todo
        return !isScalar() && !isEnum() && !isList() && !isArray();
    }
}
