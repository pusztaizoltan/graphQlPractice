package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.entity.Reader;
import org.example.graphql.annotation.FieldOf;
import org.example.graphql.annotation.FieldType;

@Getter
@AllArgsConstructor
// todo imputtype marker interface?
public class ReaderDTO {
    @FieldOf(type = FieldType.SCALAR_STRING)
    private final String fullName;
    @FieldOf(type = FieldType.SCALAR_STRING)
    private final String email;

    public Reader toReaderOfId(long id) {
        return new Reader(id, fullName, email);
    }
}
