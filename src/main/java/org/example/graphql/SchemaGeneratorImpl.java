package org.example.graphql;

import graphql.GraphQL;
import org.example.graphql.generator_component.TypeCollector;
import org.example.graphql.generator_component.GraphQLBuilder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

import static org.example.graphql.generator_component.util.ReflectionUtil.isQueryOrMutation;

public class SchemaGeneratorImpl {
    private final TypeCollector typeCollector = new TypeCollector();
    private final GraphQLBuilder builder = new GraphQLBuilder();

    /**
     * Constructor for SchemaGeneratorImpl
     * with required dataSource instance as argument
     */
    public SchemaGeneratorImpl(@NotNull Object dataService) {
        for (Method method: dataService.getClass().getDeclaredMethods()) {
            if(isQueryOrMutation(method)){
                this.typeCollector.collectTypesFromServiceMethodReturn(method);
                this.typeCollector.collectTypesFromServiceMethodArguments(method);
            }

        }
//        this.typeCollector.parseClassesFromDataService(dataService);
//        this.typeCollector.parseInputObjectsFromDataService(dataService);
        this.builder.addQueryForDataService(dataService);
        this.builder.addMutationForDataService(dataService);
    }

    /**
     * Method to provide optional Type patterns for SchemaBuilding
     */
    public void withAdditionalClasses(Class<?> @NotNull ... classes) {
        this.typeCollector.parseAdditionalClasses(classes);
    }

    /**
     * Build method of SchemaGeneratorImpl
     */
    public @NotNull GraphQL getGraphQL() {
        this.builder.addTypesForComponentClasses(this.typeCollector.getComponents());
        return GraphQL.newGraphQL(this.builder.build()).build();
    }
}