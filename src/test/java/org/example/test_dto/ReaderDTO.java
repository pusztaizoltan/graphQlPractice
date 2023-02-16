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
    @GQLField(type = GQLType.SCALAR_INT)
    private Integer id;
    @GQLField(type = GQLType.SCALAR_STRING)
    private String fullName;
    @GQLField(type = GQLType.SCALAR_STRING)
    private String email;

    public ReaderDTO(@Nullable Integer id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }


    /**
     * Used by reflection as primary GraphQLinput wireing tool
     */
    @SuppressWarnings("unused")
    public @Nonnull ReaderDTO fromMap(@Nonnull Map<String, Object> argMap) {
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
