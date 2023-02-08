package org.example.graphql.util_generator;

import lombok.Getter;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.FieldType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

import static org.example.graphql.util_adapter.ReflectionUtil.*;

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
            this.components.add(classType);
        }
    }

    /**
     * Parse dataService object that actually will serve as a dataSource
     * recursively for all unique composite classes or enums
     */
    public void parseClassesFromDataService(Object dataService) {
        for (Method method : queryMethodsOf(dataService)) {
            FieldType GQLType = method.getAnnotation(FieldOf.class).type();
            if (GQLType == FieldType.OBJECT) {
                recursiveUpdateBy(method.getReturnType());
            } else if (GQLType == FieldType.LIST) {
                recursiveUpdateBy(genericTypeOfMethod(method));
            } else {
                throw new RuntimeException("Unimplemented queryParser for " + method);
            }
        }
    }

    private void parseClassesFromFields(Class<?> classType) {
        for (Field field : typeFieldsOf(classType)) {
            FieldType GQLType = field.getAnnotation(FieldOf.class).type();
            if (!GQLType.isScalar()) {
                if (GQLType == FieldType.ENUM) {
                    recursiveUpdateBy(field.getType());
                } else if (GQLType == FieldType.OBJECT) {
                    recursiveUpdateBy(field.getType());
                } else if (GQLType == FieldType.LIST) {
                    recursiveUpdateBy(genericTypeOfField(field));
                } else {
                    throw new RuntimeException("Unimplemented fieldParser for " + GQLType);
                }
            }
        }
    }

    private void recursiveUpdateBy(Class<?> classType) {
        if (!this.components.contains(classType)) {
            this.components.add(classType);
            parseClassesFromFields(classType);
        }
    }
}
