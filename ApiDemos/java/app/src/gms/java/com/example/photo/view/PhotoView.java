package com.example.photo.view;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mapdemo.R;
import com.example.photo.adapter.PhotoAdapter;
import com.example.photo.model.PhotoModel;
import com.example.photo.presenter.PhotoPresenter;

import java.io.File;
import java.util.ArrayList;

public class PhotoView extends Activity implements IGetPhotoView {
    PhotoAdapter photoAdapter;
    PhotoPresenter photoPresenter;
    ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photolist);
        photoPresenter = new PhotoPresenter(this);
        photoAdapter = new PhotoAdapter(this, photoPresenter);
        setRecyclerView();
        if(checkPermisstion()) {
            photoPresenter.getPhotoList();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(photoAdapter);
    }

    private boolean checkPermisstion() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        3
                );
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        3
                );
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 3:
                if(permissions.length > 1 &&
                        permissions[0].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        grantResults.length > 1 &&
                        grantResults[0] != -1) {
                    photoPresenter.getPhotoList();
                }
                break;
        }
    }

    @Override
    public void updateData(ArrayList<PhotoModel> photoArrayList) {
        photoAdapter.updateData(photoArrayList);
    }

}
