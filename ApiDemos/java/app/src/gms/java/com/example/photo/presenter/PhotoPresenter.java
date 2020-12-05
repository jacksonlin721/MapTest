package com.example.photo.presenter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.photo.model.PhotoModel;
import com.example.photo.view.IGetPhotoView;

import java.util.ArrayList;

public class PhotoPresenter implements GetPhoto {
    Context mContext;
    ArrayList<PhotoModel> photoArrayList;
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
            MediaStore.Images.Media.DATE_MODIFIED + " DESC LIMIT ";
    IGetPhotoView view;
    private String TAG = "PhotoPresenter";

    public PhotoPresenter(Context context) {
        this.mContext = context;
        this.view = (IGetPhotoView) context;
    }

    @Override
    public void getPhotoList() {
        new GetPhotoAsyncTask().execute();
    }

    public ArrayList getLocalPhoto(int limit, int start) {
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                sortOrder + limit + " OFFSET " + start
        );
        // have no idea why does not need to specify project/selection

        Log.e(TAG,"[getLocalPhoto] load start= "+start+", limit= "+limit);
        cursor.moveToFirst();
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
        int nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
//                int durationColumn =
//                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
        int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        photoArrayList = new ArrayList<>();
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
        Log.e(TAG,"[getLocalPhoto] photolist size= "+photoArrayList.size());

        cursor.close();
        return photoArrayList;
    }

    class GetPhotoAsyncTask extends AsyncTask<Void, Void, ArrayList> {
        @Override
        protected ArrayList doInBackground(Void... voids) {
            return getLocalPhoto(0, 0);
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            view.updateData(photoArrayList);
        }
    }
}
