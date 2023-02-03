package org.example.entity;

import lombok.Getter;

@Getter
public class Client {
    private final String fullName;
    private final String email;

    public Client(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }
}
