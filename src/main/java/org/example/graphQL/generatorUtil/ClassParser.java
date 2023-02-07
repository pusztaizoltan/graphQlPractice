package org.example.graphQL.generatorUtil;

import lombok.Getter;
import org.example.graphQL.annotation.FieldOf;
import org.example.graphQL.annotation.FieldType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;

public class ClassParser {

    @Getter
    private final HashSet<Class<?>> components = new HashSet<>();

    public void parseAdditionalClasses(Class<?>... classes) {
        for (Class<?> classType : classes) {
            parseNestedClasses(classType);
            components.add(classType);
        }
    }

    public void parseClassesFromDataService(Object dataService) {
        Method[] methods = dataService.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers()) || !method.isAnnotationPresent(FieldOf.class)) {
                continue;
            }
            FieldType fieldType = method.getAnnotation(FieldOf.class).type();
            if (fieldType == FieldType.LIST || fieldType == FieldType.OBJECT) {
                Class<?> type;
                if (fieldType == FieldType.LIST) {
                    type = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                } else {
                    type = method.getReturnType();
                }
                if (!components.contains(type)) {
                    components.add(type);
                    parseNestedClasses(type);
                }
            }
        }
    }

    private void parseNestedClasses(Class<?> classType) {
        for (Field field : classType.getDeclaredFields()) {
            if (!field.isAnnotationPresent(FieldOf.class)) {
                continue;
            }
            FieldType fieldType = field.getAnnotation(FieldOf.class).type();
            Class<?> type;
            if (fieldType == FieldType.ENUM) {
                type = field.getType();
                components.add(type);
            } else if (fieldType == FieldType.OBJECT) {
                type = field.getType();
                if (!components.contains(type)) {
                    components.add(type);
                    parseNestedClasses(type);
                }
            } else if (fieldType == FieldType.LIST) {
                type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                if (!components.contains(type)) {
                    components.add(type);
                    parseNestedClasses(type);
                }
            } else if (fieldType.isScalar()) {
                continue;
            } else {
                throw new RuntimeException("Unimplemented fieldParser for " + fieldType);
            }
        }
    }
}
