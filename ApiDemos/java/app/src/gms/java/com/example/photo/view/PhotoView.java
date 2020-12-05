package com.example.photo.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mapdemo.R;
import com.example.photo.adapter.PhotoAdapter;
import com.example.photo.adapter.PhotoPagedListAdapter;
import com.example.photo.model.PhotoModel;
import com.example.photo.model.PhotoViewModel;
import com.example.photo.presenter.PhotoPresenter;

import java.util.ArrayList;

public class PhotoView extends AppCompatActivity implements IGetPhotoView {
    PhotoAdapter photoAdapter;
    PhotoPagedListAdapter photoPagedListAdapter;
    PhotoPresenter photoPresenter;
    ImageView imageView;
    private String TAG = "PhotoView";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photolist);
        photoPresenter = new PhotoPresenter(this);
//        initNormalAdapter();

        initViewModel();
    }

    private void initNormalAdapter() {
        photoAdapter = new PhotoAdapter(this, photoPresenter);
        setRecyclerView();
        if(checkPermission()) {
            photoPresenter.getPhotoList();
        }
    }

    private void initViewModel() {
        PhotoViewModel photoViewModel = new ViewModelProvider(
                this, new ViewModelProvider.NewInstanceFactory()).get(PhotoViewModel.class);
        photoViewModel.setPresenter(photoPresenter);
        photoPagedListAdapter = new PhotoPagedListAdapter(this);
        setRecyclerView();
        photoViewModel.initPhotoListLiveData();
        photoViewModel.photoList.observe(this, new Observer<PagedList<PhotoModel>>() {
            @Override
            public void onChanged(PagedList<PhotoModel> photoModels) {
                Log.e(TAG,"[onChanged] photo model size= "+photoModels.size());
                photoPagedListAdapter.submitList(photoModels);
            }
        });
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
        recyclerView.setAdapter(photoPagedListAdapter);
    }

    public boolean checkPermission() {
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
