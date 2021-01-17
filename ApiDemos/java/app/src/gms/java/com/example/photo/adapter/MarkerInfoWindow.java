package com.example.photo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mapdemo.R;
import com.example.photo.model.PhotoModel;
import com.example.photo.presenter.PhotoPresenter;
import com.example.photo.view.PhotoView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkerInfoWindow implements GoogleMap.InfoWindowAdapter {
    View popupWindow;
    PhotoPresenter photoPresenter;
    LayoutInflater inflater;
    Marker mMarker;
    Context context;
    PhotoModel photoModel;

    public MarkerInfoWindow(PhotoPresenter photoPresenter, LayoutInflater inflater, Marker marker, Context context) {
        this.photoPresenter = photoPresenter;
        this.inflater = inflater;
        this.mMarker = marker;
        this.context = context;

        photoModel = (PhotoModel) photoPresenter.getLocalPhoto(PhotoView.ALL_PHOTO, PhotoView.ALL_PHOTO).get(0);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (popupWindow == null) {
            popupWindow = inflater.inflate(R.layout.marker_info_image, null);
        }

        Glide.with(context)
                .load(photoModel.getUri())
                .centerCrop()
                .into((ImageView) popupWindow.findViewById(R.id.marker_info_img));

        return popupWindow;
    }

}
