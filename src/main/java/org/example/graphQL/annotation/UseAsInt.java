package org.example.graphQL.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@FieldOf(type = FieldType.SCALAR_INT)
public @interface UseAsInt {

    String name();
}
