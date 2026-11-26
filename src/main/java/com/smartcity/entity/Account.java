package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

/**
 * Account - generated from the UML diagram by diagram2app.
 */
@Embeddable
public class Account {

    @NotBlank
    @Column
    private String username;
    @NotBlank
    @Column
    private String passwordHash;
    protected Account() {
    }

    public Account(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

public String getUsername() {
        return this.username;
    }

    @Override
    public String toString() {
        return "Account{" + "username=" + getUsername() + ", " + "passwordHash=" + getPasswordHash() + "}";
    }
}
