package org.example.graphql;

import graphql.GraphQL;
import org.example.graphql.annotation.GQLMutation;
import org.example.graphql.annotation.GQLQuery;
import org.example.graphql.generator_component.GraphQLBuilder;
import org.example.graphql.generator_component.TypeCollector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class SchemaGeneratorImpl {
    private final TypeCollector typeCollector = new TypeCollector();
    private final GraphQLBuilder builder = new GraphQLBuilder();

    /**
     * Constructor for SchemaGeneratorImpl
     * with required dataSource instance as argument
     */
    public SchemaGeneratorImpl(@NotNull Object dataService) {
        for (Method method : dataService.getClass().getDeclaredMethods()) {
            if (isDataAccessor(method)) {
                this.typeCollector.collectTypesFromServiceMethodReturn(method);
                this.typeCollector.collectTypesFromServiceMethodArguments(method);
                this.builder.addDataAccessFieldForMethod(method, dataService);
            }
        }
    }

    private static boolean isDataAccessor(Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
               (method.isAnnotationPresent(GQLMutation.class) ||
                method.isAnnotationPresent(GQLQuery.class));
    }

    /**
     * Method to provide optional Type patterns for SchemaBuilding
     */
    public void withAdditionalClasses(Class<?> @NotNull ... classes) {
        this.typeCollector.collectAdditionalTypesFromClasses(classes);
    }

    /**
     * Build method of SchemaGeneratorImpl
     */
    public @NotNull GraphQL getGraphQL() {
        this.builder.addTypesForComponentClasses(this.typeCollector.getComponents());
        return GraphQL.newGraphQL(this.builder.build()).build();
    }
}