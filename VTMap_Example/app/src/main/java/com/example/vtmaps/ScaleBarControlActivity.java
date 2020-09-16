package com.example.vtmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.viettel.maps.control.maptype.MapTypeControl;
import com.viettel.maps.control.scalebar.ScaleBarOptions;
import com.viettel.maps.control.scalebar.ScaleBarPlugin;
import com.viettel.vtmsdk.MapVT;
import com.viettel.vtmsdk.annotations.MarkerOptions;
import com.viettel.vtmsdk.geometry.LatLng;
import com.viettel.vtmsdk.maps.MapView;
import com.viettel.vtmsdk.maps.OnMapReadyCallback;
import com.viettel.vtmsdk.maps.Style;
import com.viettel.vtmsdk.maps.VTMap;

public class ScaleBarControlActivity extends AppCompatActivity  implements OnMapReadyCallback {
    private MapView mapView;
    private VTMap vtMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
        setContentView(R.layout.activity_scale_bar_control);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull VTMap vtMap) {
        this.vtMap = vtMap;
        vtMap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {
            public void onStyleLoaded(@NonNull Style style) {

                ScaleBarPlugin scaleBarPlugin = new ScaleBarPlugin(mapView, ScaleBarControlActivity.this.vtMap);
                ScaleBarOptions scaleBarOptions = new ScaleBarOptions(ScaleBarControlActivity.this);
                //custom
                scaleBarOptions
                        .setTextColor(R.color.colorAccent)
                        .setTextSize(20f)
                        .setBarHeight(10f)
                        .setBorderWidth(2f)
                        .setRefreshInterval(15)
                        .setMarginTop(15f)
                        .setMarginLeft(16f)
                        .setTextBarMargin(15f);

                scaleBarPlugin.create(scaleBarOptions);

            }
        });
    }
}
