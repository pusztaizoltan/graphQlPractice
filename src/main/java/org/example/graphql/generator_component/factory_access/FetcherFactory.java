package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.QGLField;
import org.example.graphql.annotation.GQLType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

public class FetcherFactory {
    /**
     * Provide DataFetcher for a dataSource method based on the detected method signature
     */
    public static @NotNull DataFetcher<?> createFetcherFor(@NotNull Method method, @NotNull Object dataService) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return (env) -> method.invoke(dataService);
        }
        Class<?> argType = parameters[0].getType();
        ArgWith argWith = parameters[0].getAnnotation(ArgWith.class);
        String argName = argWith.name();
        if (argType.isPrimitive()) {
            System.out.println("- " + method.getName() + " isPrimitive");
            return (env) -> method.invoke(dataService, env.getArguments().get(argName));
        }
        if (argType.isEnum()) {
            System.out.println("- " + method.getName() + " isEnum");
            return (env) -> method.invoke(dataService, Enum.valueOf((Class<Enum>) argType, (String) env.getArguments().get(argName)));
        }
        if (argType.equals(String.class)) {
            System.out.println("- " + method.getName() + " isString");
            return (env) -> method.invoke(dataService, env.getArguments().get(argName));
        }
        // todo test if good
        if (argWith.type() == GQLType.OBJECT) {
            System.out.println("- " + method.getName() + " isObject");
//            DataFetcher aa = (env) -> {
//                Type typeenv.getArguments().get(argName).getClass());
//                return method.invoke(dataService, env.getArguments().get(argName));
//            };
            return (env) -> {
                System.out.println("------------------------");
                var argObject = argType.getDeclaredConstructor().newInstance();
                try {
                    Method fromMap = argType.getMethod("fromMap", Map.class);
                    argObject = fromMap.invoke(argObject, (Map) env.getArguments().get(argName));
                    System.out.println(argObject.getClass());
                    return method.invoke(dataService, argObject);
                } catch (NoSuchMethodException e) {
                    System.out.println(argName);
                    var envArg = env.getArguments().get(argName);
                    System.out.println("- envArg: " + envArg);
                    var argT = envArg.getClass();
                    System.out.println(argT);
                    LinkedHashMap args = (LinkedHashMap) envArg;
                    System.out.println(args);
                    for (Field field : argType.getDeclaredFields()) {
                        if (field.isAnnotationPresent(QGLField.class)) {
                            System.out.println(field.getName());
                            boolean accessible = field.isAccessible();
                            field.setAccessible(true);
                            var fieldValue = args.get(field.getName());
                            System.out.println(fieldValue.getClass());
                            field.set(argObject, args.get(field.getName()));
                            field.setAccessible(accessible);
                        }
                    }
                    System.out.println(argObject);
                    System.out.println("------------------------");
                    return method.invoke(dataService, argObject);
                }
            };
        }
        throw new RuntimeException("Unimplemented fetcher for " + method);
    }
}
