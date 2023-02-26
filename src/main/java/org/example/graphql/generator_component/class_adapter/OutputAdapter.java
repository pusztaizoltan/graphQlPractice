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

public class OutputAdapter<T> extends AbstractClassAdapter implements Fetchable {
    private final Class<T> javaType;
    private final GraphQLObjectType.Builder outputBuilder = GraphQLObjectType.newObject();
    private final GraphQLCodeRegistry.Builder fetcherRegistry = GraphQLCodeRegistry.newCodeRegistry();

    protected OutputAdapter(@Nonnull Class<T> javaType) {
        super();
        this.javaType = javaType;
        this.outputBuilder.name(javaType.getSimpleName());
    }

    @Override
    public @Nonnull GraphQLCodeRegistry getFetcherRegistry() {
        for (Field field : javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                Class<?> fieldType = field.getType();
                String fieldName = fieldType.getSimpleName();
                DataFetcher<?> fetcher = env -> fieldType.cast(field.get(javaType.cast(env.getSource())));
                FieldCoordinates coordinate = FieldCoordinates.coordinates(javaType.getSimpleName(), fieldName);
                this.fetcherRegistry.dataFetcher(coordinate, fetcher);
            }
        }
        return fetcherRegistry.build();
    }

    @Override
    public @Nonnull GraphQLType getGraphQLType() {
        for (Field field : javaType.getDeclaredFields()) {
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
