package com.example.photo.presenter;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

import com.example.photo.model.PhotoModel;

public class PhotoDataSourceFactory extends DataSource.Factory<Integer, PhotoModel> {
    PhotoPresenter mPhotoPresenter;

    public PhotoDataSourceFactory(PhotoPresenter photoPresenter) {
        this.mPhotoPresenter = photoPresenter;
    }

    @NonNull
    @Override
    public DataSource<Integer, PhotoModel> create() {
        return new PhotoDataSource(mPhotoPresenter);
    }
}
