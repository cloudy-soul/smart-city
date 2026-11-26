package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Token - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Instant creationDate;
    @Column(nullable = false)
    private Instant expirationDateTime;
    @Column(nullable = false)
    private String tokenValue;

    protected Token() {
    }

    public Token(Instant creationDate, Instant expirationDateTime, String tokenValue) {
        this.creationDate = creationDate;
        this.expirationDateTime = expirationDateTime;
        this.tokenValue = tokenValue;
    }

    public Long getId() {
        return id;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(Instant expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public boolean isExpired() {
        // TODO(diagram2app): the behaviour of Token.isExpired() is not specified in the diagram.
        throw new UnsupportedOperationException("Token.isExpired() is not implemented: behaviour not specified");
    }

    @Override
    public String toString() {
        return "Token{" + "id=" + getId() + ", " + "creationDate=" + getCreationDate() + ", " + "expirationDateTime=" + getExpirationDateTime() + ", " + "tokenValue=" + getTokenValue() + "}";
    }
}
