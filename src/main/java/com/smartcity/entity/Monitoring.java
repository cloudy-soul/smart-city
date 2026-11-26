package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import java.util.Map;

/**
 * Monitoring - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "monitorings")
public class Monitoring {

    @Column(nullable = false)
    private String recordType;
    @Column(nullable = false)
    private String source;
    @Column(nullable = false)
    private String status;
    @Transient
    private Map data;
    @Column(nullable = false)
    private Instant timestamp;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected Monitoring() {
    }

    public Monitoring(String recordType, String source, String status, Map data, Instant timestamp) {
        this.recordType = recordType;
        this.source = source;
        this.status = status;
        this.data = data;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void recordEvent() {
        // TODO(diagram2app): the behaviour of Monitoring.recordEvent() is not specified in the diagram.
        throw new UnsupportedOperationException("Monitoring.recordEvent() is not implemented: behaviour not specified");
    }

    public void retrieveRecords() {
        // TODO(diagram2app): the behaviour of Monitoring.retrieveRecords() is not specified in the diagram.
        throw new UnsupportedOperationException("Monitoring.retrieveRecords() is not implemented: behaviour not specified");
    }

    @Override
    public String toString() {
        return "Monitoring{" + "recordType=" + getRecordType() + ", " + "source=" + getSource() + ", " + "status=" + getStatus() + ", " + "data=" + getData() + ", " + "timestamp=" + getTimestamp() + "}";
    }
}
