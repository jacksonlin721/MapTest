package com.example.photo.presenter;

import com.example.photo.model.FolderModel;

import java.io.File;
import java.util.ArrayList;

public interface FolderView {

    public interface View {
        void updateData(ArrayList<FolderModel> folderList);
    }

    public interface Presenter {
        void onClick(File folder);
    }
}
