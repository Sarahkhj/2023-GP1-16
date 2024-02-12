package com.example.myapplication324;

import com.google.firebase.database.Exclude;

import java.util.Map;

public class FolderMetadata {


    @Exclude
    private String key;
    private String folderId;
    private String folderName;
    private Map<String, Boolean> subfolders; // Map to store subfolders

    public FolderMetadata(String folderId, String folderName, Map<String, Boolean> subfolders) {
        this.folderId = folderId;
        this.folderName = folderName;
        this.subfolders = subfolders;
    }

    private String parentFolderId; // Add this field

    public String getParentFolderId() {
        return parentFolderId;
    }

    public Map<String, Boolean> getSubfolders() {
        return subfolders;
    }

    public void setSubfolders(Map<String, Boolean> subfolders) {
        this.subfolders = subfolders;
    }



    public FolderMetadata() {
        // Default constructor required for Firebase
    }

    public FolderMetadata(String folderId, String folderName) {
        this.folderId = folderId;
        this.folderName = folderName;
    }

    public FolderMetadata(String subFolderId, String subFolderName, Object o) {

    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setParentFolderId(String parentFolderId) {
        this.parentFolderId = parentFolderId;
    }





}

