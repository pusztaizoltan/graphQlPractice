package org.example.graphql.generator_component.util;

import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.QGLField;
import org.example.graphql.annotation.QGLMutation;
import org.example.graphql.annotation.GQLQuery;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class ReflectionUtil {
    /**
     * Select methods of dataService instance that qualify as GraphQL Query field
     */
    public static Method @NotNull [] queryMethodsOf(@NotNull Object dataService) {
        return Arrays.stream(dataService.getClass().getDeclaredMethods())
                     .filter(ReflectionUtil::isQueryField)
                     .toArray(Method[]::new);
    }

    /**
     * Select methods of dataService instance that qualify as GraphQL Mutation field
     */
    public static Method @NotNull [] mutationMethodsOf(@NotNull Object dataService) {
        return Arrays.stream(dataService.getClass().getDeclaredMethods())
                     .filter(ReflectionUtil::isMutationField)
                     .toArray(Method[]::new);
    }


    public static boolean isQueryOrMutation(Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
               (method.isAnnotationPresent(QGLMutation.class) ||
                method.isAnnotationPresent(GQLQuery.class));
    }


    private static boolean isQueryField(@NotNull Method method) {
        return Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(QGLField.class);
    }

    private static boolean isMutationField(@NotNull Method method) {
        return Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(QGLMutation.class);
    }


    private static boolean isImputeObjects(@NotNull Parameter parameter) {
        return parameter.isAnnotationPresent(ArgWith.class) &&
               !parameter.getAnnotation(ArgWith.class).type().isScalar();
    }

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
