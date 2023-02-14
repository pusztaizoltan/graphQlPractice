package org.example.test_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.test_entity.Reader;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReaderDTO {
    // todo see if not primitive id can cause problem
    @FieldOf(type = GQLType.SCALAR_INT)
    private Integer id;
    @FieldOf(type = GQLType.SCALAR_STRING)
    private String fullName;
    @FieldOf(type = GQLType.SCALAR_STRING)
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
