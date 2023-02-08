package org.example.graphql.generator_util;

import lombok.Getter;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.FieldType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

public class ClassParser {
    @Getter
    private final HashSet<Class<?>> components = new HashSet<>();

    /**
     * Parse arbitrary client defined classes recursively
     * for all unique composite classes or enums
     */
    public void parseAdditionalClasses(Class<?>... classes) {
        for (Class<?> classType : classes) {
            parseClassesFromFields(classType);
            components.add(classType);
        }
    }

    /**
     * Parse dataService object that actually will serve as a dataSource
     * recursively for all unique composite classes or enums
     */
    public void parseClassesFromDataService(Object dataService) {
        for (Method method : MethodAdapter.queryMethodsOf(dataService)) {
            FieldType GQLType = method.getAnnotation(FieldOf.class).type();
            if (GQLType == FieldType.OBJECT) {
                recursiveUpdateBy(method.getReturnType());
            } else if (GQLType == FieldType.LIST) {
                recursiveUpdateBy(MethodAdapter.genericTypeOf(method));
            } else {
                throw new RuntimeException("Unimplemented queryParser for " + method);
            }
        }
    }

    private void parseClassesFromFields(Class<?> classType) {
        for (Field field : FieldAdapter.typeFieldsOf(classType)) {
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

    private void recursiveUpdateBy(Class<?> classType) {
        if (!components.contains(classType)) {
            components.add(classType);
            parseClassesFromFields(classType);
        }
    }
}
