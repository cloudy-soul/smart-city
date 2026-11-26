package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CVTest {

    private static CV sample() {
        return new CV("sample", "sample", null, "sample", "sample", "sample", "sample", "sample");
    }

    @Test
    void constructsValidInstance() {
        CV o = sample();
        assertNotNull(o);
    }

    @Test
    void titleIsStoredByConstructor() {
        CV o = sample();
        assertEquals("sample", o.getTitle());
    }

    @Test
    void descriptionIsStoredByConstructor() {
        CV o = sample();
        assertEquals("sample", o.getDescription());
    }

    @Test
    void fileTypeIsStoredByConstructor() {
        CV o = sample();
        assertEquals("sample", o.getFileType());
    }

    @Test
    void storageServerIsStoredByConstructor() {
        CV o = sample();
        assertEquals("sample", o.getStorageServer());
    }

    @Test
    void storagePathIsStoredByConstructor() {
        CV o = sample();
        assertEquals("sample", o.getStoragePath());
    }

    @Test
    void fileNameIsStoredByConstructor() {
        CV o = sample();
        assertEquals("sample", o.getFileName());
    }

    @Test
    void fileExtensionIsStoredByConstructor() {
        CV o = sample();
        assertEquals("sample", o.getFileExtension());
    }

    @Test
    void setTitleUpdatesValue() {
        CV o = sample();
        o.setTitle("other");
        assertEquals("other", o.getTitle());
    }

    @Test
    void setDescriptionUpdatesValue() {
        CV o = sample();
        o.setDescription("other");
        assertEquals("other", o.getDescription());
    }

    @Test
    void setFileTypeUpdatesValue() {
        CV o = sample();
        o.setFileType("other");
        assertEquals("other", o.getFileType());
    }

    @Test
    void setStorageServerUpdatesValue() {
        CV o = sample();
        o.setStorageServer("other");
        assertEquals("other", o.getStorageServer());
    }

    @Test
    void setStoragePathUpdatesValue() {
        CV o = sample();
        o.setStoragePath("other");
        assertEquals("other", o.getStoragePath());
    }

    @Test
    void setFileNameUpdatesValue() {
        CV o = sample();
        o.setFileName("other");
        assertEquals("other", o.getFileName());
    }

    @Test
    void setFileExtensionUpdatesValue() {
        CV o = sample();
        o.setFileExtension("other");
        assertEquals("other", o.getFileExtension());
    }

    @Test
    void titleRejects_not_blank() {
        CV o = sample();
        o.setTitle("");
        Validator v = Validation.buildDefaultValidatorFactory().getValidator();
        assertFalse(v.validate(o).isEmpty());
    }

    @Disabled("CV.updateMetadata() behaviour is not specified in the diagram")
    @Test
    void updateMetadata2_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }

    @Disabled("CV.generateLink() behaviour is not specified in the diagram")
    @Test
    void generateLink0_behaviourNotSpecified() {
        // TODO(diagram2app): specify the expected behaviour, implement it, then enable this test.
        fail("not specified");
    }
}
