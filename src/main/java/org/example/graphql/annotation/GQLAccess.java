package org.example.graphql.annotation;

import org.example.graphql.generator_component.dataholder.TypeFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for Data-Service methods,
 * declaring them as GraphQl query mediators,
 * with the specified return type category.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GQLAccess {
    TypeFactory.AccessType type();
}
