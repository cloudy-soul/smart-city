package com.smartcity.entity;

import com.smartcity.util.Coordinates;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Place - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "places")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Place extends BaseEntity {
    // Keep all existing attributes, methods and constructors

    // Keep your existing attributes and methods

    @NotBlank
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    private double latitude;
    private double longitude;
    @Column(nullable = false)
    private String address;
    @ManyToOne(optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    protected Place() {
    }

    public Place(String name, String description, double latitude, double longitude, String address, City city) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getName(String language) {
        // TODO(diagram2app): the behaviour of Place.getName() is not specified in the diagram.
        throw new UnsupportedOperationException("Place.getName() is not implemented: behaviour not specified");
    }

    public Coordinates getCoordinates() {
        // TODO(diagram2app): the behaviour of Place.getCoordinates() is not specified in the diagram.
        throw new UnsupportedOperationException("Place.getCoordinates() is not implemented: behaviour not specified");
    }

    @Override
    public String toString() {
        return "Place{" + "id=" + getId() + ", " + "name=" + getName() + ", " + "description=" + getDescription() + ", " + "latitude=" + getLatitude() + ", " + "longitude=" + getLongitude() + ", " + "address=" + getAddress() + "}";
    }
}
