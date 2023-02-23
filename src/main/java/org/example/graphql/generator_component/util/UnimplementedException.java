package org.example.graphql.generator_component.util;

/**
 * Used to signify unimplemented functionality of the project
 * placed on the end of multi-choice factory methods
 */
public class UnimplementedException extends RuntimeException {
    public UnimplementedException(String message) {
        super(message);
    }
}
