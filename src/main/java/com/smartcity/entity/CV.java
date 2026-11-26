package com.smartcity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CV - generated from the UML diagram by diagram2app.
 */
@Entity
@Table(name = "cvs")
public class CV {

    @NotBlank
    @Column(nullable = false)
    protected String title;
    @Column(nullable = false)
    protected String description;
    @Column(nullable = false)
    protected Instant lastUpdate;
    @ElementCollection
    protected List<String> keyWords = new ArrayList<>();
    @ElementCollection
    protected List<String> skills = new ArrayList<>();
    @Column(nullable = false)
    protected String fileType;
    @Column(nullable = false)
    private String storageServer;
    @Column(nullable = false)
    private String storagePath;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String fileExtension;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected CV() {
    }

    public CV(String title, String description, Instant lastUpdate, String fileType, String storageServer, String storagePath, String fileName, String fileExtension) {
        this.title = title;
        this.description = description;
        this.lastUpdate = lastUpdate;
        this.fileType = fileType;
        this.storageServer = storageServer;
        this.storagePath = storagePath;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
    }

    public Long getId() {
        return id;
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

    public Instant getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<String> getKeyWords() {
        return Collections.unmodifiableList(keyWords);
    }

    public void addKeyWord(String item) {
        if (item != null && !keyWords.contains(item)) {
            keyWords.add(item);
        }
    }

    public void removeKeyWord(String item) {
        keyWords.remove(item);
    }

    public List<String> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    public void addSkill(String item) {
        if (item != null && !skills.contains(item)) {
            skills.add(item);
        }
    }

    public void removeSkill(String item) {
        skills.remove(item);
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getStorageServer() {
        return storageServer;
    }

    public void setStorageServer(String storageServer) {
        this.storageServer = storageServer;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void updateMetadata(String title, String description) {
        // TODO(diagram2app): the behaviour of CV.updateMetadata() is not specified in the diagram.
        throw new UnsupportedOperationException("CV.updateMetadata() is not implemented: behaviour not specified");
    }

    public String generateLink() {
        // TODO(diagram2app): the behaviour of CV.generateLink() is not specified in the diagram.
        throw new UnsupportedOperationException("CV.generateLink() is not implemented: behaviour not specified");
    }

    @Override
    public String toString() {
        return "CV{" + "title=" + getTitle() + ", " + "description=" + getDescription() + ", " + "lastUpdate=" + getLastUpdate() + ", " + "fileType=" + getFileType() + ", " + "storageServer=" + getStorageServer() + ", " + "storagePath=" + getStoragePath() + ", " + "fileName=" + getFileName() + ", " + "fileExtension=" + getFileExtension() + "}";
    }
}
