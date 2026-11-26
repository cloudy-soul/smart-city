package com.smartcity.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * OnlineSession - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "online_sessions")
public class OnlineSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "cookies_id", nullable = false))
    private Cookies cookies;
    @Column(nullable = false)
    private Instant timeStamp;
    private boolean authenticated;
    @OneToOne
    @JoinColumn(name = "token_id")
    private Token token;
    @Embedded
    private Account account;

    protected OnlineSession() {
    }

    public OnlineSession(Cookies cookies, Instant timeStamp, boolean authenticated, Token token) {
        this.cookies = cookies;
        this.timeStamp = timeStamp;
        this.authenticated = authenticated;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public Cookies getCookies() {
        return cookies;
    }

    public void setCookies(Cookies cookies) {
        this.cookies = cookies;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void startSession() {
        // TODO(diagram2app): the behaviour of OnlineSession.startSession() is not specified in the diagram.
        throw new UnsupportedOperationException("OnlineSession.startSession() is not implemented: behaviour not specified");
    }

    public void endSession() {
        // TODO(diagram2app): the behaviour of OnlineSession.endSession() is not specified in the diagram.
        throw new UnsupportedOperationException("OnlineSession.endSession() is not implemented: behaviour not specified");
    }

    public boolean validateSession() {
        // TODO(diagram2app): the behaviour of OnlineSession.validateSession() is not specified in the diagram.
        throw new UnsupportedOperationException("OnlineSession.validateSession() is not implemented: behaviour not specified");
    }

    @Override
    public String toString() {
        return "OnlineSession{" + "id=" + getId() + ", " + "timeStamp=" + getTimeStamp() + ", " + "authenticated=" + isAuthenticated() + "}";
    }
}
