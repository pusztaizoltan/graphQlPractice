package org.example.test_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLInput;
import org.example.graphql.annotation.GQLType;
import org.example.test_entity.Reader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@Getter
@NoArgsConstructor
@GQLInput()
public class ReaderDTO{
    @GQLField
    private Integer id;
    @GQLField
    private String fullName;
    @GQLField
    private String email;

    public ReaderDTO(@Nullable Integer id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }


    /**
     * Used by reflection as primary GraphQL-input wiring tool
     */
    @SuppressWarnings("unused")
    public static @Nonnull ReaderDTO fromMap(@Nonnull Map<String, Object> argMap) {
        return new ReaderDTO(
                (Integer) argMap.get("id"),
                (String) argMap.get("fullName"),
                (String) argMap.get("email"));
    }

    public @Nonnull Reader toReaderOfId(long id) {
        return new Reader(id, fullName, email);
    }

    public @Nonnull Reader toReaderOfId() {
        return new Reader(id, fullName, email);
    }
}
