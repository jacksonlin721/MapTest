package com.example.photo.presenter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.example.photo.model.PhotoModel;
import com.example.photo.view.IGetPhotoView;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

import java.util.ArrayList;

public class PhotoPresenter implements GetPhoto {
    Context mContext;
    ArrayList<PhotoModel> photoArrayList = new ArrayList<>();
    String[] photoProjection = new String[]{
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.Images.Media.SIZE
    };
    String selection = /*MediaStore.Images.Media.BUCKET_ID + " = " + "-1739773001"*/
            MediaStore.MediaColumns.DATA + "=? ";
    String sortOrder =
            MediaStore.Images.Media.DATE_MODIFIED + " DESC";
    IGetPhotoView view;

    public PhotoPresenter(Context context) {
        this.mContext = context;
        this.view = (IGetPhotoView) context;
    }

    @Override
    public void getPhotoList() {
        new GetPhotoAsyncTask().execute();
    }

    class GetPhotoAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Cursor cursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    sortOrder
            );
            // have no idea why does not need to specify project/selection

            cursor.moveToFirst();
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
//                int durationColumn =
//                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            while(cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int size = cursor.getInt(sizeColumn);
                String path = cursor.getString(pathColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
                );

                photoArrayList.add(new PhotoModel(contentUri, name, size, path));
            }

            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            view.updateData(photoArrayList);
        }
    }
}
