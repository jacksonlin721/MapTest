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

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * This shows how to close the info window when the currently selected marker is re-tapped.
 */
public class MarkerCloseInfoWindowOnRetapDemoActivity extends AppCompatActivity implements
        OnMarkerClickListener,
        OnMapClickListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);

    private GoogleMap mMap = null;

    private BottomSheetBehavior<View> bottomSheetBehavior;

    /**
     * Keeps track of the selected marker.
     */
    private Marker mSelectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_close_info_window_on_retap_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);

        setupBottomSheet();
    }

    private void setupBottomSheet() {
        View bottom_sheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

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

        bottomSheetBehavior.setPeekHeight(80);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        mSelectedMarker = marker;

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }
}