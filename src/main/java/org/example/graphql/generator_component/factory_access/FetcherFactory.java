package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLType;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
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
        // todo generalize
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return (env) -> method.invoke(dataService);
        }
        Class<?> argType = parameters[0].getType();
        GQLArg GQLArg = parameters[0].getAnnotation(GQLArg.class);
        String argName = GQLArg.name();
        if (argType.isPrimitive()) {
            return (env) -> method.invoke(dataService, env.getArguments().get(argName));
        } else if (argType.equals(String.class)) {
            return (env) -> method.invoke(dataService, env.getArguments().get(argName));
        } else if (argType.isEnum()) {
            return (env) -> method.invoke(dataService, Enum.valueOf((Class<Enum>) argType, (String) env.getArguments().get(argName)));
        } else if (GQLArg.type() == GQLType.OBJECT && hasMapperMethod(argType)) {
            return (env) -> {
                Object argObject = argType.getDeclaredConstructor().newInstance();
                Method fromMap = argType.getMethod("fromMap", Map.class);
                argObject = fromMap.invoke(argObject, env.getArguments().get(argName));
                return method.invoke(dataService, argObject);
            };
        } else if (GQLArg.type() == GQLType.OBJECT && !hasMapperMethod(argType)) {
            return (env) -> {
                Object argObject = argType.getDeclaredConstructor().newInstance();
                LinkedHashMap<String, ?> args = (LinkedHashMap<String, ?>) env.getArguments().get(argName);
                for (Field field : argType.getDeclaredFields()) {
                    if (field.isAnnotationPresent(GQLField.class)) {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        field.set(argObject, args.get(field.getName()));
                        field.setAccessible(accessible);
                    }
                }
                return method.invoke(dataService, argObject);
            };
        } else {
            throw new RuntimeException("Unimplemented fetcher for " + method);
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