package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Tourists - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "touristses")
public class Tourists {

    @NotNull
    @Column(nullable = false)
    private Long touristID;
    @Column(nullable = false)
    private String preferredLanguage;
    @Column(nullable = false)
    private String travelPurpose;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected Tourists() {
    }

    public Tourists(Long touristID, String preferredLanguage, String travelPurpose) {
        this.touristID = touristID;
        this.preferredLanguage = preferredLanguage;
        this.travelPurpose = travelPurpose;
    }

    public Long getId() {
        return id;
    }

    public Long getTouristID() {
        return touristID;
    }

    public void setTouristID(Long touristID) {
        this.touristID = touristID;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getTravelPurpose() {
        return travelPurpose;
    }

    public void setTravelPurpose(String travelPurpose) {
        this.travelPurpose = travelPurpose;
    }

    @Override
    public String toString() {
        return "Tourists{" + "touristID=" + getTouristID() + ", " + "preferredLanguage=" + getPreferredLanguage() + ", " + "travelPurpose=" + getTravelPurpose() + "}";
    }
}
