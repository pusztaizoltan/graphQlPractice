package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.generator_component.util.UnimplementedException;

import javax.annotation.Nonnull;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * Static Utility class used in{@link org.example.graphql.generator_component.GraphQLBuilder}
 * to automatically create DataFetcher for the Query and Mutation type methods of data-service
 * based on the signature of the method.
 */
public class FetcherFactory {
    static DataFetchingEnvironment environment;

    private FetcherFactory() {
    }

    /**
     * Factory method of the class.
     */
    public static @Nonnull DataFetcher<?> createFetcherFor(@Nonnull Method method, @Nonnull Object dataService) {
        return (DataFetchingEnvironment env) -> {
            environment = env;
            Parameter[] parameters = method.getParameters();
            Object[] arguments = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                arguments[i] = mapArgument(parameters[i]);
            }
            return method.invoke(dataService, arguments);
        };
    }

    private static @Nonnull Object mapArgument(@Nonnull Parameter parameter) {
        String argName = parameter.getAnnotation(GQLArg.class).name();
        Class<?> argumentClass = parameter.getType();
        GQLType gqlType = GQLType.ofParameter(parameter);
        if (gqlType.isScalar()) {
            return environment.getArgument(argName);
        } else if (gqlType == GQLType.ENUM) {
            return Enum.valueOf(argumentClass.asSubclass(Enum.class), environment.getArgument(argName));
        } else if (gqlType == GQLType.OBJECT && hasMapperMethod(argumentClass)) {
            return mapByStaticMapperMethod(argumentClass, argName);
        } else if (gqlType == GQLType.OBJECT && !hasMapperMethod(argumentClass)) {
            return mapBySetterMatching(argumentClass, argName);
        } else {
            throw new UnimplementedException("Unimplemented argumentMapper for" + parameter);
        }
    }

    private static @Nonnull Object mapByStaticMapperMethod(@Nonnull Class<?> argumentClass, @Nonnull String argName) {
        String exceptionMessage = "Unimplemented preferential input-wiring method with required signature for ";
        try {
            Map<String, Object> inputArgument = environment.getArgument(argName);
            return argumentClass.getMethod("fromMap", Map.class)
                                .invoke(null, inputArgument);
        } catch (IllegalAccessException |
                 InvocationTargetException |
                 NoSuchMethodException e) {
            throw new UnimplementedException(exceptionMessage + argumentClass);
        }
    }

    private static <T> @Nonnull T mapBySetterMatching(@Nonnull Class<T> argumentClass, @Nonnull String argName) {
        T inputObject = tryInstantiatingInputObject(argumentClass);
        Map<String, Object> arguments = environment.getArgument(argName);
        for (Field field : argumentClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                trySettingInputValue(inputObject, field, arguments.get(field.getName()));
            }
        }
        return inputObject;
    }

    private static <T> @Nonnull T tryInstantiatingInputObject(@Nonnull Class<T> argumentClass) {
        String exceptionMessage = "Unimplemented default constructor for secondary input-wiring solution for ";
        try {
            return argumentClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException |
                 InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException e) {
            throw new UnimplementedException(exceptionMessage + argumentClass);
        }
    }

    private static void trySettingInputValue(@Nonnull Object inputObject, @Nonnull Field property, @Nonnull Object inputValue) {
        String exceptionMessage = "Unimplemented public setter for secondary input-wiring solution for ";
        try {
            new PropertyDescriptor(property.getName(), property.getDeclaringClass())
                    .getWriteMethod()
                    .invoke(inputObject, inputValue);
        } catch (IllegalAccessException |
                 InvocationTargetException |
                 IntrospectionException e) {
            throw new UnimplementedException(exceptionMessage + property);
        }
    }

    private static boolean hasMapperMethod(@Nonnull Class<?> classType) {
        try {
            classType.getMethod("fromMap", Map.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}