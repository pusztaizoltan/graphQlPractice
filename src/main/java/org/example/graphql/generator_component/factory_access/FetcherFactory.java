package org.example.graphql.generator_component.factory_access;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.generator_component.dataholder.TypeFactory;
import org.example.graphql.generator_component.dataholder.TypeDetail;
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
public class FetcherFactory{
    private DataFetchingEnvironment environment;
    private Map<String, Object> envArgs;
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
            envArgs = env.getArguments();
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
            return envArgs.get(data.getName());
//            return environment.getArgument(data.getName());
        } else if (data.isEnum()) {
            return mapEnumArgument(data);
        } else if (data.isObject()) {
            return mapObjectArgument(data);
        } else if (data.isList()) {
            return mapListArgument(data);
        } else {
            throw new UnimplementedException("Unimplemented argumentMapper in  " + FetcherFactory.class.getSimpleName() + " for" + data.getName());
        }
    }
//    private static Object mapScalarListArgument(@Nonnull Class<T> argumentClass, @Nonnull String argName){
//        return
//    }

    private <T> @Nonnull List<T> mapListArgument(TypeDetail<T, Parameter> data) {
        Iterable<T> arg = environment.getArgument(data.getName());
//        System.out.println(environment.g);
        System.out.println(arg);
        System.out.println(arg.getClass());
        System.out.println(data.getContentType());
//        data.getGraphQLType()

        List<Long> aa = new ArrayList<>();
        ArrayList<T> result = new ArrayList<>();
        for (T item: (Iterable<T>) environment.getArgument(data.getName())) {
            result.add(item);
        }
        //        var aa =arg.get(0);

        return result;


    }

    private <T> @Nonnull T mapEnumArgument(TypeDetail<T, Parameter> data) {
        for (T enumConstant : data.getContentType().getEnumConstants()) {
            if (((Enum<?>) enumConstant).name().equals(envArgs.get(data.getName()))) {
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
        for (Method method : data.getContentType().getMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && method.getName().equals("fromMap")) {
                Parameter[] parameters = method.getParameters();
                if (parameters.length == 1 && parameters[0].getType().equals(Map.class)) {
                    Map<String, Object> inputArgument = (Map<String, Object>) envArgs.get(data.getName());
                    return data.getContentType().cast(method.invoke(null, inputArgument));
                }
            }
        }
        throw new UnimplementedException(exceptionMessage + data.getContentType());
    }

    private <T> @Nonnull T mapBySetterMatching(@Nonnull TypeDetail<T, Parameter> data) {
        T inputObject = instantiateInputObject(data);
        Map<String, Object> arguments = Map.class.cast( envArgs.get(data.getName()));
        for (Field field : data.getContentType().getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                setInputValue(inputObject, field, arguments.get(field.getName()));
            }
        }
        return inputObject;
    }

    private <T> @Nonnull T instantiateInputObject(@Nonnull TypeDetail<T, Parameter> data) {
        String exceptionMessage = "Unimplemented default constructor for secondary input-wiring solution for ";
        try {
            return data.getContentType().getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException |
                 InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException e) {
            throw new UnimplementedException(exceptionMessage + data.getContentType());
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