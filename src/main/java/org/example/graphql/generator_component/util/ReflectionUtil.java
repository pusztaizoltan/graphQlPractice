package org.example.graphql.generator_component.util;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;

/**
 * Collection of methods used in multiple times in different classes and in different context
 */
public class ReflectionUtil {
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
}
