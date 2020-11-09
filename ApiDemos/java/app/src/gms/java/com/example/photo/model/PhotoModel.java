package com.example.photo.model;

import android.net.Uri;

public class PhotoModel {
    String name;
    Uri uri;
    int size;
    String path;

    public PhotoModel(Uri uri, String name, int size, String path) {
        this.uri = uri;
        this.name = name;
        this.size = size;
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }
}
