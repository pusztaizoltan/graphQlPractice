package org.example.entity;

import graphql.schema.idl.TypeRuntimeWiring;

import java.lang.reflect.Field;

public interface Schemable {
    default void experimentMethod() {
        System.out.println(this.getClass().getSimpleName());
    }


}
