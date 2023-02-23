package org.example.graphql.generator_component.factory_access;

import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.GQLArg;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.generator_component.util.TypeHolder;
import org.example.graphql.generator_component.util.UnimplementedException;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

import static org.example.graphql.generator_component.util.ReflectionUtil.genericTypeOfMethod;
import static org.example.graphql.generator_component.util.ReflectionUtil.genericTypeOfParameter;
// TODO: missing class level javadoc
// todo done also in other Classes

/**
 * Static Utility class used in{@link org.example.graphql.generator_component.GraphQLBuilder}
 * to automatically create GraphQLFieldDefinitions for the Query and Mutation type methods
 * of data-service based on method signature witch is processed by the class private factory
 * methods
 */
public class DataAccessFactory {
    // TODO: the method level javadocs are more helpful if they describe the intention in the first place,
    // then the implementation details. In this case I could imagine something like
    // "Checks if the method should be included into the schema as a list field"
    // then the rules also can be detailed
    // todo done also javadoc are rewrote

    private DataAccessFactory() {
    }

    /**
     * Only entry point of the class that generate the GraphQLFieldDefinition for Mutation or Query.
     * to determine the particulars of the returned object (argument, type) it implicitly uses
     * the static factory methods of the class
     */
    public static @Nonnull GraphQLFieldDefinition createDataAccessorFor(@Nonnull Method method) {
        GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition();
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(GQLArg.class)) {
                builder.argument(createArgumentFor(parameter));
            }
        }
        return builder.name(method.getName()).type(returnTypeFrom(method)).build();
    }

    private static @Nonnull GraphQLArgument createArgumentFor(@Nonnull Parameter parameter) {
        GQLArg annotation = parameter.getAnnotation(GQLArg.class);
        return GraphQLArgument.newArgument()
                              .name(annotation.name())
                              .type(argumentTypeFrom(parameter))
                              .build();
    }

    private static @Nonnull GraphQLOutputType returnTypeFrom(@Nonnull Method method) {
        GQLType returnType = GQLType.ofMethod(method);

        // analogous to GraphQl scalar
        if (returnType == GQLType.SCALAR) {
            return GQLType.getScalar(method.getReturnType());
        }
        // analogous to GraphQl enum
        else if (returnType == GQLType.ENUM) {
            String typeName = method.getReturnType().getSimpleName();
            return GraphQLTypeReference.typeRef(typeName);
        }
        // analogous to GraphQl list
        else if (returnType == GQLType.LIST){
            Class<?> genericType = genericTypeOfMethod(method);
            String typeName = genericType.getSimpleName();
            if(GQLType.isScalar(genericType)){
                return GraphQLList.list(GQLType.getScalar(genericType));
            } else {
                return GraphQLList.list(GraphQLTypeReference.typeRef(typeName));
            }
        }
        // analogous to GraphQl list
        else if (returnType == GQLType.ARRAY) {
            Class<?> componentType = method.getReturnType().componentType();
            String typeName = componentType.getSimpleName();
            if(GQLType.isScalar(componentType)){
                return GraphQLList.list(GQLType.getScalar(componentType));
            } else {
                return GraphQLList.list(GraphQLTypeReference.typeRef(typeName));
            }
//        } else if (method.getReturnType().componentType()) {
        } else if (returnType == GQLType.OBJECT){
            String typeName = method.getReturnType().getSimpleName();
            return GraphQLTypeReference.typeRef(typeName);
        } else {
            throw new UnimplementedException("Not implemented output-type for Data-Access field of " + method);
        }
    }

    private static @Nonnull GraphQLInputType argumentTypeFrom(@Nonnull Parameter parameter) {
        GQLType argumentType = GQLType.ofParameter(parameter);
        if (argumentType.isScalar()) {
            return GQLType.getScalar(parameter.getType());
        } else if (argumentType == GQLType.ENUM) {
            String typeName = parameter.getType().getSimpleName();
            return GraphQLTypeReference.typeRef(typeName);
        } else if (argumentType == GQLType.OBJECT) {
            String typeName = parameter.getType().getSimpleName();
            return GraphQLTypeReference.typeRef(typeName);
        } else if (argumentType == GQLType.LIST) {
            // todo problem if scalar return type in list
            Class<?> arg = genericTypeOfParameter(parameter);
            String typeName = arg.getSimpleName();
            if(GQLType.isScalar(arg)){
                return GraphQLList.list(GQLType.getScalar(arg));
            } else {
                return GraphQLList.list(GraphQLTypeReference.typeRef(typeName));
            }
//            return GraphQLList.list(GraphQLTypeReference.typeRef(typeName));
        } else {
            throw new UnimplementedException("(Unimplemented argument type for " + argumentType);
        }
    }

}
