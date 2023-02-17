package org.example.graphql.generator_component.util;

/**
 * Used to signify required, for proper operation, but missing
 * annotation placement
 */
public class MissingAnnotationException extends RuntimeException{
    public MissingAnnotationException(String message) {
        super(message);
    }
}
