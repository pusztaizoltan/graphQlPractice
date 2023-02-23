package org.example.graphql.generator_component.util;

import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLType;
import org.example.graphql.annotation.GQLType;


public class TypeDetails<T> extends TypeData {

    public final Class<T> contentType;
    public GraphQLType graphQLType;

    public TypeDetails(GQLType gqlType, Class<T> contentType) {
        super(gqlType);
        this.contentType = contentType;
    }

    @Override
    public Class<T> getContentType() {
        return contentType;
    }

}
