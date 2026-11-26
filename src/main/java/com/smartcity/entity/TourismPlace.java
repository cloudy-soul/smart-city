package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * TourismPlace - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "tourism_places")
public class TourismPlace extends Place {
    // Keep existing fields and methods

    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private String openingHours;
    private double rating;

    protected TourismPlace() {
    }

    public TourismPlace(String name, String description, double latitude, double longitude, String address, City city, String category, String openingHours, double rating) {
        super(name, description, latitude, longitude, address, city);
        this.category = category;
        this.openingHours = openingHours;
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "TourismPlace{" + "id=" + getId() + ", " + "name=" + getName() + ", " + "description=" + getDescription() + ", " + "latitude=" + getLatitude() + ", " + "longitude=" + getLongitude() + ", " + "address=" + getAddress() + ", " + "category=" + getCategory() + ", " + "openingHours=" + getOpeningHours() + ", " + "rating=" + getRating() + "}";
    }
}
