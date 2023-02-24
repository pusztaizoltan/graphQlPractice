package org.example.graphql.generator_component.dataholder;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;

import java.lang.reflect.AnnotatedElement;

public class Details<T, A extends AnnotatedElement> extends Data<A> {
    private final Class<T> contentType;

    public Details(Data<A> typeData, Class<T> contentType) {
        super(typeData);
        this.contentType = contentType;
    }

    @Override
    public Class<T> getContentType() {
        return contentType;
    }

    public boolean hasScalarContent() {
        return DataFactory.SCALAR_MAP.containsKey(contentType);
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
            return DataFactory.SCALAR_MAP.get(contentType);
        } else {
            return GraphQLTypeReference.typeRef(contentType.getSimpleName());
        }
    }

    public boolean isEnum() {
        return this.dataType == DataFactory.Type.ENUM;
    }

    public boolean isList() {
        return this.dataType == DataFactory.Type.LIST;
    }

    public boolean isArray() {
        return this.dataType == DataFactory.Type.ARRAY;
    }

    public boolean isObject() {
        return this.dataType == DataFactory.Type.OBJECT;
    }
}
