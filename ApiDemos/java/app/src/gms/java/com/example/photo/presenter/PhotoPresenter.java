package com.example.photo.presenter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.work.CoroutineWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.photo.model.PhotoModel;
import com.example.photo.view.IGetPhotoView;
import com.example.photo.view.PhotoView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import kotlin.coroutines.Continuation;

public class PhotoPresenter implements GetPhoto {
    Context mContext;
    ArrayList<PhotoModel> photoArrayList;
    ArrayList<ExifInterface> photoExifArrayList;
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
//        new GetPhotoAsyncTask().execute();
        OneTimeWorkRequest requestGetPhoto = OneTimeWorkRequest.from(GetPhotoWork.class);
        OneTimeWorkRequest requestGetPhotoExif = OneTimeWorkRequest.from(GetPhotoExifWork.class);
        OneTimeWorkRequest requestUpdatePhoto = OneTimeWorkRequest.from(UpdatePhotoWork.class);
        WorkManager.getInstance(mContext)
                .beginWith(requestGetPhoto)
                .then(requestGetPhotoExif)
                .then(requestUpdatePhoto)
                .enqueue();
    }

    public ArrayList getLocalPhoto(int limit, int start) {
        Cursor cursor;
        if (limit == PhotoView.ALL_PHOTO &&
                start == PhotoView.ALL_PHOTO) {
            cursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
        } else {
            cursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    sortOrder + limit + " OFFSET " + start
            );
        }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<ExifInterface> getPhotoExif() {
        photoExifArrayList = new ArrayList<>();
        for (int index=0; index < photoArrayList.size(); index++) {
            try {
                InputStream inputStream =
                        mContext.getContentResolver().openInputStream(photoArrayList.get(index).getUri());
                photoExifArrayList.add(new ExifInterface(inputStream));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return photoExifArrayList;
    }

    class GetPhotoAsyncTask extends AsyncTask<Void, Void, ArrayList> {
        @Override
        protected ArrayList doInBackground(Void... voids) {
            return getLocalPhoto(PhotoView.ALL_PHOTO, PhotoView.ALL_PHOTO);
        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            view.updateData(photoArrayList);
        }
    }

    public class GetPhotoWork extends CoroutineWorker {

        public GetPhotoWork(@NotNull Context appContext, @NotNull WorkerParameters params) {
            super(appContext, params);
        }

        @Nullable
        @Override
        public Object doWork(@NotNull Continuation<? super Result> continuation) {
            Log.e(TAG,"[GetPhotoWork] doWork");
            getLocalPhoto(PhotoView.ALL_PHOTO, PhotoView.ALL_PHOTO);
            return Result.success();
        }
    }

    public class GetPhotoExifWork extends CoroutineWorker {

        public GetPhotoExifWork(@NotNull Context appContext, @NotNull WorkerParameters params) {
            super(appContext, params);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Nullable
        @Override
        public Object doWork(@NotNull Continuation<? super Result> continuation) {
            Log.e(TAG,"[GetPhotoExifWork] doWork");
            getPhotoExif();
            return Result.success();
        }
    }

    public class UpdatePhotoWork extends CoroutineWorker {

        public UpdatePhotoWork(@NotNull Context appContext, @NotNull WorkerParameters params) {
            super(appContext, params);
        }

        @Nullable
        @Override
        public Object doWork(@NotNull Continuation<? super Result> continuation) {
            Log.e(TAG,"[UpdatePhotoWork] doWork");
            view.updateData(photoArrayList);
            return Result.success();
        }
    }
}
