package org.example.graphql.util_adapter;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.Mutate;
import org.example.graphql.annotation.TypeOf;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

public class MethodAdapter {
    /**
     * Generate GraphQLFieldDefinition for a dataSource method based on the detected method signature
     */
    public static @NotNull GraphQLFieldDefinition createFieldFromMethod(@NotNull Method method) {
        if (hasListReturnWithoutArg(method)) {
            return listReturnWithoutArg(method);
        } else if (hasObjectReturnByOneArg(method)) {
            return objectReturnByOneArg(method);
        } else if (hasListReturnByOneArg(method)) {
            return listReturnByOneArg(method);
        } else if (hasScalarReturnByOneObject(method)) {
            return scalarReturnByOneObject(method);
        } else {
            throw new RuntimeException("Not implemented type of Query field for " + method);
        }
    }

    /**
     * Provide DataFetcher for a dataSource method based on the detected method signature
     */
    public static @NotNull DataFetcher<?> createFetcherFor(@NotNull Method method, @NotNull Object dataService) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return (env) -> method.invoke(dataService);
        }
        Class<?> argType = parameters[0].getType();
        String argName = parameters[0].getAnnotation(ArgWith.class).name();
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
        if (argType.isAnnotationPresent(TypeOf.class)) {
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
                        if (field.isAnnotationPresent(FieldOf.class)) {
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

    private static boolean hasListReturnWithoutArg(@NotNull Method method) {
        return method.getParameters().length == 0 &&
               method.isAnnotationPresent(FieldOf.class) &&
               method.getAnnotation(FieldOf.class).type() == GQLType.LIST;
    }

    private static boolean hasObjectReturnByOneArg(@NotNull Method method) {
        return method.getParameters().length == 1 &&
               method.isAnnotationPresent(FieldOf.class) &&
               method.getAnnotation(FieldOf.class).type() == GQLType.OBJECT &&
               method.getParameters()[0].isAnnotationPresent(ArgWith.class);
    }

    private static boolean hasListReturnByOneArg(@NotNull Method method) {
        return method.getParameters().length == 1 &&
               method.isAnnotationPresent(FieldOf.class) &&
               method.getAnnotation(FieldOf.class).type() == GQLType.LIST &&
               method.getParameters()[0].isAnnotationPresent(ArgWith.class);
    }

    private static @NotNull GraphQLFieldDefinition listReturnWithoutArg(@NotNull Method method) {
        String typeName = ReflectionUtil.genericTypeOfMethod(method).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .build();
    }

    private static @NotNull GraphQLFieldDefinition listReturnByOneArg(@NotNull Method method) {
        String typeName = ReflectionUtil.genericTypeOfMethod(method).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .argument(argumentFrom(method))
                                     .build();
    }

    private static @NotNull GraphQLFieldDefinition objectReturnByOneArg(@NotNull Method method) {
        String typeName = method.getReturnType().getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(GraphQLTypeReference.typeRef(typeName))
                                     .argument(argumentFrom(method))
                                     .build();
    }

    private static @NotNull GraphQLArgument argumentFrom(@NotNull Method method) {
        ArgWith annotation = method.getParameters()[0].getAnnotation(ArgWith.class);
        if (annotation.type().isScalar()) {
            return scalarArgument(annotation);
        } else if (annotation.type() == GQLType.ENUM) {
            return enumArgument(method);
        } else if (annotation.type() == GQLType.INPUT) {
            return objectArgument(method);
        } else {
            throw new RuntimeException("(Unimplemented argument type for " + annotation.type());
        }
    }

    private static @NotNull GraphQLArgument scalarArgument(@NotNull ArgWith annotation) {
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(annotation.type().graphQLScalarType)
                              .build();
    }

    private static @NotNull GraphQLArgument enumArgument(@NotNull Method method) {
        ArgWith annotation = method.getParameters()[0].getAnnotation(ArgWith.class);
        String typeName = method.getParameters()[0].getType().getSimpleName();
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(GraphQLTypeReference.typeRef(typeName))
                              .build();
    }

    private static @NotNull GraphQLArgument objectArgument(@NotNull Method method) {
        ArgWith annotation = method.getParameters()[0].getAnnotation(ArgWith.class);
        String typeName = method.getParameters()[0].getType().getSimpleName();
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(GraphQLTypeReference.typeRef(typeName))
                              .build();
    }

    private static boolean hasScalarReturnByOneObject(@NotNull Method method) {
        return method.getParameters().length == 1 &&
               method.isAnnotationPresent(Mutate.class) &&
               method.getAnnotation(Mutate.class).type().isScalar() &&
               method.getParameters()[0].isAnnotationPresent(ArgWith.class) &&
               method.getParameters()[0].getAnnotation(ArgWith.class).type() == GQLType.INPUT;
    }

    private static @NotNull GraphQLFieldDefinition scalarReturnByOneObject(@NotNull Method method) {
        GQLType gqlType = method.getAnnotation(Mutate.class).type();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(method.getName())
                                     .type(gqlType.graphQLScalarType)
                                     .argument(argumentFrom(method))
                                     .build();
    }
}
