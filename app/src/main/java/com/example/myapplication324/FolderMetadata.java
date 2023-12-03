package com.example.myapplication324;

public class FolderMetadata {

    private String folderId;
    private String folderName;

    public FolderMetadata() {
        // Default constructor required for Firebase
    }

    public FolderMetadata(String folderId, String folderName) {
        this.folderId = folderId;
        this.folderName = folderName;
    }

    public FolderMetadata(String subFolderId, String subFolderName, Object o) {

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

}

