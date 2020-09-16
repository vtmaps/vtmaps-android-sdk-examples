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

public class MapTypeControlActivity extends AppCompatActivity  implements OnMapReadyCallback {
    private MapView mapView;
    private VTMap vtMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
        setContentView(R.layout.activity_map_type_control);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull VTMap vtMap) {
        this.vtMap = vtMap;
        vtMap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {
            public void onStyleLoaded(@NonNull Style style) {

                MapTypeControl mapTypeControl = new MapTypeControl(mapView, MapTypeControlActivity.this.vtMap);
                mapTypeControl.addToMap(MapTypeControlActivity.this);
                mapTypeControl.setOnMapTypeChangedListener(new MapTypeControl.OnMapTypeChangedListener() {
                    @Override
                    public void onMapTypeChanged(Style style) {
                        Toast.makeText(MapTypeControlActivity.this, "style changed to: " + style.getUri(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}
