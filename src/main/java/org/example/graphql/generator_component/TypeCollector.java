package org.example.graphql.generator_component;

import lombok.Getter;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.generator_component.util.UnimplementedException;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;

import static org.example.graphql.generator_component.util.ReflectionUtil.*;
// TODO: I think the 'parse' term is misleading, because in this case you build a model
// from an already parsed class. The class is parsed by the Java compiler
// TODO: as I see the purpose of this class is more like a 'model class holder' because it's only functionality to
// collect the types from methods returns and store in a HashMap
// todo done renamed

/**
 * Class responsible for collecting java types as schema type base
 * weather defined by client or automatically extracted from provided
 * data-service method.
 */
public class TypeCollector {
    private static final String UNIMPLEMENTED_MESSAGE = "Unimplemented type collector for ";
    @Getter
    private final HashSet<Class<?>> components = new HashSet<>();

    /**
     * Collect unique composite classes or enums recursively
     * from the return type of data-service method, which are
     * required as schema-components for processing the
     * data-service call.
     */
    public void collectTypesFromServiceMethodReturn(@Nonnull Method method) {
        GQLType returnType = GQLType.ofMethod(method);
        if (!returnType.isScalar()) {
            System.out.println("ret: " +method.getReturnType());
            collectRecursivelyFromClassFields(getClassFromReturn(method, returnType));
        }
    }

    /**
     * Collect unique composite classes or enums recursively
     * from the arguments of data-service method, which are
     * required as schema-components for processing the
     * data-service call.
     */
    public void collectTypesFromServiceMethodArguments(@Nonnull Method method) {
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(GQLArg.class)) {
                GQLType argumentType = GQLType.ofParameter(parameter);
                if (!argumentType.isScalar()) {
                    System.out.println("arg: " +parameter.getType());
                    collectRecursivelyFromClassFields(getClassFromArgument(parameter, argumentType));
                }
            }
        }
    }

    /**
     * Collect unique composite classes or enums from
     * input classes arbitrarily defined by client
     */
    public void collectAdditionalTypesFromClasses(@Nonnull Class<?>... classes) {
        for (Class<?> classType : classes) {
            collectTypesFromClassFields(classType);
        }
    }

    private <T> void collectRecursivelyFromClassFields(@Nonnull Class<T> classType) {
        if (!this.components.contains(classType)) {
            this.components.add(classType);
            System.out.println(classType);
            collectTypesFromClassFields(classType);
        }
    }

    private <T> void collectTypesFromClassFields(@Nonnull Class<T> classType) {
        for (Field field : classType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                GQLType fieldType = GQLType.ofField(field);
                if (!fieldType.isScalar()) {
                    System.out.println("field: " +field.getType());
                    collectRecursivelyFromClassFields(getClassFromField(field, fieldType));
                }
            }
        }
    }

    private Class<?> getClassFromReturn(@Nonnull Method method, GQLType returnType) {
        if (returnType == GQLType.OBJECT || returnType == GQLType.ENUM) {
            return method.getReturnType();
        } else if (returnType == GQLType.LIST) {
            return genericTypeOfMethod(method);
        } else if (returnType == GQLType.ARRAY) {
            return method.getReturnType().componentType();
        } else {
            throw new UnimplementedException(UNIMPLEMENTED_MESSAGE + method.getReturnType());
        }
    }

    private Class<?> getClassFromArgument(@Nonnull Parameter parameter, GQLType argumentType) {
        if (argumentType == GQLType.OBJECT || argumentType == GQLType.ENUM) {
            return parameter.getType();
        } else if (argumentType == GQLType.LIST) {
            return genericTypeOfParameter(parameter);
        } else if (argumentType == GQLType.ARRAY) {
            return parameter.getType().componentType();
        } else {
            throw new UnimplementedException(UNIMPLEMENTED_MESSAGE + parameter.getType());
        }
    }

    private Class<?> getClassFromField(@Nonnull Field field, GQLType fieldType) {
        if (fieldType == GQLType.OBJECT || fieldType == GQLType.ENUM) {
            return field.getType();
        } else if (fieldType == GQLType.LIST) {
            return genericTypeOfField(field);
        } else if (fieldType == GQLType.ARRAY) {
            return field.getType().componentType();
        } else {
            throw new UnimplementedException(UNIMPLEMENTED_MESSAGE + field.getType());
        }
    }
}
