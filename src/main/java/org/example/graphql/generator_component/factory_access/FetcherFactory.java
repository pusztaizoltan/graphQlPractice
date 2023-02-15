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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Static Utility class used in{@link org.example.graphql.generator_component.GraphQLBuilder}
 * to automatically create DataFetcher for the Query and Mutation type methods of data-service
 * based on the signature of the method.
 */
public class FetcherFactory {
    /**
     * Factory method of the class.
     */
    public static @Nonnull DataFetcher<?> createFetcherFor(@Nonnull Method method, @Nonnull Object dataService) {
        return (DataFetchingEnvironment env) -> {
            Parameter[] parameters = method.getParameters();
            Object[] arguments = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                String argName = parameters[i].getAnnotation(GQLArg.class).name();
                arguments[i] = mapArgument(parameters[i], env.getArguments().get(argName));
            }
            return method.invoke(dataService, arguments);
        };
    }

    private static Object mapArgument(Parameter parameter, Object envArgObject) {
        Class<?> argType = parameter.getType();
        GQLType gqlType = GQLType.ofParameter(parameter);
        if (gqlType.isScalar()) {
            return envArgObject;
        } else if (gqlType == GQLType.ENUM) {
            return Enum.valueOf((Class<Enum>) argType, (String) envArgObject);
        } else if (gqlType == GQLType.OBJECT && hasMapperMethod(argType)) {
            try {
                return mapByMapperMethod(argType, envArgObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (gqlType == GQLType.OBJECT && !hasMapperMethod(argType)) {
            try {
                return mapByFieldMatching(argType, envArgObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("Unimplemented argumentMapper for" + parameter);
        }
        return envArgObject;
    }

    private static Object mapByMapperMethod(Class<?> argType, Object envArgObject) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object argObject = argType.getDeclaredConstructor().newInstance();
        Method fromMap = argType.getMethod("fromMap", Map.class);
        argObject = fromMap.invoke(argObject, envArgObject);
        return argObject;
    }

    private static Object mapByFieldMatching(Class<?> argType, Object envArgObject) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object argObject = argType.getDeclaredConstructor().newInstance();
        LinkedHashMap<String, ?> args = (LinkedHashMap<String, ?>) envArgObject;
        for (Field field : argType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(argObject, args.get(field.getName()));
                field.setAccessible(accessible);
            }
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