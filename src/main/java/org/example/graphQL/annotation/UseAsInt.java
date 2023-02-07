package org.example.graphQL.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@UseMarker(category = GraphQlIdentifyer.SCALAR, asScalar = ScalarFitter.INT)
public @interface UseAsInt {

    String name();
}
