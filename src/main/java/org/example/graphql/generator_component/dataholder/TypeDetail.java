package org.example.graphql.generator_component.dataholder;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;

import java.lang.reflect.AnnotatedElement;

public class TypeDetail<T, E extends AnnotatedElement> extends TypeData<E> {
    private final Class<T> content;

    public TypeDetail(E element, Class<T> content) {
        super(element);
        this.content = content;
    }

    @Override
    public Class<T> getContentType() {
        return content;
    }

    @Override
    public boolean hasScalarContent() {
        return TypeFactory.OUTPUT_MAP.containsKey(content);
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
            return TypeFactory.OUTPUT_MAP.get(content);
        } else {
            return GraphQLTypeReference.typeRef(content.getSimpleName());
        }
    }
}
