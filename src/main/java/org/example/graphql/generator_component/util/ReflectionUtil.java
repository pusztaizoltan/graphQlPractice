package org.example.graphql.generator_component.util;

import org.example.graphql.annotation.GGLField;
import org.example.graphql.annotation.GQLMutation;
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


    public static boolean isDataAccessor(Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
               (method.isAnnotationPresent(GQLMutation.class) ||
                method.isAnnotationPresent(GQLQuery.class));
    }


    private static boolean isQueryField(@NotNull Method method) {
        return Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(GGLField.class);
    }

    private static boolean isMutationField(@NotNull Method method) {
        return Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(GQLMutation.class);
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
