package com.example.vtmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mapbox.mapboxsdk.plugins.annotation.FillManager;
import com.viettel.maps.services.AdminLevelType;
import com.viettel.maps.services.AdminService;
import com.viettel.maps.services.AdminServiceResult;
import com.viettel.vtmsdk.MapVT;
import com.viettel.vtmsdk.camera.CameraPosition;
import com.viettel.vtmsdk.geometry.LatLng;
import com.viettel.vtmsdk.geometry.LatLngBounds;
import com.viettel.vtmsdk.maps.MapView;
import com.viettel.vtmsdk.maps.OnMapReadyCallback;
import com.viettel.vtmsdk.maps.Style;
import com.viettel.vtmsdk.maps.VTMap;
import com.viettel.vtmsdk.snapshotter.MapSnapshot;
import com.viettel.vtmsdk.snapshotter.MapSnapshotter;

public class SnapshotMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private VTMap vtMap;
    private MapSnapshotter mapSnapshotter;
    private ImageView imageViewMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
        setContentView(R.layout.activity_snapshot_map);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);

        Button imageButtonSnapshot = (Button) findViewById(R.id.imb_snapshot);
        imageViewMap = (ImageView) findViewById(R.id.imv_map);

        //thuc hien chup anh ban do
        imageButtonSnapshot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startSnapShot(
                        vtMap.getProjection().getVisibleRegion().latLngBounds,
                        mapView.getMeasuredHeight(),
                        mapView.getMeasuredWidth());
            }
        });

    }

    @Override
    public void onMapReady(@NonNull VTMap VTMap) {

        this.vtMap = VTMap;
        VTMap.setCustomStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {
            public void onStyleLoaded(@NonNull Style style) {
                //set zoom and center
                vtMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(16.04791610056455, 108.21643351855755))
                        .zoom(10)
                        .build());
            }
        });
    }

    private void startSnapShot(final LatLngBounds latLngBounds,final int height,final int width) {
        vtMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                if (mapSnapshotter == null) {
                    // Initialize snapshotter with map dimensions and given bounds
                    MapSnapshotter.Options options =
                            new MapSnapshotter.Options(width, height)
                                    .withRegion(latLngBounds)
                                    .withCameraPosition(vtMap.getCameraPosition())
                                    .withStyle(style.getUri());

                    mapSnapshotter = new MapSnapshotter(SnapshotMapActivity.this, options);
                } else {
                    // Reuse pre-existing MapSnapshotter instance
                    mapSnapshotter.setSize(width, height);
                    mapSnapshotter.setRegion(latLngBounds);
                    mapSnapshotter.setCameraPosition(vtMap.getCameraPosition());
                }

                mapSnapshotter.start(new MapSnapshotter.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(MapSnapshot snapshot) {

                        Bitmap bitmapOfMapSnapshotImage = snapshot.getBitmap();

                        if (bitmapOfMapSnapshotImage != null) {
                            imageViewMap.setImageBitmap(bitmapOfMapSnapshotImage);
                        }
                    }
                });
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
