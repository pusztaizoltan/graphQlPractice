package org.example.graphQL.generatorUtil;

import lombok.Getter;
import org.example.graphQL.annotation.FieldOf;
import org.example.graphQL.annotation.FieldType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

public class ClassParser {
    @Getter
    private final HashSet<Class<?>> components = new HashSet<>();

    public void parseAdditionalClasses(Class<?>... classes) {
        for (Class<?> classType : classes) {
            parseClassesFromFields(classType);
            components.add(classType);
        }
    }

    public void parseClassesFromDataService(Object dataService) {
        for (Method method : dataService.getClass().getDeclaredMethods()) {
            if (FieldAdapter.isQueryField(method)) {
                FieldType GQLType = method.getAnnotation(FieldOf.class).type();
                if (GQLType == FieldType.OBJECT) {
                    recursiveUpdateBy(method.getReturnType());
                } else if (GQLType == FieldType.LIST) {
                    recursiveUpdateBy(FieldAdapter.genericTypeOf(method));
                } else {
                    throw new RuntimeException("Unimplemented queryParser for " + method);
                }
            }
        }
    }

    private void parseClassesFromFields(Class<?> classType) {
        for (Field field : classType.getDeclaredFields()) {
            if (field.isAnnotationPresent(FieldOf.class)) {
                FieldType GQLType = field.getAnnotation(FieldOf.class).type();
                if (!GQLType.isScalar()) {
                    if (GQLType == FieldType.ENUM) {
                        recursiveUpdateBy(field.getType());
                    } else if (GQLType == FieldType.OBJECT) {
                        recursiveUpdateBy(field.getType());
                    } else if (GQLType == FieldType.LIST) {
                        recursiveUpdateBy(FieldAdapter.genericTypeOf(field));
                    } else {
                        throw new RuntimeException("Unimplemented fieldParser for " + GQLType);
                    }
                }
            }
        }
    }

    private void recursiveUpdateBy(Class<?> classType) {
        if (!components.contains(classType)) {
            components.add(classType);
            parseClassesFromFields(classType);
        }
    }
}
