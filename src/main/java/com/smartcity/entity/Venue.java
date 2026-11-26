package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Venue - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "venues")

public class Venue extends Place {
    // Keep existing fields and methods

    @Column(nullable = false)
    private String venueType;
    private int capacity;

    protected Venue() {
    }

    public Venue(String name, String description, double latitude, double longitude, String address, City city, String venueType, int capacity) {
        super(name, description, latitude, longitude, address, city);
        this.venueType = venueType;
        this.capacity = capacity;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Venue{" + "id=" + getId() + ", " + "name=" + getName() + ", " + "description=" + getDescription() + ", " + "latitude=" + getLatitude() + ", " + "longitude=" + getLongitude() + ", " + "address=" + getAddress() + ", " + "venueType=" + getVenueType() + ", " + "capacity=" + getCapacity() + "}";
    }
}
