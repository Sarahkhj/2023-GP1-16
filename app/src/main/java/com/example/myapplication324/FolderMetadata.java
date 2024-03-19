
package com.example.myapplication324;

import com.google.firebase.database.Exclude;

import java.util.List;
import java.util.Map;

public class FolderMetadata {
    private List<FileMetadata> files;
    private List<FolderMetadata> subfolders;
    @Exclude
    private String key;
    private String folderId;
    private String folderName;
    //private String parentFolderId;

    public FolderMetadata() {
        // Default constructor required for Firebase
    }

    public FolderMetadata(String folderId, String folderName) {
        this.folderId = folderId;
        this.folderName = folderName;
    }

    // Getters and setters

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
    public List<FileMetadata> getFiles() {
        return files;
    }
    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

//    public Map<String, Boolean> getSubfolders() {
//        return subfolders;
//    }

//    public void setSubfolders(Map<String, Boolean> subfolders) {
//        this.subfolders = subfolders;
//    }

//    public String getParentFolderId() {
//        return parentFolderId;
//    }
//
//    public void setParentFolderId(String parentFolderId) {
//        this.parentFolderId = parentFolderId;
//    }
}
