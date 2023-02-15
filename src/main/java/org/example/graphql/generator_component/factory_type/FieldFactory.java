package org.example.graphql.generator_component.factory_type;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLTypeReference;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLType;

// todo it is preferred for the nullity annotations to use the javax.annotation, os I added the respective lib to the gradle.build
import javax.annotation.Nonnull;
import java.lang.reflect.Field;

import static org.example.graphql.generator_component.util.ReflectionUtil.genericTypeOfField;

/**
 * Used to create {@link GraphQLFieldDefinition} and {@link GraphQLInputObjectField),
 * only should be used in {@link TypeFactory } so its visibility shold be minimized
 * according to that, to package-private.
 */
class FieldFactory {
    /**
     * Generate GraphQLFieldDefinition based on field and the required
     * {@link GQLField} annotation on it.
     */
    static @Nonnull GraphQLFieldDefinition GQLObjectFieldFrom(@Nonnull Field field) {
        if (!field.isAnnotationPresent(GQLField.class)) {
            throw new RuntimeException("Parsing attempt of unannotated field:" + field);
        }
        GQLField QGLField = field.getAnnotation(GQLField.class);
        if (QGLField.type().isScalar()) {
            return scalarObjectField(field);
        } else if (QGLField.type() == GQLType.OBJECT) {
            return objectObjectField(field);
        } else if (QGLField.type() == GQLType.LIST) {
            return listObjectField(field);
        } else if (QGLField.type() == GQLType.ENUM) {
            return enumObjectField(field);
        } else {
            throw new RuntimeException("Unimplemented fieldAdapter for " + QGLField);
        }
    }

    /**
     * Generate GraphQLInputObjectField based on field and the required
     * {@link GQLField} annotation on it
     */
    static @Nonnull GraphQLInputObjectField GQLInputFieldFrom(@Nonnull Field field) {
        if (!field.isAnnotationPresent(GQLField.class)) {
            throw new RuntimeException("Parsing attempt of unannotated field:" + field);
        }
        GQLField QGLField = field.getAnnotation(GQLField.class);
        if (QGLField.type().isScalar()) {
            return scalarInputField(field);
        } else if (QGLField.type() == GQLType.OBJECT) {
            return objectInputField(field);
        } else if (QGLField.type() == GQLType.LIST) {
            return listInputField(field);
        } else if (QGLField.type() == GQLType.ENUM) {
            return enumInputField(field);
        } else {
            throw new RuntimeException("Unimplemented fieldAdapter for " + QGLField);
        }
    }

    private static @Nonnull GraphQLFieldDefinition scalarObjectField(@Nonnull Field field) {
        GraphQLScalarType scalar = field.getAnnotation(GQLField.class).type().graphQLScalarType;
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(scalar)
                                     .build();
    }

    private static @Nonnull GraphQLFieldDefinition listObjectField(@Nonnull Field field) {
        String typeName = genericTypeOfField(field).getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                     .build();
    }

    private static @Nonnull GraphQLFieldDefinition objectObjectField(@Nonnull Field field) {
        String type = field.getType().getSimpleName();
        return GraphQLFieldDefinition.newFieldDefinition()
                                     .name(field.getName())
                                     .type(GraphQLTypeReference.typeRef(type))
                                     .build();
    }

    private static @Nonnull GraphQLFieldDefinition enumObjectField(@Nonnull Field field) {
        return objectObjectField(field);
    }

    private static @Nonnull GraphQLInputObjectField scalarInputField(@Nonnull Field field) {
        GraphQLScalarType scalar = field.getAnnotation(GQLField.class).type().graphQLScalarType;
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(field.getName())
                                      .type(scalar)
                                      .build();
    }

    private static @Nonnull GraphQLInputObjectField listInputField(@Nonnull Field field) {
        String typeName = genericTypeOfField(field).getSimpleName();
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(field.getName())
                                      .type(GraphQLList.list(GraphQLTypeReference.typeRef(typeName)))
                                      .build();
    }

    private static @Nonnull GraphQLInputObjectField objectInputField(@Nonnull Field field) {
        String type = field.getType().getSimpleName();
        return GraphQLInputObjectField.newInputObjectField()
                                      .name(field.getName())
                                      .type(GraphQLTypeReference.typeRef(type))
                                      .build();
    }

    private static @Nonnull GraphQLInputObjectField enumInputField(@Nonnull Field field) {
        return objectInputField(field);
    }
}
