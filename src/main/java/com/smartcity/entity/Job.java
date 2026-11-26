package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Job - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "jobs")
public class Job extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String industry;
    @Column(nullable = false)
    private String employmentType;
    private double salary;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    protected Job() {
    }

    public Job(String title, String description, String industry, String employmentType, double salary) {
        this.title = title;
        this.description = description;
        this.industry = industry;
        this.employmentType = employmentType;
        this.salary = salary;
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

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "Job{" + "id=" + getId() + ", " + "title=" + getTitle() + ", " + "description=" + getDescription() + ", " + "industry=" + getIndustry() + ", " + "employmentType=" + getEmploymentType() + ", " + "salary=" + getSalary() + "}";
    }
}
