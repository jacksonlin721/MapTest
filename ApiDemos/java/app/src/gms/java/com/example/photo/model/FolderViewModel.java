package com.example.photo.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FolderViewModel extends ViewModel {
    public MutableLiveData<FolderModel> folderModel = new MutableLiveData<>();

    public FolderViewModel(){}


}
