package com.example.photo.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.photo.presenter.PhotoDataSourceFactory;
import com.example.photo.presenter.PhotoPresenter;

public class PhotoViewModel extends ViewModel {
    public LiveData<PagedList<PhotoModel>> photoList;
    PhotoPresenter mPhotoPresenter;

    public PhotoViewModel() {
    }

    public void initPhotoListLiveData() {
        PagedList.Config config =
                new PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build();

        photoList = new LivePagedListBuilder<>(
                new PhotoDataSourceFactory(mPhotoPresenter), config)
                .build();
    }

    public void setPresenter(PhotoPresenter photoPresenter) {
        this.mPhotoPresenter = photoPresenter;
    }
}
