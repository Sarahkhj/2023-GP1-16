package com.example.myapplication324;

public class FileMetadata {

    private String fileName;
    private String fileDownloadUrl;

    public FileMetadata() {
        // Default constructor is required for Firebase
    }

    public FileMetadata(String fileName, String fileDownloadUrl) {
        this.fileName = fileName;
        this.fileDownloadUrl = fileDownloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileDownloadUrl() {
        return fileDownloadUrl;
    }
}
