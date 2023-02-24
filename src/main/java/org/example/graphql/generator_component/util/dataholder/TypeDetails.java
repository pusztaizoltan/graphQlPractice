package org.example.graphql.generator_component.util.dataholder;

import graphql.schema.GraphQLList;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;

import javax.annotation.Nonnull;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

public class TypeDetails<T, A extends AnnotatedElement> extends TypeData<A> {
    private final Class<T> contentType;
//    public TypeDetails(TypeData.Type dataType, Class<C> contentType, T annotatedElement) {
//        super(dataType, annotatedElement);
//        this.contentType = contentType;
//    }

    public TypeDetails(TypeData<A> typeData, Class<T> contentType) {
        super(typeData);
        this.contentType = contentType;
    }

    public static <A extends AnnotatedElement> TypeDetails<?, A> of(@Nonnull A element) {
        return (TypeDetails<?, A>) TypeData.of(element);
    }

    @Override
    public Class<T> getContentType() {
        return contentType;
    }

    @Override
    public boolean hasScalarContent() {
        return TypeData.SCALAR_MAP.containsKey(contentType);
    }

    public Function<Class<T>, ?> test() {
        return (this.isScalar() || this.hasScalarContent()) ? TypeData.SCALAR_MAP::get : Class::getSimpleName;
    }

    public GraphQLType getGraphQLType() {
        if (this.isList() || this.isArray()) {
            return GraphQLList.list(this.GraphQLTypeByReferenceType());
        } else {
            return this.GraphQLTypeByReferenceType();
        }
    }

    private GraphQLType GraphQLTypeByReferenceType() {
        if (this.hasScalarContent()) {
            return TypeData.SCALAR_MAP.get(contentType);
        } else {
            return GraphQLTypeReference.typeRef(contentType.getSimpleName());
        }
    }
}
