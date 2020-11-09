// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


package com.example.mapdemo;

import com.example.mapdemo.oauth.GoogleLogin;
import com.example.photo.adapter.PhotoAdapter;
import com.example.photo.model.PhotoModel;
import com.example.photo.presenter.PhotoPresenter;
import com.example.photo.view.IGetPhotoView;
import com.example.photo.view.PhotoView;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * This shows how to close the info window when the currently selected marker is re-tapped.
 */
public class MarkerCloseInfoWindowOnRetapDemoActivity extends AppCompatActivity implements
        OnMarkerClickListener,
        OnMapClickListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener,
        IGetPhotoView {

    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
    private static final String TAG = "MarkerCloseInfoWindowOnRetapDemoActivity";

    private GoogleMap mMap = null;

    private BottomSheetBehavior<View> bottomSheetBehavior;
    private int contentHeight;
    GoogleSignInAccount account;

    public static Credential Credential;

    private PhotoAdapter photoAdapter;
    private PhotoPresenter photoPresenter;

    /**
     * Keeps track of the selected marker.
     */
    private Marker mSelectedMarker;
    private String stateToString(int state) {
        switch (state) {
            case BottomSheetBehavior.STATE_DRAGGING:
                return "STATE_DRAGGING";
            case BottomSheetBehavior.STATE_SETTLING:
                return "STATE_SETTLING";
            case BottomSheetBehavior.STATE_EXPANDED:
                return "STATE_EXPANDED";
            case BottomSheetBehavior.STATE_COLLAPSED:
                return "STATE_COLLAPSED";
            case BottomSheetBehavior.STATE_HIDDEN:
                return "STATE_HIDDEN";
            case BottomSheetBehavior.STATE_HALF_EXPANDED:
                return "STATE_HALF_EXPANDED";
        }
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_close_info_window_on_retap_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);

        setupBottomSheet();

        photoPresenter = new PhotoPresenter(this);
        photoAdapter = new PhotoAdapter(this, photoPresenter);
        setRecyclerView();

        if(checkPermission()){
            photoPresenter.getPhotoList();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        Log.e(TAG,"[onStart] account null? "+(account==null));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signin:
                /*if (account==null) {
                    startActivityForResult(new Intent(this, GoogleLogin.class), 0);
                } else {
                    getPhotoInstance();
                }
                */
                startActivity(new Intent(this, PhotoView.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
//            getPhotoInstance();
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        3
                );
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        3
                );
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 3:
                if(permissions.length > 1 &&
                        permissions[0].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        grantResults.length > 1 &&
                        grantResults[0] != -1) {
                    photoPresenter.getPhotoList();
                }
                break;
        }
    }

    private void  getPhotoInstance() {
        GetPhoto getPhoto = GetPhoto.getInstance();
        getPhoto.setContext(this);
        FutureTask futureTask = getFutureTask(getPhoto);
        while (!futureTask.isDone()) {}
        getPhoto.getPhoto();
    }

    private FutureTask<Void> getFutureTask(GetPhoto getPhoto) {

        FutureTask<Void> futureTask = new FutureTask<Void>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                getPhoto.init();
                return null;
            }
        });
        Thread thread = new Thread(futureTask);
        thread.start();

        return futureTask;
    }

    private void setupBottomSheet() {
        View bottom_sheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.e(TAG,"[onStateChanged] newState= "+stateToString(newState));
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        getBottomSheetContentHeight(bottom_sheet);
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(photoAdapter);
    }

    private void getBottomSheetContentHeight(View view) {
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                contentHeight = view.getHeight();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Hide the zoom controls.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Add lots of markers to the map.
        addMarkersToMap();

        // Set listener for marker click event.  See the bottom of this class for its behavior.
        mMap.setOnMarkerClickListener(this);

        // Set listener for map click event.  See the bottom of this class for its behavior.
        mMap.setOnMapClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localized.
        map.setContentDescription("Demo showing how to close the info window when the currently"
            + " selected marker is re-tapped.");

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(PERTH)
                .include(SYDNEY)
                .include(ADELAIDE)
                .include(BRISBANE)
                .include(MELBOURNE)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));


    }

    private void addMarkersToMap() {
       mMap.addMarker(new MarkerOptions()
                .position(BRISBANE)
                .title("Brisbane")
                .snippet("Population: 2,074,200")
               .icon(BitmapDescriptorFactory.fromResource(R.drawable.badge_nsw)));

        mMap.addMarker(new MarkerOptions()
                .position(SYDNEY)
                .title("Sydney")
                .snippet("Population: 4,627,300")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.badge_nt)));

        mMap.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.badge_qld)));

        mMap.addMarker(new MarkerOptions()
                .position(PERTH)
                .title("Perth")
                .snippet("Population: 1,738,800")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.badge_sa)));

        mMap.addMarker(new MarkerOptions()
                .position(ADELAIDE)
                .title("Adelaide")
                .snippet("Population: 1,213,000")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.badge_victoria)));

    }

    @Override
    public void onMapClick(final LatLng point) {
        // Any showing info window closes when the map is clicked.
        // Clear the currently selected marker.
        mSelectedMarker = null;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // The user has re-tapped on the marker which was already showing an info window.
        if (marker.equals(mSelectedMarker)) {
            // The showing info window has already been closed - that's the first thing to happen
            // when any marker is clicked.
            // Return true to indicate we have consumed the event and that we do not want the
            // the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            mSelectedMarker = null;
            return true;
        }

        // FIXME: this value should be dynamic
        bottomSheetBehavior.setPeekHeight(600);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mSelectedMarker = marker;

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }

    @Override
    public void updateData(ArrayList<PhotoModel> photoArrayList) {
        photoAdapter.updateData(photoArrayList);
    }
}
