package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.generator_component.dataholder.TypeDetail;
import org.example.graphql.generator_component.dataholder.TypeFactory;
import org.example.graphql.generator_component.util.UnimplementedException;

import javax.annotation.Nonnull;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * Static Utility class used in{@link org.example.graphql.generator_component.GraphQLBuilder}
 * to automatically create DataFetcher for the Query and Mutation type methods of data-service
 * based on the signature of the method.
 */
public class FetcherFactory {
    private DataFetchingEnvironment environment;
    private final Object dataService;

    public FetcherFactory(Object dataService) {
        this.dataService = dataService;
    }

    /**
     * Factory method of the class.
     */
    public @Nonnull DataFetcher<Object> createFetcherFor(@Nonnull Method method) {
        return (DataFetchingEnvironment env) -> {
            environment = env;
            Parameter[] parameters = method.getParameters();
            Object[] arguments = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                TypeDetail<?, Parameter> data = TypeFactory.detailOf(parameters[i]);
                arguments[i] = mapArgument(data);
            }
            return method.invoke(this.dataService, arguments);
        };
    }

    private <T> @Nonnull Object mapArgument(@Nonnull TypeDetail<T, Parameter> data) {
        if (data.isScalar()) {
            return environment.getArgument(data.getName());
        } else if (data.isEnum()) {
            return mapEnumArgument(data);
        } else if (data.isObject()) {
            return mapObjectArgument(data);
        } else if (data.isList()) {
            return environment.getArgument(data.getName());
        } else if (data.isArray()) {
            return mapArrayArgument(data);
        } else {
            throw new UnimplementedException("Unimplemented argumentMapper in  " + FetcherFactory.class.getSimpleName() + " for" + data.getName());
        }
    }

    @Nonnull
    private <T> T[] mapArrayArgument(@Nonnull TypeDetail<T, Parameter> data) {
        List<T> envArg = environment.getArgument(data.getName());
        @SuppressWarnings("unchecked")
        T[] arrayArg = (T[]) Array.newInstance(data.getContentClass(), envArg.size());
        return envArg.toArray(arrayArg);
    }

    private <T> @Nonnull T mapEnumArgument(@Nonnull TypeDetail<T, Parameter> data) {
        for (T enumConstant : data.getContentClass().getEnumConstants()) {
            if (((Enum<?>) enumConstant).name().equals(environment.getArgument(data.getName()))) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("Invalid enum constant");
    }

    private <T> @Nonnull T mapObjectArgument(@Nonnull TypeDetail<T, Parameter> data) {
        try {
            return tryMappingByStaticMapperMethod(data);
        } catch (UnimplementedException | InvocationTargetException | IllegalAccessException e) {
            return mapBySetterMatching(data);
        }
    }

    private <T> @Nonnull T tryMappingByStaticMapperMethod(@Nonnull TypeDetail<T, Parameter> data) throws InvocationTargetException, IllegalAccessException {
        String exceptionMessage = "Unimplemented preferential input-wiring method with required signature for ";
        for (Method method : data.getContentClass().getMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && method.getName().equals("fromMap")) {
                Parameter[] parameters = method.getParameters();
                if (parameters.length == 1 && parameters[0].getType().equals(Map.class)) {
                    Map<String, Object> inputArgument = environment.getArgument(data.getName());
                    return data.getContentClass().cast(method.invoke(null, inputArgument));
                }
            }
        }
        throw new UnimplementedException(exceptionMessage + data.getContentClass());
    }

    private <T> @Nonnull T mapBySetterMatching(@Nonnull TypeDetail<T, Parameter> data) {
        T inputObject = instantiateInputObject(data);
        Map<String, Object> arguments = environment.getArgument(data.getName());
        for (Field field : data.getContentClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                setInputValue(inputObject, field, arguments.get(field.getName()));
            }
        }
        return inputObject;
    }

    private <T> @Nonnull T instantiateInputObject(@Nonnull TypeDetail<T, Parameter> data) {
        String exceptionMessage = "Unimplemented default constructor for secondary input-wiring solution for ";
        try {
            return data.getContentClass().getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException |
                 InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException e) {
            throw new UnimplementedException(exceptionMessage + data.getContentClass());
        }
    }

    private void setInputValue(@Nonnull Object inputObject, @Nonnull Field property, @Nonnull Object inputValue) {
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