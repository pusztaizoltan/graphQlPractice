package org.example.graphql.generator_component;

import lombok.Getter;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;

import static org.example.graphql.generator_component.util.ReflectionUtil.*;

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
            GQLType gqlType = method.getAnnotation(FieldOf.class).type();
            if (gqlType == GQLType.OBJECT || gqlType == GQLType.INPUT) {
                recursiveUpdateBy(method.getReturnType());
            } else if (gqlType == GQLType.LIST) {
                recursiveUpdateBy(genericTypeOfMethod(method));
            } else {
                throw new RuntimeException("Unimplemented queryParser for " + method);
            }
        }
    }

    // todo consider consolidate it into parseClassesFromDataService to
    // parse not only Mutate methods but Query methods too for argument classes
    public void parseInputObjectsFromDataService(@NotNull Object dataService) {
        for (Method method : mutationMethodsOf(dataService)) {
            for (Parameter parameter : imputeObjectsOf(method)) {
                GQLType gqlType = parameter.getAnnotation(ArgWith.class).type();
                if (gqlType == GQLType.OBJECT || gqlType == GQLType.INPUT) {
                    recursiveUpdateBy(parameter.getType());
                } else if (gqlType == GQLType.LIST) {
                    recursiveUpdateBy(genericTypeOfParameter(parameter));
                } else {
                    throw new RuntimeException("Unimplemented queryParser for " + method);
                }
            }
        }
    }

    private void parseClassesFromFields(@NotNull Class<?> classType) {
        for (Field field : typeFieldsOf(classType)) {
            GQLType gqlType = field.getAnnotation(FieldOf.class).type();
            if (!gqlType.isScalar()) {
                if (gqlType == GQLType.ENUM) {
                    recursiveUpdateBy(field.getType());
                } else if (gqlType == GQLType.OBJECT || gqlType == GQLType.INPUT) {
                    recursiveUpdateBy(field.getType());
                } else if (gqlType == GQLType.LIST) {
                    recursiveUpdateBy(genericTypeOfField(field));
                } else {
                    throw new RuntimeException("Unimplemented fieldParser for " + gqlType);
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
