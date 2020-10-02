package com.example.vtmaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.viettel.vtmsdk.MapVT;
import com.viettel.vtmsdk.annotations.Icon;
import com.viettel.vtmsdk.annotations.IconFactory;
import com.viettel.vtmsdk.annotations.Marker;
import com.viettel.vtmsdk.annotations.MarkerOptions;
import com.viettel.vtmsdk.camera.CameraPosition;
import com.viettel.vtmsdk.geometry.LatLng;
import com.viettel.vtmsdk.maps.MapView;
import com.viettel.vtmsdk.maps.OnMapReadyCallback;
import com.viettel.vtmsdk.maps.Style;
import com.viettel.vtmsdk.maps.VTMap;
import com.viettel.vtmsdk.style.layers.RasterLayer;
import com.viettel.vtmsdk.style.sources.RasterSource;
import com.viettel.vtmsdk.style.sources.TileSet;

public class AddMakerActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private VTMap vtMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
        setContentView(R.layout.activity_add_maker);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull VTMap VTMap) {
        this.vtMap = VTMap;
        VTMap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {
            public void onStyleLoaded(@NonNull Style style) {
                //set onClick for map
                vtMap.addOnMapClickListener(new VTMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {

                        Toast.makeText(AddMakerActivity.this, String.format("User clicked at: %s", point.toString()), Toast.LENGTH_LONG).show();

                        return true;
                    }
                });

                //set onClick for map
                vtMap.addOnMapLongClickListener(new VTMap.OnMapLongClickListener() {
                    @Override
                    public boolean onMapLongClick(@NonNull LatLng point) {

                        Toast.makeText(AddMakerActivity.this, String.format("User long clicked at: %s", point.toString()), Toast.LENGTH_LONG).show();

                        return true;
                    }
                });

                //set zoom and center
                vtMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(16.04791610056455, 108.21643351855755))
                        .zoom(10)
                        .build());

                vtMap.addOnCameraMoveListener(new VTMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        Log.d("VTMap", " onCameraMove");
                    }
                });

                vtMap.addOnCameraIdleListener(new VTMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        Log.d("VTMap", " onCameraIdle");
                    }
                });
                mapView.addOnCameraDidChangeListener(new MapView.OnCameraDidChangeListener() {
                    @Override
                    public void onCameraDidChange(boolean animated) {
                        Log.d("VTMap", " onCameraDidChange");
                    }
                });

                addSimpleMarker();
                addCustomMarker();

                vtMap.setOnMarkerClickListener(new VTMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        if (marker.getTitle() == null || marker.getTitle().isEmpty()) {
                             return true;
                        }
                        return false;
                    }
                });

                vtMap.setInfoWindowAdapter(new VTMap.InfoWindowAdapter() {
                    @Nullable
                    @Override
                    public View getInfoWindow(@NonNull Marker marker) {

                        if (marker.getId() == 1) {
                            View v = AddMakerActivity.this.getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                            TextView tvTitle = v.findViewById(R.id.tvTitle);
                            TextView tvContent = v.findViewById(R.id.tvContent);
//
                            tvTitle.setText(marker.getTitle());
                            tvContent.setText(marker.getSnippet());
                            return v;
                        }
                        return null;
                    }
                });


            }
        });
    }

    private void addSimpleMarker() {
        MarkerOptions option = new MarkerOptions();
        option.setPosition(new LatLng(16.04791610056455, 108.21643351855755));
        option.setTitle("Marker");
        option.setSnippet("This is content");
        Marker simpleMarker = vtMap.addMarker(option);

    }

    private void addCustomMarker() {
        IconFactory iconFactory = IconFactory.getInstance(this);
        Icon icon = iconFactory.fromResource(R.drawable.marker);
        MarkerOptions option = new MarkerOptions();
        option.setTitle("");
        option.setSnippet("1");
        option.setPosition(new LatLng(16.05791610056455, 108.23643351855755));
        option.setIcon(icon);
        Marker customMarker = vtMap.addMarker(option);

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
}
