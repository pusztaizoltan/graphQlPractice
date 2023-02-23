package org.example.graphql.generator_component.util;

import graphql.schema.GraphQLType;

import java.lang.reflect.AnnotatedElement;

public class TypeDetails<C,T extends AnnotatedElement> extends TypeData<T> {
    public final Class<C> contentType;
//    public TypeData.Type  graphQLType;

    public TypeDetails(TypeData.Type dataType, Class<C> contentType,T annotatedElement) {
        super(dataType, annotatedElement);
        this.contentType = contentType;
    }

    @Override
    public Class<C> getContentType() {
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
