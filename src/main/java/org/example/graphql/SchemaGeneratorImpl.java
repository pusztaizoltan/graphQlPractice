package org.example.graphql;

import graphql.GraphQL;
import org.example.graphql.annotation.GQLAccess;
import org.example.graphql.generator_component.GraphQLBuilder;
import org.example.graphql.generator_component.TypeCollector;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * Class to organize {@link TypeCollector} and {@link GraphQLBuilder}
 * operations on the required dataService and the optionally provided classes
 */
public class SchemaGeneratorImpl {
    private final TypeCollector typeCollector = new TypeCollector();

    private GraphQLBuilder builder;

    /**
     * Constructor for SchemaGeneratorImpl
     * with required dataSource instance as argument
     */
    public SchemaGeneratorImpl(@Nonnull Object... dataServices) {
        for (Object dataService : dataServices) {
            this.builder = new GraphQLBuilder(dataService);
            for (Method method : dataService.getClass().getMethods()) {
                if (method.isAnnotationPresent(GQLAccess.class)) {
                    this.typeCollector.collectTypesFromServiceMethodReturn(method);
                    this.typeCollector.collectTypesFromServiceMethodArguments(method);
                    this.builder.addDataAccessFieldForMethod(method);
                }
            }
        }
    }

    /**
     * Method to provide optional Type patterns for SchemaBuilding
     */
    @SuppressWarnings("unused")
    public void withAdditionalClasses(@Nonnull Class<?>... classes) {
        this.typeCollector.collectAdditionalTypesFromClasses(classes);
    }

    /**
     * Build method of SchemaGeneratorImpl
     */
    public @Nonnull GraphQL getGraphQL() {
        this.builder.addAdditionalTypes(this.typeCollector.getGraphQLTypes());
        this.builder.addFetchers(this.typeCollector.getTypeRegistry().build());
        return GraphQL.newGraphQL(this.builder.build()).build();
    }
}