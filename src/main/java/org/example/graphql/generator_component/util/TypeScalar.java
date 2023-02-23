package org.example.graphql.generator_component.util;

public class TypeScalar<T> extends TypeHolder<T>{
    public final Class<T> contentType;
    public TypeScalar(Class<T> classType) {
        super(classType);
        this.contentType = classType;
    }
}
