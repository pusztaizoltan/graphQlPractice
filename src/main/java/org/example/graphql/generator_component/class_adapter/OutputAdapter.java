package org.example.graphql.generator_component.class_adapter;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.generator_component.dataholder.TypeDetail;
import org.example.graphql.generator_component.dataholder.TypeFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class OutputAdapter<T> extends AbstractClassAdapter<T> implements Fetchable {
    private final GraphQLObjectType.Builder outputBuilder = GraphQLObjectType.newObject().name(super.javaType.getSimpleName());
    private final GraphQLCodeRegistry.Builder fetcherRegistry = GraphQLCodeRegistry.newCodeRegistry();

    public OutputAdapter(Class<T> javaType) {
        super(javaType);
    }

    @Override
    public @Nonnull GraphQLCodeRegistry getFetcherRegistry() {
        for (Field field : javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                Class<?> fieldType = field.getType();
                String fieldName = fieldType.getSimpleName();
                DataFetcher<?> fetcher = env -> fieldType.cast(field.get(javaType.cast(env.getSource())));
                this.fetcherRegistry.dataFetcher(FieldCoordinates.coordinates(super.getName(), fieldName), fetcher);
            }
        }
        return fetcherRegistry.build();
    }

    @Override
    public GraphQLType getGraphQLType() {
        for (Field field : super.javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                TypeDetail<?, Field> data = TypeFactory.detailOf(field);
                outputBuilder.field(GQLObjectFieldFrom(data));
            }
        }
        return outputBuilder.build();
    }

    /**
     * Generate GraphQLFieldDefinition based on field and the required
     * {@link GQLField} annotation on it.
     */
    private @Nonnull GraphQLFieldDefinition GQLObjectFieldFrom(@Nonnull TypeDetail<?, Field> data) {
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(data.getName())
                                     .type((GraphQLOutputType) data.getGraphQLType())
                                     .build();
    }
}
