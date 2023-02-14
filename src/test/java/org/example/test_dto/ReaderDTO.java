package org.example.test_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.graphql.annotation.GQLField;
import org.example.graphql.annotation.GQLInput;
import org.example.graphql.annotation.GQLType;
import org.example.test_entity.Reader;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@GQLInput()
public class ReaderDTO {
    @GQLField(type = GQLType.SCALAR_INT)
    private Integer id;
    @GQLField(type = GQLType.SCALAR_STRING)
    private String fullName;
    @GQLField(type = GQLType.SCALAR_STRING)
    private String email;

    public ReaderDTO fromMap(Map<String, Object> argMap) {
        return new ReaderDTO(
                (Integer) argMap.get("id"),
                (String) argMap.get("fullName"),
                (String) argMap.get("email"));
    }

    public Reader toReaderOfId(long id) {
        return new Reader(id, fullName, email);
    }

    public Reader toReaderOfId() {
        return new Reader(id, fullName, email);
    }
}
