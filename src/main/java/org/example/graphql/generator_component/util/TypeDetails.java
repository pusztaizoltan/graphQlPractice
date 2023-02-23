package org.example.graphql.generator_component.util;

import graphql.schema.GraphQLType;

public class TypeDetails<T> extends TypeData {
    public final Class<T> contentType;
//    public TypeData.Type  graphQLType;

    public TypeDetails(TypeData.Type dataType, Class<T> contentType) {
        super(dataType);
        this.contentType = contentType;
    }

    @Override
    public Class<T> getContentType() {
        return contentType;
    }

    @Override
    public GraphQLType getScalarType() {
        return TypeData.SCALAR_MAP.get(contentType);
    }

    @Override
    public boolean hasScalarContent() {
        return TypeData.SCALAR_MAP.containsKey(contentType);
    }
}
