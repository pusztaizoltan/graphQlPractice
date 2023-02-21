package org.example.graphql.generator_component.factory_type;

import org.example.graphql.annotation.GQLInput;
import org.example.graphql.generator_component.GraphQLBuilder;
import org.example.graphql.generator_component.factory_type.oop.ConverterAbstract;
import org.example.graphql.generator_component.factory_type.oop.ConverterEnum;
import org.example.graphql.generator_component.factory_type.oop.ConverterInput;
import org.example.graphql.generator_component.factory_type.oop.ConverterObject;

/**
 * Used in {@link GraphQLBuilder} to create Types for GraphQlSchema, in contrast of
 * {@link org.example.graphql.generator_component.factory_access.DataAccessFactory} and
 * {@link org.example.graphql.generator_component.factory_access.FetcherFactory} that
 * are concerned with Query and Mutation fields
 */
public class TypeFactory {
    private TypeFactory() {
    }

    /**
     * Factory method of TypeFactory
     */
    public static <T> ConverterAbstract<T> getTypeConverter(Class<T> javaType) {
        if (javaType.isEnum()) {
            return new ConverterEnum<>(javaType);
        } else if (javaType.isAnnotationPresent(GQLInput.class)) {
            return new ConverterInput<>(javaType);
        } else {
            return new ConverterObject<>(javaType);
        }
    }
}
