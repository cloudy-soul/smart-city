package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Company - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "companies")
public class Company {

    @NotBlank
    @Column(nullable = false)
    private String name;
    @NotBlank
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String region;
    @Column(nullable = false)
    private String address;
    @NotBlank
    @Column(nullable = false)
    private String phone;
    @NotBlank
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String tax;
    @ElementCollection
    private List<String> specialties = new ArrayList<>();
    @Column(nullable = false)
    private String domainName;
    @Embedded
    private ForwardAgent forwardAgent;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;

    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    protected Company() {
    }

    public Company(String name, String title, String description, String country, String city, String region, String address, String phone, String email, String tax, String domainName) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.country = country;
        this.city = city;
        this.region = region;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.tax = tax;
        this.domainName = domainName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public List<String> getSpecialties() {
        return Collections.unmodifiableList(specialties);
    }

    public void addSpecialty(String item) {
        if (item != null && !specialties.contains(item)) {
            specialties.add(item);
        }
    }

    public void removeSpecialty(String item) {
        specialties.remove(item);
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public ForwardAgent getForwardAgent() {
        return forwardAgent;
    }

    public void setForwardAgent(ForwardAgent forwardAgent) {
        this.forwardAgent = forwardAgent;
    }

    @Override
    public String toString() {
        return "Company{" + "name=" + getName() + ", " + "title=" + getTitle() + ", " + "description=" + getDescription() + ", " + "country=" + getCountry() + ", " + "city=" + getCity() + ", " + "region=" + getRegion() + ", " + "address=" + getAddress() + ", " + "phone=" + getPhone() + ", " + "email=" + getEmail() + ", " + "tax=" + getTax() + ", " + "domainName=" + getDomainName() + "}";
    }
}
