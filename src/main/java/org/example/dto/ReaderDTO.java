package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.entity.Reader;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.GQLType;
import org.example.graphql.annotation.TypeOf;

@Getter
@AllArgsConstructor
@TypeOf(type = GQLType.INPUT)
public class ReaderDTO {
    @FieldOf(type = GQLType.SCALAR_STRING)
    private final String fullName;
    @FieldOf(type = GQLType.SCALAR_STRING)
    private final String email;

    public Reader toReaderOfId(long id) {
        return new Reader(id, fullName, email);
    }
}
