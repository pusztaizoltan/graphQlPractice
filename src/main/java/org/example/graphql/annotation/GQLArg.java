package org.example.graphql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
// TODO: can you find a better name for the functionality represented by this annotation? (also missing javadocs)

/**
 * Marker annotation for method parameters to identify
 * its respective name and type in GraphQL Schema.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface GQLArg {
    String name();

    GQLType type();
}
