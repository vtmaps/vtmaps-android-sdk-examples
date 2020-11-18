package com.example.vtmaps;

import android.os.Bundle;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.viettel.vtmsdk.MapVT;
import com.viettel.vtmsdk.geometry.LatLng;
import com.viettel.vtmsdk.location.LocationComponent;
import com.viettel.vtmsdk.location.LocationComponentActivationOptions;
import com.viettel.vtmsdk.location.modes.CameraMode;
import com.viettel.vtmsdk.location.modes.RenderMode;
import com.viettel.vtmsdk.maps.MapView;
import com.viettel.vtmsdk.maps.OnMapReadyCallback;
import com.viettel.vtmsdk.maps.Style;
import com.viettel.vtmsdk.maps.VTMap;

import java.util.List;

import timber.log.Timber;

public class PlacePickerActivity extends AppCompatActivity implements OnMapReadyCallback,
        VTMap.OnCameraMoveStartedListener, VTMap.OnCameraIdleListener,
        PermissionsListener {

    private PermissionsManager permissionsManager;
    CarmenFeature carmenFeature;
    private ImageView markerImage;
    private VTMap vtMap;
    private String accessToken;
    private MapView mapView;
    private FloatingActionButton userLocationButton;
    private boolean includeReverseGeocode;
    boolean initMarker = true;
    public boolean isOpenGPS = false;
    public String mapStyle = Style.TRAFFIC_DAY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_place_picker);

        bindViews();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }


    private void bindViews() {
        mapView = findViewById(R.id.mapView);
        markerImage = findViewById(R.id.image_view_marker);
    }

    private void bindListeners() {
        PlacePickerActivity.this.vtMap.addOnCameraMoveStartedListener(PlacePickerActivity.this);
        PlacePickerActivity.this.vtMap.addOnCameraIdleListener(PlacePickerActivity.this);
    }

    @Override
    public void onMapReady(final VTMap vtMap) {
        this.vtMap = vtMap;
        vtMap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(Style style) {
                bindListeners();
                enableLocationComponent(style);
            }
        });

    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = vtMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.NONE);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.NORMAL);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            vtMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(Style style) {
                    enableLocationComponent(style);
                }
            });
        }
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        Timber.v("Map camera has begun moving.");
        if (markerImage.getTranslationY() == 0) {
            markerImage.animate().translationY(-75)
                    .setInterpolator(new OvershootInterpolator()).setDuration(250).start();
        }
    }

    @Override
    public void onCameraIdle() {
        Timber.v("Map camera is now idling.");
        markerImage.animate().translationY(0)
                .setInterpolator(new OvershootInterpolator()).setDuration(250).start();
        LatLng latLng = vtMap.getCameraPosition().target;
        Toast.makeText(this, String.format("Location at %s,%s", latLng.getLatitude(), latLng.getLongitude()), Toast.LENGTH_SHORT).show();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
