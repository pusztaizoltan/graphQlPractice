package org.example.graphql.generator_component;

import lombok.Getter;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.QGLField;
import org.example.graphql.annotation.GQLType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;

import static org.example.graphql.generator_component.util.ReflectionUtil.*;

public class TypeCollector {
    @Getter
    private final HashSet<Class<?>> components = new HashSet<>();

    /**
     * Parse arbitrary client defined classes recursively
     * for all unique composite classes or enums
     */
    public void collectAdditionalTypesFromClasses(Class<?> @NotNull ... classes) {
        for (Class<?> classType : classes) {
            collectTypesFromClassFields(classType);
            this.components.add(classType);
        }
    }

    public void collectTypesFromServiceMethodReturn(@NotNull Method method) {
        GQLType returnType = GQLType.ofMethod(method);
        if (!returnType.isScalar()) {
            recursiveUpdateBy(getClassFromReturn(method, returnType));
        }
    }

    public void collectTypesFromServiceMethodArguments(@NotNull Method method) {
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(ArgWith.class)) {
                GQLType argumentType = GQLType.ofParameter(parameter);
                if (!argumentType.isScalar()) {
                    recursiveUpdateBy(getClassFromArgument(parameter, argumentType));
                }
            }
        }
    }

    private void collectTypesFromClassFields(@NotNull Class<?> classType) {
        for (Field field : classType.getDeclaredFields()) {
            if (field.isAnnotationPresent(QGLField.class)) {
                GQLType fieldType = GQLType.ofField(field);
                if (!fieldType.isScalar()) {
                    recursiveUpdateBy(getClassFromField(field, fieldType));
                }
            }
        }
    }

    private void recursiveUpdateBy(@NotNull Class<?> classType) {
        if (!this.components.contains(classType)) {
            this.components.add(classType);
            collectTypesFromClassFields(classType);
        }
    }

    private Class<?> getClassFromReturn(Method method, GQLType returnType) {
        if (returnType == GQLType.OBJECT || returnType == GQLType.ENUM) {
            return method.getReturnType();
        } else if (returnType == GQLType.LIST) {
            return genericTypeOfMethod(method);
        } else {
            throw new RuntimeException("Unimplemented queryParser for " + method.getReturnType());
        }
    }

    private Class<?> getClassFromArgument(Parameter parameter, GQLType argumentType) {
        if (argumentType == GQLType.OBJECT || argumentType == GQLType.ENUM) {
            return parameter.getType();
        } else if (argumentType == GQLType.LIST) {
            return genericTypeOfParameter(parameter);
        } else {
            throw new RuntimeException("Unimplemented queryParser for " + parameter.getType());
        }
    }

    private Class<?> getClassFromField(Field field, GQLType fieldType) {
        if (fieldType == GQLType.OBJECT || fieldType == GQLType.ENUM) {
            return field.getType();
        } else if (fieldType == GQLType.LIST) {
            return genericTypeOfField(field);
        } else {
            throw new RuntimeException("Unimplemented queryParser for " + field.getType());
        }
    }
}
