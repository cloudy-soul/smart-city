package com.smartcity.entity;

import com.smartcity.enums.ApplicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * JobApplication - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "job_applications")
public class JobApplication extends BaseEntity {

    @Column(nullable = false)
    private Instant applicationDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    @ManyToOne
    @JoinColumn(name = "c_v_id")
    private CV cV;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private Users user;

    @Column(nullable = false, length = 2000)
    private String coverNote = "";

    @Column(nullable = false)
    private boolean sampleSubmission;

    public Users getUser() { return user; }
    public void setUser(Users user) { this.user = user; }
    public String getCoverNote() { return coverNote; }
    public boolean isSampleSubmission() { return sampleSubmission; }

    public void submit(Users user, String coverNote) {
        this.user = user;
        this.coverNote = coverNote;
        this.sampleSubmission = true;
        submitApplication();
    }

    protected JobApplication() {
    }

    public JobApplication(Instant applicationDate, ApplicationStatus status) {
        this.applicationDate = applicationDate;
        this.status = status;
    }

    public Instant getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Instant applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public CV getCV() {
        return cV;
    }

    public void setCV(CV cV) {
        this.cV = cV;
    }

    public void submitApplication() {
        status = ApplicationStatus.PENDING;
    }

    public void updateStatus(ApplicationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "JobApplication{" + "id=" + getId() + ", " + "applicationDate=" + getApplicationDate() + "}";
    }
}
