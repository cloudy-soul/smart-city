package com.smartcity.entity;

import com.smartcity.enums.ApplicationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
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
        // TODO(diagram2app): the behaviour of JobApplication.submitApplication() is not specified in the diagram.
        throw new UnsupportedOperationException("JobApplication.submitApplication() is not implemented: behaviour not specified");
    }

    public void updateStatus(ApplicationStatus status) {
        // TODO(diagram2app): the behaviour of JobApplication.updateStatus() is not specified in the diagram.
        throw new UnsupportedOperationException("JobApplication.updateStatus() is not implemented: behaviour not specified");
    }

    @Override
    public String toString() {
        return "JobApplication{" + "id=" + getId() + ", " + "applicationDate=" + getApplicationDate() + "}";
    }
}
