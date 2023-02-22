package org.example.test_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLInput;
import org.example.graphql.annotation.GQLType;
import org.example.test_entity.Author;

import javax.annotation.Nonnull;

@Getter
@Setter
@NoArgsConstructor
@GQLInput()
public class AuthorDTO {
    @GQLField
    private Integer id;
    @GQLField
    private String name;
    @GQLField
    // todo remember don't name as isAlive because PropertyDescriptor will search for isIsAlive accessor
    private boolean alive;

    public @Nonnull Author toAuthorOfId(long id) {
        return new Author(id, name, alive);
    }

    public @Nonnull Author toAuthorOfId() {
        return new Author(id, name, alive);
    }
}
