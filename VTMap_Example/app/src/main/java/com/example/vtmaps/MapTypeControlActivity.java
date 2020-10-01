package com.example.vtmaps;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.viettel.maps.v3.control.maptype.LocationControl;
import com.viettel.maps.v3.control.maptype.MapTypeControl;
import com.viettel.vtmsdk.MapVT;
import com.viettel.vtmsdk.camera.CameraPosition;
import com.viettel.vtmsdk.geometry.LatLng;
import com.viettel.vtmsdk.maps.MapView;
import com.viettel.vtmsdk.maps.OnMapReadyCallback;
import com.viettel.vtmsdk.maps.Style;
import com.viettel.vtmsdk.maps.VTMap;

import java.util.List;

public class MapTypeControlActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {
    private MapView mapView;
    private VTMap vtMap;
    private PermissionsManager permissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
        setContentView(R.layout.activity_map_type_control);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);

        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }

    @Override
    public void onMapReady(@NonNull VTMap vtMap) {
        this.vtMap = vtMap;
        vtMap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {
            public void onStyleLoaded(@NonNull Style style) {
                MapTypeControlActivity.this.vtMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(16.04791610056455, 108.21643351855755))
                        .zoom(10)
                        .build());

                MapTypeControl mapTypeControl = new MapTypeControl(mapView, MapTypeControlActivity.this.vtMap);
                mapTypeControl.addToMap(MapTypeControlActivity.this);
                mapTypeControl.setOnMapTypeChangedListener(new MapTypeControl.OnMapTypeChangedListener() {
                    @Override
                    public void onMapTypeChanged(Style style) {
                        Toast.makeText(MapTypeControlActivity.this, "style changed to: " + style.getUri(), Toast.LENGTH_SHORT).show();
                    }
                });

                LocationControl locationControl = new LocationControl(mapView, MapTypeControlActivity.this.vtMap);
                locationControl.addToMap(MapTypeControlActivity.this);
                // ---- You can set custom attribute for LocationControl ---
                //locationControl.setCustomSize(140);
                //locationControl.setImageResource(ExampleActivity.this.getResources().getDrawable(R.drawable.ic_location));
                //locationControl.setRippleColor(Color.RED);
                locationControl.setZoom(18);
                locationControl.setOnLocationClickListener(new LocationControl.OnLocationClickListener() {
                    @Override
                    public void onLocationClick() {
                        Toast.makeText(MapTypeControlActivity.this, "Location Control click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLocationNotFound() {
                        Toast.makeText(MapTypeControlActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
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
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }
}
