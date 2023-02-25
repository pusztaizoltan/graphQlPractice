package org.example.graphql.generator_component.class_adapter;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.generator_component.dataholder.TypeFactory;
import org.example.graphql.generator_component.dataholder.TypeContent;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class InputAdapter<T> extends AbstractClassAdapter<T> {
    public InputAdapter(Class<T> javaType) {
        super(javaType);
    }

    @Override
    protected void buildGraphQLAnalogue() {
        GraphQLInputObjectType.Builder builder = GraphQLInputObjectType.newInputObject().name(super.getName());
        for (Field field : javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                TypeContent<? ,Field> data = TypeFactory.contentOf(field);
                builder.field(GQLInputFieldFrom(data));
            }
        }
        super.graphQLType = builder.build();
    }

    /**
     * Generate GraphQLInputObjectField based on field and the required
     * {@link GQLField} annotation on it
     */
    private @Nonnull GraphQLInputObjectField GQLInputFieldFrom(@Nonnull TypeContent<? ,Field> data) {
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(data.getName())
                                      .type((GraphQLInputType) data.getGraphQLType())
                                      .build();
    }
}
