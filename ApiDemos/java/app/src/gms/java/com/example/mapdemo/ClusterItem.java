package com.example.mapdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public interface ClusterItem {
    /**
     * The position of this marker. This must always return the same value.
     */
    @NonNull
    LatLng getPosition();

    /**
     * The title of this marker.
     */
    @Nullable String getTitle();

    /**
     * The description of this marker.
     */
    @Nullable
    String getSnippet();
}
