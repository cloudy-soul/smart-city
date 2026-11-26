package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Admin - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "admins")
public class Admin {

    private int authorization_level;
    @Column(nullable = false)
    protected Long adminID;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected Admin() {
    }

    public Admin(int authorization_level, Long adminID) {
        this.authorization_level = authorization_level;
        this.adminID = adminID;
    }

    public Long getId() {
        return id;
    }

    public int getAuthorization_level() {
        return authorization_level;
    }

    public void setAuthorization_level(int authorization_level) {
        this.authorization_level = authorization_level;
    }

    public Long getAdminID() {
        return adminID;
    }

    public void setAdminID(Long adminID) {
        this.adminID = adminID;
    }

    @Override
    public String toString() {
        return "Admin{" + "authorization_level=" + getAuthorization_level() + ", " + "adminID=" + getAdminID() + "}";
    }
}
