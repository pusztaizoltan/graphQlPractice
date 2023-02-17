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
            return mapByFieldMatching(argumentClass, argName);
        } else {
            throw new UnimplementedException("Unimplemented argumentMapper for" + parameter);
        }
    }

    private static Object mapByStaticMapperMethod(@Nonnull Class<?> argumentClass,@Nonnull String argName) {
        String exceptionMessage = "Unimplemented preferential input-wiring method with required signature for ";
        try {
            Map<String, Object> inputArgument =  environment.getArgument(argName);
            return argumentClass.getMethod("fromMap", Map.class)
                                .invoke(null, inputArgument);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new UnimplementedException(exceptionMessage + argumentClass);
        }
    }

    private static <T> T mapByFieldMatching(Class<T> argType, String argName) {
//        Statement statement = new Statement((Object) argObject,"fromMap", (Object[]) args.get(field.getName()));
//        PropertyDescriptor aa;
        T argObject = tryInstanceOf(argType);
        Map<String, Object> args = environment.getArgument(argName);
        for (Field field : argType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
//                try {
//                    // todo boolean nameing
//                    new PropertyDescriptor(field.getName(), argType)
//                            .getWriteMethod()
//                            .invoke(argObject, args.get(field.getName()));
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                } catch (InvocationTargetException e) {
//                    throw new RuntimeException(e);
//                } catch (IntrospectionException e) {
//                    throw new RuntimeException(e);
//                }
                boolean accessible = field.canAccess(argObject);
                field.setAccessible(true);
                try {
                    field.set(argObject, args.get(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                field.setAccessible(accessible);
            }
        }
        return argObject;
    }

    static <T> T tryInstanceOf(Class<T> argType) {
        try {
            return argType.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException |
                 InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
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