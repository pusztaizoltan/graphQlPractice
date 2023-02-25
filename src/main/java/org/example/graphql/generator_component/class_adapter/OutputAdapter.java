package org.example.graphql.generator_component.class_adapter;

import graphql.schema.DataFetcher;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.generator_component.util.Fetchable;
import org.example.graphql.generator_component.dataholder.TypeFactory;
import org.example.graphql.generator_component.dataholder.TypeDetail;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class OutputAdapter<T> extends AbstractClassAdapter<T> implements Fetchable {
    private final GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();

    public OutputAdapter(Class<T> javaType) {
        super(javaType);
        registerFetchers();
    }
    @Override
    public @Nonnull GraphQLCodeRegistry getRegistry() {
        return registry.build();
    }


    @Override
    protected void buildGraphQLAnalogue() {
        GraphQLObjectType.Builder builder = GraphQLObjectType.newObject().name(super.getName());
        for (Field field : super.javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                TypeDetail<?, Field> data = TypeFactory.detailOf(field);
                builder.field(GQLObjectFieldFrom(data));
            }
        }
        super.graphQLType = builder.build();
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

}
