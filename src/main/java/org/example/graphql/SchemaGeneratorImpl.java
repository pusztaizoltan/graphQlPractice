package org.example.graphql;

import graphql.GraphQL;
import org.example.graphql.generatorutil.ClassParser;
import org.example.graphql.generatorutil.GraphQLBuilder;

public class SchemaGeneratorImpl {
    private final ClassParser classParser = new ClassParser();
    private final GraphQLBuilder builder = new GraphQLBuilder();

    /**
     * Constructor for SchemaGeneratorImpl
     * with required dataSource instance as argument
     */
    public SchemaGeneratorImpl(Object dataService) {
        this.classParser.parseClassesFromDataService(dataService);
        this.builder.addQueryForDataService(dataService);
    }

    /**
     * Method to provide optional Type patterns for SchemaBuilding
     */
    public void withAdditionalClasses(Class<?>... classes) {
        this.classParser.parseAdditionalClasses(classes);
    }

    /**
     * Build method of SchemaGeneratorImpl
     */
    public GraphQL getGraphQL() {
        builder.addTypesForComponentClasses(this.classParser.getComponents());
        return GraphQL.newGraphQL(builder.build()).build();
    }
}