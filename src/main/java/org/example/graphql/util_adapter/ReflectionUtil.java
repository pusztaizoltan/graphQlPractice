package org.example.graphql.util_adapter;

import org.example.graphql.annotation.FieldOf;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class ReflectionUtil {
    /**
     * Select fields of a Class that qualify as GraphQL Type field
     */
    public static Field[] typeFieldsOf(Class<?> classType) {
        return Arrays.stream(classType.getDeclaredFields())
                     .filter((field) -> field.isAnnotationPresent(FieldOf.class))
                     .toArray(Field[]::new);
    }

    /**
     * Select methods of dataService instance that qualify as GraphQL Query field
     */
    public static Method[] queryMethodsOf(Object dataService) {
        return Arrays.stream(dataService.getClass().getDeclaredMethods())
                     .filter(ReflectionUtil::isQueryField)
                     .toArray(Method[]::new);
    }

    private static boolean isQueryField(Method method) {
        return Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(FieldOf.class);
    }

    /**
     * Determine the Generic Type of afield
     */
    public static Class<?> genericTypeOfField(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    /**
     * Determine the Generic Type of the return of a method
     */
    public static Class<?> genericTypeOfMethod(Method method) {
        return (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
    }
}
