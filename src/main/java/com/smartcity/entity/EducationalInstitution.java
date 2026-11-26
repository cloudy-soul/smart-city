package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * EducationalInstitution - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "educational_institutions")
public class EducationalInstitution extends Place {
    // Keep existing fields and methods

    @Column(nullable = false)
    private String institutionType;
    @Column(nullable = false)
    private String website;
    @NotBlank
    @Column(nullable = false)
    private String contactEmail;

    protected EducationalInstitution() {
    }

    public EducationalInstitution(String name, String description, double latitude, double longitude, String address, City city, String institutionType, String website, String contactEmail) {
        super(name, description, latitude, longitude, address, city);
        this.institutionType = institutionType;
        this.website = website;
        this.contactEmail = contactEmail;
    }

    public String getInstitutionType() {
        return institutionType;
    }

    public void setInstitutionType(String institutionType) {
        this.institutionType = institutionType;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @Override
    public String toString() {
        return "EducationalInstitution{" + "id=" + getId() + ", " + "name=" + getName() + ", " + "description=" + getDescription() + ", " + "latitude=" + getLatitude() + ", " + "longitude=" + getLongitude() + ", " + "address=" + getAddress() + ", " + "institutionType=" + getInstitutionType() + ", " + "website=" + getWebsite() + ", " + "contactEmail=" + getContactEmail() + "}";
    }
}
