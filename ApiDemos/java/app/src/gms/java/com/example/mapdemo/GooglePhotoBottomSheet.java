package com.example.mapdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class GooglePhotoBottomSheet extends AppCompatActivity {
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private View bottom_sheet;
    private ImageView iv;
    private String TAG = "GooglePhotoBottomSheet";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photoview_layout);
        setupBottomSheet();
        iv = findViewById(R.id.photo_view);
    }

    private void setupBottomSheet() {
        bottom_sheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.e(TAG, "[onSlide] slideOffset= "+slideOffset);
                if (slideOffset > 0) {
                    iv.animate().y((1 - slideOffset) * 1280 - 500);
                } else {
                    iv.animate().y(640);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_bottom_sheet:
                bottomSheetBehavior.setPeekHeight(600);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
