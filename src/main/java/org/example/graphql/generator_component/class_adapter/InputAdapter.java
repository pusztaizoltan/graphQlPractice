package org.example.graphql.generator_component.class_adapter;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.generator_component.dataholder.TypeDetail;
import org.example.graphql.generator_component.dataholder.TypeFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class InputAdapter<T> extends AbstractClassAdapter {
    private final Class<T> javaType;
    private final GraphQLInputObjectType.Builder inputBuilder = GraphQLInputObjectType.newInputObject();

    protected InputAdapter(@Nonnull Class<T> javaType) {
        super();
        this.javaType = javaType;
        this.inputBuilder.name(javaType.getSimpleName());
    }

    @Override
    public @Nonnull GraphQLType getGraphQLType() {
        for (Field field : javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                TypeDetail<?, Field> data = TypeFactory.detailOf(field);
                inputBuilder.field(GQLInputFieldFrom(data));
            }
        }
        return inputBuilder.build();
    }

    /**
     * Generate GraphQLInputObjectField based on field and the required
     * {@link GQLField} annotation on it
     */
    private @Nonnull GraphQLInputObjectField GQLInputFieldFrom(@Nonnull TypeDetail<?, Field> data) {
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(data.getName())
                                      .type((GraphQLInputType) data.getGraphQLType())
                                      .build();
    }
}
