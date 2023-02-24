package org.example.graphql.generator_component.type_adapter;

import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.generator_component.dataholder.DataFactory;
import org.example.graphql.generator_component.dataholder.Details;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

public class InputAdapter<T> extends AbstractTypeAdapter<T> {
    public InputAdapter(Class<T> javaType) {
        super(javaType);
    }

    @Override
    protected void buildGraphQLAnalogue() {
        GraphQLInputObjectType.Builder builder = GraphQLInputObjectType.newInputObject().name(super.getName());
        for (Field field : javaType.getDeclaredFields()) {
            if (field.isAnnotationPresent(GQLField.class)) {
                Details<? ,Field> data = DataFactory.detailOf(field);
                builder.field(GQLInputFieldFrom(data));
            }
        }
        super.graphQLType = builder.build();
    }

    /**
     * Generate GraphQLInputObjectField based on field and the required
     * {@link GQLField} annotation on it
     */
    private @Nonnull GraphQLInputObjectField GQLInputFieldFrom(@Nonnull Details<? ,Field> data) {
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(data.getName())
                                      .type((GraphQLInputType) data.getGraphQLType())
                                      .build();
    }
}
