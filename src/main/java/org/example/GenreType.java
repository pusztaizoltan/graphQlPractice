package org.example;

import java.util.Arrays;

public enum GenreType {
    SCIENCE(0),
    ROMANTIC(1),
    FICTION(2),
    FANTASY(3),
    ;
    long id;

    GenreType(long id) {
        this.id = id;
    }
    public static GenreType getById(long id){
        return Arrays.stream(GenreType.values())
                     .filter((genre)->genre.id == id)
                     .findFirst()
                     .orElseThrow(()-> new IllegalArgumentException(String.format("invalid genre id:%s",id)));
    }
}
