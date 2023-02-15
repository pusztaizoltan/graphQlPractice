package org.example.graphql;

import graphql.GraphQL;
import org.example.graphql.annotation.GQLMutation;
import org.example.graphql.annotation.GQLQuery;
import org.example.graphql.generator_component.GraphQLBuilder;
import org.example.graphql.generator_component.TypeCollector;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SchemaGeneratorImpl {
    private final TypeCollector typeCollector = new TypeCollector();
    private final GraphQLBuilder builder = new GraphQLBuilder();

    /**
     * Constructor for SchemaGeneratorImpl
     * with required dataSource instance as argument
     */
    public SchemaGeneratorImpl(@Nonnull Object dataService) {
        // TODO: the following two methods implement the same rule how to select ad what to do with methods
        // which is redundant. If the rule changes, there is two places where the code must be maintained.
        // I think you can find a better pattern to have the method selection rule implemented only once
        for (Method method : dataService.getClass().getDeclaredMethods()) {
            if (isDataAccessor(method)) {
                this.typeCollector.collectTypesFromServiceMethodReturn(method);
                this.typeCollector.collectTypesFromServiceMethodArguments(method);
                this.builder.addDataAccessFieldForMethod(method, dataService);
            }
        }
    }

    private static boolean isDataAccessor(@Nonnull Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
               (method.isAnnotationPresent(GQLMutation.class) ||
                method.isAnnotationPresent(GQLQuery.class));
    }

    /**
     * Method to provide optional Type patterns for SchemaBuilding
     */
    public void withAdditionalClasses(Class<?> @Nonnull ... classes) {
        this.typeCollector.collectAdditionalTypesFromClasses(classes);
    }

    /**
     * Build method of SchemaGeneratorImpl
     */
    public @Nonnull GraphQL getGraphQL() {
        this.builder.addTypesForComponentClasses(this.typeCollector.getComponents());
        return GraphQL.newGraphQL(this.builder.build()).build();
    }
}