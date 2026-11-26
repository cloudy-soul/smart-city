package com.smartcity.media;

/**
 * mediaAgent - generated from the UML diagram by diagram2app.
 */
public class mediaAgent implements Interface1 {
    // Existing generated code

    private String fileName;
    private String fileType;

    public mediaAgent(String fileName, String fileType) {
        this.fileName = fileName;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

  
    @Override
    public String generateLink() {
        throw new UnsupportedOperationException(
            "generateLink() is not implemented yet"
        );
    }

    protected String generatePath() {
        // TODO(diagram2app): the behaviour of mediaAgent.generatePath() is not specified in the diagram.
        throw new UnsupportedOperationException("mediaAgent.generatePath() is not implemented: behaviour not specified");
    }

    @Override
    public String toString() {
        return "mediaAgent{" + "fileName=" + getFileName() + ", " + "fileType=" + getFileType() + "}";
    }
}
