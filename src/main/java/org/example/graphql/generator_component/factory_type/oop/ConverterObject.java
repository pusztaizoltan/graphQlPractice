package org.example.graphql.generator_component.factory_type.oop;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLObjectType;
import org.example.graphql.annotation.GQLField;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class ConverterObject<T> extends ConverterAbstract<T> implements Fetchable {
    private final GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();

    public ConverterObject(Class<T> javaType) {
        super(javaType);
        registerFetchers();
    }

    @Override
    protected void buildGraphQLAnalogue() {
        GraphQLObjectType.Builder builder = GraphQLObjectType.newObject().name(super.getName());
        for (Field field : super.javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                builder.field(FieldFactory.GQLObjectFieldFrom(field));
            }
        }
        super.graphQLType = builder.build();
    }

    private void registerFetchers() {
        for (Field field : javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                Class<?> fieldType = field.getType();
                String fieldName = fieldType.getSimpleName();
                DataFetcher<?> fetcher = env -> fieldType.cast(field.get(javaType.cast(env.getSource())));
                this.registry.dataFetcher(FieldCoordinates.coordinates(super.getName(), fieldName), fetcher);
            }
        }
    }

    @Override
    public @Nonnull GraphQLCodeRegistry getRegistry() {
        return registry.build();
    }
}
