package org.example.graphql.generator_component;

import lombok.Getter;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.generator_component.util.TypeData;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;

import static org.example.graphql.annotation.GQLType.*;
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

    @Getter
    private final HashSet<Class<?>> components = new HashSet<>();

    /**
     * Collect unique composite classes or enums recursively
     * from the return type of data-service method, which are
     * required as schema-components for processing the
     * data-service call.
     */
    public void collectTypesFromServiceMethodReturn(@Nonnull Method method) {
        if (!isScalar(method.getReturnType())) {
            collectRecursivelyFromClassFields(TypeData.ofMethod(method).getContentType());
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
                if (!isScalar(parameter.getType())) {
                    collectRecursivelyFromClassFields(TypeData.ofParameter(parameter).getContentType());
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
            if (!isScalar(classType)) {
                this.components.add(classType);
                collectTypesFromClassFields(classType);
            }
        }
    }

    private <T> void collectTypesFromClassFields(@Nonnull Class<T> classType) {
        for (Field field : classType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                if (!isScalar(field.getType())) {
                    collectRecursivelyFromClassFields(TypeData.ofField(field).getContentType());
                }
            }
        }
    }
}
