package org.example.test_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLInput;
import org.example.graphql.annotation.GQLType;
import org.example.test_entity.Author;

import javax.annotation.Nonnull;

@Getter
@NoArgsConstructor
@GQLInput()
public class AuthorDTO {
    @GQLField(type = GQLType.SCALAR_INT)
    private Integer id;
    @GQLField(type = GQLType.SCALAR_STRING)
    private String name;
    @GQLField(type = GQLType.SCALAR_BOOLEAN)
    private boolean isAlive;

    public @Nonnull Author toAuthorOfId(long id) {
        return new Author(id, name, isAlive);
    }

    public @Nonnull Author toAuthorOfId() {
        return new Author(id, name, isAlive);
    }
}
