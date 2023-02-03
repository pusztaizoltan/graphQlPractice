package org.example.entity;

import graphql.schema.idl.TypeRuntimeWiring;

import java.lang.reflect.Field;

public interface Schemable {
    default void experimentMethod() {
        System.out.println(this.getClass().getSimpleName());
    }

    static TypeRuntimeWiring TypeRuntimeWiringFromClass(Class<?> classType) {
        TypeRuntimeWiring.Builder builder = new TypeRuntimeWiring.Builder().typeName(classType.getSimpleName());
        Field[] fields = classType.getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            String fieldName = field.getType().getSimpleName();
            builder = builder.dataFetcher(fieldName, env ->
                    fieldType.cast(field.get(classType.cast(env.getSource()))));
        }
        return builder.build();
    }
}
