package com.example.photo.presenter;

import android.net.Uri;
import android.os.Environment;

import com.example.mapdemo.FolderTest;
import com.example.photo.model.FolderModel;

import java.io.File;
import java.util.ArrayList;

public class FolderPresenter {
    FolderModel folderModel;
    ArrayList<FolderModel> fileList;
    FolderView.View mView;

    public FolderPresenter(FolderTest folderTest) {
        mView = (FolderView.View) folderTest;
    }

    public void getFolders() {
//        File file = Environment.getExternalStorageDirectory();
//        listSubFolder(file);
    }

    public void listSubFolder(File dir) {
        File file[] = dir.listFiles();
        fileList = new ArrayList<>();
        for (File files : file) {
            folderModel = new FolderModel();
            folderModel.setType(
                    files.isDirectory()?
                            FolderModel.TYPE_FOLDER : FolderModel.TYPE_FILE);
            folderModel.setFolderName(files.getName());
            folderModel.setFolderImgUri(
                    Uri.fromFile(files));
            folderModel.setFolder(files);
            fileList.add(folderModel);
        }
        mView.updateData(fileList);
    }
}
