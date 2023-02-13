package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.entity.Reader;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

@Getter
@NoArgsConstructor
@TypeOf(type = GQLType.INPUT)
public class ReaderDTO {
    // todo see if not primitive id can cause problem
    @FieldOf(type = GQLType.SCALAR_INT)
    private Integer id;
    @FieldOf(type = GQLType.SCALAR_STRING)
    private String fullName;
    @FieldOf(type = GQLType.SCALAR_STRING)
    private String email;

    public Reader toReaderOfId(long id) {
        return new Reader(id, fullName, email);
    }

    public Reader toReaderOfId() {
        return new Reader(id, fullName, email);
    }
}
