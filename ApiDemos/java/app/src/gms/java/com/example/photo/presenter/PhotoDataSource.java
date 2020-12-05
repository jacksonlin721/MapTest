package com.example.photo.presenter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

class PhotoDataSource extends PositionalDataSource {
    PhotoPresenter mPhotoPresenter;
    private String TAG = "PhotoDataSource";

    public PhotoDataSource(PhotoPresenter photoPresenter) {
        this.mPhotoPresenter = photoPresenter;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback callback) {
        Log.e(TAG,"[loadInitial] start= "+params.requestedStartPosition+", size= "+params.requestedLoadSize);
        callback.onResult(
                mPhotoPresenter.getLocalPhoto(params.requestedLoadSize, params.requestedStartPosition),
                0);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback callback) {
        Log.e(TAG,"[loadRange] start= "+params.startPosition+", size= "+params.loadSize);
        callback.onResult(
                mPhotoPresenter.getLocalPhoto(params.loadSize, params.startPosition+1));
    }

}
