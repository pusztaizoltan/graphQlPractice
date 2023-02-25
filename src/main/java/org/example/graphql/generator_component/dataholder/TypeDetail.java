package org.example.graphql.generator_component.dataholder;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;

import java.lang.reflect.AnnotatedElement;

public class TypeDetail<T, E extends AnnotatedElement> extends TypeData<E> {
    private final Class<T> contentType;

    public TypeDetail(TypeData<E> typeData, Class<T> contentType) {
        super(typeData);
        this.contentType = contentType;
    }

    @Override
    public Class<T> getContentType() {
        return contentType;
    }

    @Override
    public boolean hasScalarContent() {
        return TypeFactory.SCALAR_MAP.containsKey(contentType);
    }

    private boolean isAggregation() {
        return isArray() || isList();
    }

    public GraphQLType getGraphQLType() {
        if (isAggregation()) {
            return GraphQLList.list(this.GraphQLTypeByReferenceType());
        } else {
            return this.GraphQLTypeByReferenceType();
        }
    }

    private GraphQLType GraphQLTypeByReferenceType() {
        if (hasScalarContent()) {
            return TypeFactory.SCALAR_MAP.get(contentType);
        } else {
            return GraphQLTypeReference.typeRef(contentType.getSimpleName());
        }
    }

}
