package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * City - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "cities")
public class City extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String description;
    private double latitude;
    private double longitude;

    protected City() {
    }

    public City(String name, String country, String description, double latitude, double longitude) {
        this.name = name;
        this.country = country;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "City{" + "id=" + getId() + ", " + "name=" + getName() + ", " + "country=" + getCountry() + ", " + "description=" + getDescription() + ", " + "latitude=" + getLatitude() + ", " + "longitude=" + getLongitude() + "}";
    }
}
