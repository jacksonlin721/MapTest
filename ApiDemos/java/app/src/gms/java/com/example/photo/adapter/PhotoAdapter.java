package com.example.photo.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.example.mapdemo.R;
import com.example.photo.model.PhotoModel;
import com.example.photo.presenter.PhotoPresenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<ViewHolder> {
    PhotoPresenter photoPresenter;
    Context context;
    ArrayList<PhotoModel> photoArrayList = new ArrayList<>();
    private String TAG = "PhotoAdapter";

    public PhotoAdapter(Context context, PhotoPresenter photoPresenter) {
        this.photoPresenter = photoPresenter;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoview, parent, false);
        ViewHolder viewHolder = new PhotoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PhotoModel photoModel = photoArrayList.get(position);
        showImage(photoModel.getUri(), ((PhotoViewHolder)holder).imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        if (photoArrayList != null) {
            return photoArrayList.size();
        } else {
            return 0;
        }
    }

    private void showImage(Uri uri, ImageView imageView) {
        Glide.with(context)
                .load(uri)
                .centerCrop()
                .into(imageView);
    }

    public void updateData(ArrayList<PhotoModel> photoArrayList) {

        if (this.photoArrayList != null &&
                this.photoArrayList.size() != 0) {
            new UpdateDataTask().execute(photoArrayList);
        } else {
            this.photoArrayList = photoArrayList;
            notifyDataSetChanged();
        }
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    class UpdateDataTask extends AsyncTask<ArrayList<PhotoModel>, Void, DiffUtil.DiffResult> {
        ArrayList<PhotoModel> newData;

        @Override
        protected DiffUtil.DiffResult doInBackground(ArrayList<PhotoModel>... arrayLists) {
            newData = arrayLists[0];
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PhotoDiffCallback(photoArrayList, newData));
            return diffResult;
        }

        @Override
        protected void onPostExecute(DiffUtil.DiffResult diffResult) {
            super.onPostExecute(diffResult);
            diffResult.dispatchUpdatesTo(PhotoAdapter.this);
        }
    }

    private class PhotoDiffCallback extends DiffUtil.Callback {
        ArrayList<PhotoModel> mOldPhotoArrayList;
        ArrayList<PhotoModel> mNewPhotoArrayList;

        public PhotoDiffCallback(ArrayList<PhotoModel> oldPhotoArrayList, ArrayList<PhotoModel> newPhotoArrayList) {
            this.mOldPhotoArrayList = oldPhotoArrayList;
            this.mNewPhotoArrayList = newPhotoArrayList;
        }

        @Override
        public int getOldListSize() {
            return mOldPhotoArrayList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewPhotoArrayList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldItemPosition == newItemPosition;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldPhotoArrayList.get(oldItemPosition).hashCode() ==
                    mNewPhotoArrayList.get(newItemPosition).hashCode();
        }
    }
}
