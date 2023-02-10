package org.example.graphql;

import graphql.GraphQL;
import org.example.graphql.util_generator.ClassParser;
import org.example.graphql.util_generator.GraphQLBuilder;
import org.jetbrains.annotations.NotNull;

public class SchemaGeneratorImpl {
    private final ClassParser classParser = new ClassParser();
    private final GraphQLBuilder builder = new GraphQLBuilder();

    /**
     * Constructor for SchemaGeneratorImpl
     * with required dataSource instance as argument
     */
    public SchemaGeneratorImpl(@NotNull Object dataService) {
        this.classParser.parseClassesFromDataService(dataService);
        this.classParser.parseInputObjectsFromDataService(dataService);
        this.builder.addQueryForDataService(dataService);
    }

    /**
     * Method to provide optional Type patterns for SchemaBuilding
     */
    public void withAdditionalClasses(Class<?> @NotNull ... classes) {
        this.classParser.parseAdditionalClasses(classes);
    }

    /**
     * Build method of SchemaGeneratorImpl
     */
    public @NotNull GraphQL getGraphQL() {
        this.builder.addTypesForComponentClasses(this.classParser.getComponents());
        return GraphQL.newGraphQL(this.builder.build()).build();
    }
}