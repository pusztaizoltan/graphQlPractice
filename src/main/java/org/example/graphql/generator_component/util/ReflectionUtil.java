package org.example.graphql.generator_component.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;

public class ReflectionUtil {
    /**
     * Determine the Generic Type of afield
     */
    public static Class<?> genericTypeOfField(@NotNull Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    /**
     * Determine the Generic Type of the return of a method
     */
    public static Class<?> genericTypeOfMethod(@NotNull Method method) {
        return (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
    }

    /**
     * Determine the Generic Type of the return of aan argument
     */
    public static Class<?> genericTypeOfParameter(@NotNull Parameter parameter) {
        return (Class<?>) ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
    }
}
