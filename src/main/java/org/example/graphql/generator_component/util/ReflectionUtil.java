package org.example.graphql.generator_component.util;

import org.example.graphql.annotation.GQLType;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;

import static org.example.graphql.annotation.GQLType.*;

/**
 * Collection of methods used in multiple times in different classes and in different context
 */
public class ReflectionUtil {
    private static final String UNIMPLEMENTED_MESSAGE = "Unimplemented type collector for ";
    private ReflectionUtil() {
    }
    // TODO: the reflection util by design deals with metadata level objects such as Classes, Methods, Fields,
    // so this method also should accept a class
    // TODO: the name also is bad, the 'methodsOf' would be enough or by the classic naming 'getMethods'
    // TODO: also see the objections against the streams: {@link ListDbTestImpl#testClassById(long)}
    // todo done questioned parts no longer exists

    /**
     * Shortcut method  to determine the Generic Type of afield
     */
    public static Class<?> genericTypeOfField(@Nonnull Field field) {
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
    public static Class<?> getClassFromReturn(@Nonnull Method method) {
        GQLType returnType = ofMethod(method);
        if (returnType == OBJECT || returnType == ENUM) {
            return method.getReturnType();
        } else if (returnType == LIST) {
            return genericTypeOfMethod(method);
        } else if (returnType == ARRAY) {
            return method.getReturnType().componentType();
        } else {
            throw new UnimplementedException(UNIMPLEMENTED_MESSAGE + method.getReturnType());
        }
    }

    public static Class<?> getClassFromArgument(@Nonnull Parameter parameter) {
        GQLType argumentType = ofParameter(parameter);
        if (argumentType == OBJECT || argumentType == ENUM) {
            return parameter.getType();
        } else if (argumentType == LIST) {
            return genericTypeOfParameter(parameter);
        } else if (argumentType == ARRAY) {
            return parameter.getType().componentType();
        } else {
            throw new UnimplementedException(UNIMPLEMENTED_MESSAGE + parameter.getType());
        }
    }

    public static Class<?> getClassFromField(@Nonnull Field field) {
        GQLType fieldType = ofField(field);
        if (fieldType == OBJECT || fieldType == ENUM) {
            return field.getType();
        } else if (fieldType == LIST) {
            return genericTypeOfField(field);
        } else if (fieldType == ARRAY) {
            return field.getType().componentType();
        } else {
            throw new UnimplementedException(UNIMPLEMENTED_MESSAGE + field.getType());
        }
    }
}
