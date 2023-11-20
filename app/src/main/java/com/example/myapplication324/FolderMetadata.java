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
//    private String folderName;
//    private String folderPath;
//
//    public FolderMetadata() {
//        // Default constructor required for Firebase
//    }
//
//    public FolderMetadata(String folderName, String folderPath) {
//        this.folderName = folderName;
//        this.folderPath = folderPath;
//    }
//
//    public String getFolderName() {
//        return folderName;
//    }
//
//    public String getFolderPath() {
//        return folderPath;
//    }
//}
//
