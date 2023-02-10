package org.example.graphql.util_generator;

import lombok.Getter;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;

import static org.example.graphql.util_adapter.ReflectionUtil.*;

public class ClassParser {
    @Getter
    private final HashSet<Class<?>> components = new HashSet<>();

    /**
     * Parse arbitrary client defined classes recursively
     * for all unique composite classes or enums
     */
    public void parseAdditionalClasses(Class<?> @NotNull ... classes) {
        for (Class<?> classType : classes) {
            parseClassesFromFields(classType);
            this.components.add(classType);
        }
    }

    /**
     * Parse dataService object that actually will serve as a dataSource
     * recursively for all unique composite classes or enums
     */
    public void parseClassesFromDataService(@NotNull Object dataService) {
        for (Method method : queryMethodsOf(dataService)) {
            GQLType GQLType = method.getAnnotation(FieldOf.class).type();
            if (GQLType == org.example.graphql.annotation.GQLType.OBJECT) {
                recursiveUpdateBy(method.getReturnType());
            } else if (GQLType == org.example.graphql.annotation.GQLType.LIST) {
                recursiveUpdateBy(genericTypeOfMethod(method));
            } else {
                throw new RuntimeException("Unimplemented queryParser for " + method);
            }
        }
    }

    public void parseInputObjectsFromDataService(@NotNull Object dataService) {
        for (Method method : mutationMethodsOf(dataService)) {
            for(Parameter parameter: imputeObjectsOf(method)){
                GQLType argType = method.getAnnotation(ArgWith.class).type();
                if (argType == GQLType.OBJECT) {
                    recursiveUpdateBy(parameter.getType());
                } else if (argType == GQLType.LIST) {
                    recursiveUpdateBy(genericTypeOfParameter(parameter));
                } else {
                    throw new RuntimeException("Unimplemented queryParser for " + method);
                }
            }


        }
    }

    private void parseClassesFromFields(@NotNull Class<?> classType) {
        for (Field field : typeFieldsOf(classType)) {
            GQLType GQLType = field.getAnnotation(FieldOf.class).type();
            if (!GQLType.isScalar()) {
                if (GQLType == org.example.graphql.annotation.GQLType.ENUM) {
                    recursiveUpdateBy(field.getType());
                } else if (GQLType == org.example.graphql.annotation.GQLType.OBJECT) {
                    recursiveUpdateBy(field.getType());
                } else if (GQLType == org.example.graphql.annotation.GQLType.LIST) {
                    recursiveUpdateBy(genericTypeOfField(field));
                } else {
                    throw new RuntimeException("Unimplemented fieldParser for " + GQLType);
                }
            }
        }
    }

    private void recursiveUpdateBy(@NotNull Class<?> classType) {
        if (!this.components.contains(classType)) {
            this.components.add(classType);
            parseClassesFromFields(classType);
        }
    }
}
