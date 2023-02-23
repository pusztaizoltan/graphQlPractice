package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.generator_component.util.TypeData;
import org.example.graphql.generator_component.util.UnimplementedException;

import javax.annotation.Nonnull;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
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
    public static @Nonnull DataFetcher<Object> createFetcherFor(@Nonnull Method method, @Nonnull Object dataService) {
        return (DataFetchingEnvironment env) -> {
            environment = env;
            Parameter[] parameters = method.getParameters();
            Object[] arguments = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Class<?> argumentClass = parameters[i].getType();
                arguments[i] = mapArgument(parameters[i], argumentClass);
            }
            return method.invoke(dataService, arguments);
        };
    }

    private static <T> @Nonnull T mapArgument(@Nonnull Parameter parameter, Class<T> argumentClass) {
        String argName = parameter.getAnnotation(GQLArg.class).name();
        TypeData data = TypeData.ofParameter(parameter);
        if (data.isScalar()) {
            return environment.getArgument(argName);
        } else if (data.isEnum()) {
            return mapEnumArgument(argumentClass, argName);
        } else if (data.isObject()) {
            return mapObjectArgument(argumentClass, argName);
        } else if (data.isList()) {
            List<Integer> arg = environment.getArgument(argName);
            System.out.println(argumentClass);
            System.out.println(environment.getArgument(argName).toString());
            System.out.println(((List) environment.getArgument(argName)).size());
            System.out.println(environment.getArgument(argName).getClass());
//            return environment.getArgument(argName);
            ArrayList<Long> list = new ArrayList<>();
            for (int i = 0; i < arg.size(); i++) {
                list.add(arg.get(i).longValue());
            }
            return (T) list;
        } else {
            throw new UnimplementedException("Unimplemented argumentMapper in  " + FetcherFactory.class.getSimpleName() + " for" + parameter);
        }
    }
//    private static Object mapScalarListArgument(@Nonnull Class<T> argumentClass, @Nonnull String argName){
//        return
//    }

    private static <T> @Nonnull T mapEnumArgument(@Nonnull Class<T> argumentClass, @Nonnull String argName) {
        for (T enumConstant : argumentClass.getEnumConstants()) {
            if (((Enum<?>) enumConstant).name().equals(environment.getArgument(argName))) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("Invalid enum constant");
    }

    private static <T> @Nonnull T mapObjectArgument(@Nonnull Class<T> argumentClass, @Nonnull String argName) {
        try {
            return tryMappingByStaticMapperMethod(argumentClass, argName);
        } catch (UnimplementedException | InvocationTargetException | IllegalAccessException e) {
            return mapBySetterMatching(argumentClass, argName);
        }
    }

    private static <T> @Nonnull T tryMappingByStaticMapperMethod(@Nonnull Class<T> argumentClass, @Nonnull String argName) throws InvocationTargetException, IllegalAccessException {
        String exceptionMessage = "Unimplemented preferential input-wiring method with required signature for ";
        for (Method method : argumentClass.getMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && method.getName().equals("fromMap")) {
                Parameter[] parameters = method.getParameters();
                if (parameters.length == 1 && parameters[0].getType().equals(Map.class)) {
                    Map<String, Object> inputArgument = environment.getArgument(argName);
                    return argumentClass.cast(method.invoke(null, inputArgument));
                }
            }
        }
        throw new UnimplementedException(exceptionMessage + argumentClass);
    }

    private static <T> @Nonnull T mapBySetterMatching(@Nonnull Class<T> argumentClass, @Nonnull String argName) {
        T inputObject = instantiateInputObject(argumentClass);
        Map<String, Object> arguments = environment.getArgument(argName);
        for (Field field : argumentClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                setInputValue(inputObject, field, arguments.get(field.getName()));
            }
        }
        return inputObject;
    }

    private static <T> @Nonnull T instantiateInputObject(@Nonnull Class<T> argumentClass) {
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

    private static void setInputValue(@Nonnull Object inputObject, @Nonnull Field property, @Nonnull Object inputValue) {
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
}