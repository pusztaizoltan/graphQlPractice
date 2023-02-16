package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLType;

import javax.annotation.Nonnull;
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

    private static Object mapArgument(Parameter parameter) {
        String argName = parameter.getAnnotation(GQLArg.class).name();
        Class<?> argType = parameter.getType();
        GQLType gqlType = GQLType.ofParameter(parameter);
        if (gqlType.isScalar()) {
            return environment.getArgument(argName);
        } else if (gqlType == GQLType.ENUM) {
            return Enum.valueOf(argType.asSubclass(Enum.class), environment.getArgument(argName));
        } else if (gqlType == GQLType.OBJECT && hasMapperMethod(argType)) {
            return mapByStaticMapperMethod(argType, argName);
        } else if (gqlType == GQLType.OBJECT && !hasMapperMethod(argType)) {
            return mapByFieldMatching(argType, argName);
        } else {
            throw new RuntimeException("Unimplemented argumentMapper for" + parameter);
        }
    }

    private static Object mapByStaticMapperMethod(Class<?> argType, String argName) {
        Object argObject = null;
        try {
            argObject = argType.getDeclaredConstructor().newInstance();
            Method fromMap = argType.getMethod("fromMap", Map.class);
            Map<String, Object> args = environment.getArgument(argName);
            argObject = fromMap.invoke(argObject, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            e.printStackTrace();
        }
        return argObject;
    }

    private static <T> T mapByFieldMatching(Class<T> argType, String argName) {
        T argObject = null;
        try {
            argObject = argType.getDeclaredConstructor().newInstance();
            Map<String, Object> args = environment.getArgument(argName);
            for (Field field : argType.getDeclaredFields()) {
                if (field.isAnnotationPresent(GQLField.class)) {
                    boolean accessible = field.canAccess(argObject);
                    field.setAccessible(true);
                    field.set(argObject, args.get(field.getName()));
                    field.setAccessible(accessible);
                }
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return argObject;
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