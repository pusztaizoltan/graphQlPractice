package org.example.graphql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
// TODO: can you find a better name for the functionality represented by this annotation? (also missing javadocs)
// todo done also in other annotations
/**
 * Marker annotation for Class fields to identify
 * its respective type category in GraphQL Schema.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface GQLField {
    GQLType type();
}
