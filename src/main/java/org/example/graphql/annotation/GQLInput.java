package org.example.graphql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for classes signifying for it
 * to be handled as a GraphQL input type, in contrast
 * of default process what is handling objects as
 * output types.
 * <p>
 * Classes marked with this annotation are preferably
 * expected to have a fromMap named static method to wire it to its
 * respective GraphQl input, but it's not strictly necessary,
 * since during wiring the use of it will be the first option
 * to try and not the only.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GQLInput {
}
