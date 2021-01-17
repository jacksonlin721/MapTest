package com.example.photo.model;

import android.net.Uri;

import java.io.File;

public class FolderModel {
    Uri folderImgUri;
    String folderName;
    int type;
    File folder;
    public static int TYPE_FOLDER = 0;
    public static int TYPE_FILE = 1;

    public FolderModel() {

    }

    public Uri getFolderImgUri() {
        return folderImgUri;
    }

    public void setFolderImgUri(Uri folderImgUri) {
        this.folderImgUri = folderImgUri;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }
}
