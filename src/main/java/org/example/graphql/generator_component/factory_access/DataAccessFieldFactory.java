package org.example.graphql.generator_component.factory_access;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.ArgWith;
import org.example.graphql.annotation.GGLField;
import org.example.graphql.annotation.GQLMutation;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.generator_component.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.example.graphql.generator_component.factory_access.ArgumentFactory.argumentFrom;

public class DataAccessFieldFactory {
    public static @NotNull GraphQLFieldDefinition createDataAccessForMethod(@NotNull Method method) {
        GraphQLFieldDefinition.Builder builder = GraphQLFieldDefinition.newFieldDefinition();
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(ArgWith.class)) {
                builder.argument(ArgumentFactory.argumentFrom(parameter));
            }
        }
        method.getParameters();
        return builder.name(method.getName()).type(returnTypeFactory(method)).build();
    }

    private static GraphQLOutputType returnTypeFactory(Method method) {
        GQLType returnType = GQLType.ofMethod(method);
        if (returnType.isScalar()) {
            return returnType.graphQLScalarType;
        } else if (returnType == GQLType.LIST) {
            String typeName = ReflectionUtil.genericTypeOfMethod(method).getSimpleName();
            return GraphQLList.list(GraphQLTypeReference.typeRef(typeName));
        } else if (returnType == GQLType.OBJECT) {
            String typeName = method.getReturnType().getSimpleName();
            return GraphQLTypeReference.typeRef(typeName);
        } else {
            throw new RuntimeException("Not implemented output-type for Data-Access field of " + method);
        }
    }


    /**
     * Generate GraphQLFieldDefinition for a dataSource method based on the detected method signature
     */
//    public static @NotNull GraphQLFieldDefinition createFieldFromMethod(@NotNull Method method) {
//        if (hasListReturnWithoutArg(method)) {
//            return listReturnWithoutArg(method);
//        } else if (hasObjectReturnByOneArg(method)) {
//            return objectReturnByOneArg(method);
//        } else if (hasListReturnByOneArg(method)) {
//            return listReturnByOneArg(method);
//        } else if (hasScalarReturnByOneObject(method)) {
//            return scalarReturnByOneObject(method);
//        } else {
//            throw new RuntimeException("Not implemented type of Data-Access field for " + method);
//        }
//    }
//
//    private static boolean hasListReturnWithoutArg(@NotNull Method method) {
//        return method.getParameters().length == 0 &&
//               method.isAnnotationPresent(GGLField.class) &&
//               method.getAnnotation(GGLField.class).type() == GQLType.LIST;
//    }
//
//    private static boolean hasObjectReturnByOneArg(@NotNull Method method) {
//        return method.getParameters().length == 1 &&
//               method.isAnnotationPresent(GGLField.class) &&
//               method.getAnnotation(GGLField.class).type() == GQLType.OBJECT &&
//               method.getParameters()[0].isAnnotationPresent(ArgWith.class);
//    }
//
//    private static boolean hasListReturnByOneArg(@NotNull Method method) {
//        return method.getParameters().length == 1 &&
//               method.isAnnotationPresent(GGLField.class) &&
//               method.getAnnotation(GGLField.class).type() == GQLType.LIST &&
//               method.getParameters()[0].isAnnotationPresent(ArgWith.class);
//    }
//
//    private static @NotNull GraphQLFieldDefinition listReturnWithoutArg(@NotNull Method method) {
//        String typeName = ReflectionUtil.genericTypeOfMethod(method).getSimpleName();
//        return GraphQLFieldDefinition.newFieldDefinition()
//                                     .name(method.getName())
//                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
//                                     .build();
//    }
//
//    private static @NotNull GraphQLFieldDefinition listReturnByOneArg(@NotNull Method method) {
//        String typeName = ReflectionUtil.genericTypeOfMethod(method).getSimpleName();
//        return GraphQLFieldDefinition.newFieldDefinition()
//                                     .name(method.getName())
//                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
//                                     .argument(argumentFrom(method))
//                                     .build();
//    }
//
//    private static @NotNull GraphQLFieldDefinition objectReturnByOneArg(@NotNull Method method) {
//        String typeName = method.getReturnType().getSimpleName();
//        return GraphQLFieldDefinition.newFieldDefinition()
//                                     .name(method.getName())
//                                     .type(GraphQLTypeReference.typeRef(typeName))
//                                     .argument(argumentFrom(method))
//                                     .build();
//    }
//
//    private static boolean hasScalarReturnByOneObject(@NotNull Method method) {
//        return method.getParameters().length == 1 &&
//               method.isAnnotationPresent(GQLMutation.class) &&
//               method.getAnnotation(GQLMutation.class).type().isScalar() &&
//               method.getParameters()[0].isAnnotationPresent(ArgWith.class) &&
//               method.getParameters()[0].getAnnotation(ArgWith.class).type() == GQLType.OBJECT;
//    }
//
//    private static @NotNull GraphQLFieldDefinition scalarReturnByOneObject(@NotNull Method method) {
//        GQLType gqlType = method.getAnnotation(GQLMutation.class).type();
//        return GraphQLFieldDefinition.newFieldDefinition()
//                                     .name(method.getName())
//                                     .type(gqlType.graphQLScalarType)
//                                     .argument(argumentFrom(method))
//                                     .build();
//    }
}
