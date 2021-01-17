package com.example.photo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mapdemo.FolderTest;
import com.example.mapdemo.R;
import com.example.photo.model.FolderModel;
import com.example.photo.presenter.FolderView;

import java.io.File;
import java.util.ArrayList;

public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FolderView.Presenter {
    ArrayList<FolderModel> folderList = new ArrayList<>();
    Context context;
    FolderView.Presenter presenter;

    public FolderAdapter(FolderTest folderTest) {
        context = folderTest;
        presenter = folderTest;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_layout, null);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        showImage(
                folderList.get(position).getFolderImgUri(),
                ((FolderViewHolder)holder).folderIcon,
                folderList.get(position).getType());
        ((FolderViewHolder)holder).folderName.setText(folderList.get(position).getFolderName());
        ((FolderViewHolder)holder).folderName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onClick(folderList.get(position).getFolder());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (folderList != null &&
                folderList.size() != 0) {
            return folderList.size();
        } else {
            return 0;
        }
    }

    public void updateData(ArrayList<FolderModel> folderList) {
        this.folderList = folderList;
        notifyDataSetChanged();
    }

    private void showImage(Uri uri, ImageView imageView, int folderType) {
        if (folderType == FolderModel.TYPE_FILE) {
            Glide.with(context)
                    .load(uri)
                    .centerCrop()
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.badge_qld)
                    .centerCrop()
                    .into(imageView);
        }
    }

    @Override
    public void onClick(File folder) {

    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        public ImageView folderIcon;
        public TextView folderName;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderIcon = itemView.findViewById(R.id.folder_icon);
            folderName = itemView.findViewById(R.id.folder_name);
        }
    }
}
