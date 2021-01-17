package com.example.mapdemo;

import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photo.adapter.FolderAdapter;
import com.example.photo.model.FolderModel;
import com.example.photo.model.FolderViewModel;
import com.example.photo.presenter.FolderPresenter;
import com.example.photo.presenter.FolderView;

import java.io.File;
import java.util.ArrayList;

public class FolderTest extends AppCompatActivity implements FolderView.View, FolderView.Presenter {
    FolderPresenter presenter;
    FolderAdapter folderAdapter;
    RecyclerView recyclerView;
    File rootFolder;
    File curFolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder_view);
        initViewModel();
        initComponent();

        rootFolder = Environment.getExternalStorageDirectory();
    }

    private void initComponent() {
        presenter = new FolderPresenter(this);
        folderAdapter = new FolderAdapter(this);
        recyclerView = findViewById(R.id.recyclerView_folder);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(folderAdapter);
    }

    private void initViewModel() {
        FolderViewModel folderViewModel = new ViewModelProvider(
                this, new ViewModelProvider.NewInstanceFactory()).get(FolderViewModel.class);
        folderViewModel.folderModel.observe(this, new Observer<FolderModel>() {
            @Override
            public void onChanged(FolderModel folderModel) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        curFolder = rootFolder;
        presenter.listSubFolder(rootFolder);
    }

    @Override
    public void updateData(ArrayList<FolderModel> folderList) {
        folderAdapter.updateData(folderList);
    }

    @Override
    public void onClick(File folder) {
        curFolder = folder;
        presenter.listSubFolder(folder);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (curFolder.getAbsolutePath().equals(rootFolder.getAbsolutePath())) {
            finish();
        } else {
            onClick(curFolder.getParentFile());
        }
    }
}
