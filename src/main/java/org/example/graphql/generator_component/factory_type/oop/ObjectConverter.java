package org.example.graphql.generator_component.factory_type.oop;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLField;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class ObjectConverter<T> extends TypeConverter<T> implements Fetchable {
    private final GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();

    public ObjectConverter(Class<T> javaType) {
        super(javaType);
        super.graphQLType = buildGraphQLAnalogue();
        registerFetchers();
    }

    @Override
    protected @Nonnull GraphQLType buildGraphQLAnalogue() {
        GraphQLObjectType.Builder objectTypeBuilder = GraphQLObjectType.newObject().name(super.getName());
        for (Field field : super.javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                objectTypeBuilder.field(FieldFactory.GQLObjectFieldFrom(field));
            }
        }
        return objectTypeBuilder.build();
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
