package org.example.graphql.generator_component.util;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;

import java.lang.reflect.AnnotatedElement;

public class TypeDetails<C, T extends AnnotatedElement> extends TypeData<T> {
    private final Class<C> contentType;

    public TypeDetails(TypeData.Type dataType, Class<C> contentType, T annotatedElement) {
        super(dataType, annotatedElement);
        this.contentType = contentType;
    }

    @Override
    public Class<C> getContentType() {
        return contentType;
    }

    @Override
    public boolean hasScalarContent() {
        return TypeData.SCALAR_MAP.containsKey(contentType);
    }

    public GraphQLType getGraphQLType() {
        if (this.isScalar()) {
            return TypeData.SCALAR_MAP.get(contentType);
        } else if (this.isEnum()) {
            return GraphQLTypeReference.typeRef(this.getContentType().getSimpleName());
        } else if (this.isList()) {
            if (this.hasScalarContent()) {
                return GraphQLList.list(TypeData.SCALAR_MAP.get(contentType));
            } else {
                return GraphQLList.list(GraphQLTypeReference.typeRef(this.getContentType().getSimpleName()));
            }
        } else if (this.isArray()) {
            if (this.hasScalarContent()) {
                return GraphQLList.list(TypeData.SCALAR_MAP.get(contentType));
            } else {
                return GraphQLList.list(GraphQLTypeReference.typeRef(this.getContentType().getSimpleName()));
            }
        } else if (this.isObject()) {
            return GraphQLTypeReference.typeRef(this.getContentType().getSimpleName());
        } else {
            throw new UnimplementedException("Not implemented output-type for Data-Access field of " + this.getOrigin());
        }
    }
}
