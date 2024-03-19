package com.example.myapplication324;

import com.google.firebase.database.Exclude;

public class FileMetadata {




    @Exclude
    private String key;
    private String fileName;
    private String fileDownloadUrl;
    private String folderId;
    private boolean favorite;
    private String page;






    public FileMetadata() {
        // Default constructor is required for Firebase
    }


    public FileMetadata(String fileName, String fileDownloadUrl) {
        this.fileName = fileName;
        this.fileDownloadUrl = fileDownloadUrl;
        this.favorite=false;
    }

    public FileMetadata(String fileName, String fileDownloadUrl, String folderId) {
        this.fileName = fileName;
        this.fileDownloadUrl = fileDownloadUrl;
        this.folderId = folderId;
        this.favorite=false;

    }

    public String getFileName() {
        return fileName;
    }

    public String getFileDownloadUrl() {
        return fileDownloadUrl;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFileName(String newName) {
        fileName=newName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isFavorite() {
        return favorite;
    }
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
