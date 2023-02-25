package org.example.graphql.generator_component.dataholder;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

public class TypeDetail<T extends Type,E extends AnnotatedElement> extends TypeData<E>{
    private T type;

    public TypeDetail(E element, T type) {
        super(element);
        this.type = type;
    }

    public T getType() {
        return type;
    }
}
