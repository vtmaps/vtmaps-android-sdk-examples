package com.example.vtmaps;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.viettel.vtmsdk.MapVT;
import com.viettel.vtmsdk.maps.MapView;
import com.viettel.vtmsdk.maps.OnMapReadyCallback;
import com.viettel.vtmsdk.maps.Style;
import com.viettel.vtmsdk.maps.VTMap;
import com.viettel.vtmsdk.style.layers.FillLayer;

import static com.viettel.vtmsdk.style.layers.PropertyFactory.fillColor;

public class CustomLayerActivity extends AppCompatActivity {
    private MapView mapView;
    FillLayer water;
    FillLayer building;
    FillLayer tbl_countries;
    FillLayer tbl_vn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapVT.getInstance(this, "6ht5fdbc-1996-4f54-87gf-5664f304f3d2");
        setContentView(R.layout.activity_custom_layer);
        final SeekBar redSeekBar = findViewById(R.id.red_seek_bar);
        final SeekBar greenSeekBar = findViewById(R.id.green_seek_bar);
        final SeekBar blueSeekBar = findViewById(R.id.blue_seek_bar);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
            @Override
            public void onProviderInstalled() {
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull VTMap vtmap) {
                        vtmap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                water = (FillLayer) style.getLayer("tbl_land");
                                building = (FillLayer) style.getLayer("forests");
                                tbl_countries = (FillLayer) style.getLayer("tbl_countries");
                                tbl_vn = (FillLayer) style.getLayer("tbl_vn");
                            }
                        });
                    }
                });
            }

            @Override
            public void onProviderInstallFailed(int errorCode, Intent recoveryIntent) {
                GooglePlayServicesUtil.showErrorNotification(errorCode, CustomLayerActivity.this);
            }
        });


        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (water != null) {
                    water.setProperties(
                            fillColor(Color.rgb(redSeekBar.getProgress(), greenSeekBar.getProgress(), progress))
                    );
                }

                if (building != null) {
                    building.setProperties(
                            fillColor(Color.rgb(progress, redSeekBar.getProgress(), progress))
                    );
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (water != null) {
                    water.setProperties(
                            fillColor(Color.rgb(redSeekBar.getProgress(), progress, blueSeekBar.getProgress()))
                    );
                }
                if (building != null) {
                    building.setProperties(
                            fillColor(Color.rgb(progress, greenSeekBar.getProgress(), redSeekBar.getProgress()))
                    );
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (water != null) {
                    water.setProperties(
                            fillColor(Color.rgb(redSeekBar.getProgress(), greenSeekBar.getProgress(), progress))
                    );
                }
                if (building != null) {
                    building.setProperties(
                            fillColor(Color.rgb(progress, greenSeekBar.getProgress(), blueSeekBar.getProgress()))
                    );
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}
