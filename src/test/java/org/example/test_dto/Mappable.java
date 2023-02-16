package org.example.test_dto;

import javax.annotation.Nonnull;
import java.util.Map;

public interface Mappable<T> {
    @Nonnull T fromMap(@Nonnull Map<String, Object> argMap);
}
