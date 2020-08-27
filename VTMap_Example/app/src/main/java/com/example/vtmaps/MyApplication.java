package com.example.vtmaps;

import android.app.Application;

import com.viettel.vtmsdk.MapVT;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

// Mapbox Access token
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
    }
}