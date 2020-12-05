package com.example.photo.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mapdemo.R;
import com.example.photo.model.PhotoModel;

public class PhotoPagedListAdapter extends PagedListAdapter {
    Context mContext;

    public PhotoPagedListAdapter(Context context) {
        this(DIFF_CALLBACK, context);
    }

    protected PhotoPagedListAdapter(@NonNull DiffUtil.ItemCallback diffCallback, Context context) {
        super(DIFF_CALLBACK);
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoview, parent, false);
        RecyclerView.ViewHolder viewHolder = new PhotoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PhotoModel photoModel = (PhotoModel) getItem(position);
        displayPhoto(photoModel.getUri(), ((PhotoViewHolder)holder).imageView);
        ((PhotoViewHolder)holder).index.setText(String.valueOf(position));
    }

    private void displayPhoto(Uri uri, ImageView imageView) {
        Glide.with(mContext)
                .load(uri)
                .centerCrop()
                .into(imageView);
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView index;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            index = itemView.findViewById(R.id.index);
        }
    }

    private static DiffUtil.ItemCallback<PhotoModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<PhotoModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull PhotoModel oldItem, @NonNull PhotoModel newItem) {
                    return oldItem.getUri().toString().equals(newItem.getUri().toString());
                }

                @Override
                public boolean areContentsTheSame(@NonNull PhotoModel oldItem, @NonNull PhotoModel newItem) {
                    return oldItem.equals(newItem);
                }
            };

}
