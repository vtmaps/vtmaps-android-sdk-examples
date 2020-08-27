package com.example.vtmaps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.viettel.maps.services.GeoObjItem;
import com.viettel.maps.services.GeoService;
import com.viettel.maps.services.GeoServiceResult;
import com.viettel.vtmsdk.MapVT;
import com.viettel.vtmsdk.annotations.Marker;
import com.viettel.vtmsdk.annotations.MarkerOptions;
import com.viettel.vtmsdk.camera.CameraPosition;
import com.viettel.vtmsdk.camera.CameraUpdate;
import com.viettel.vtmsdk.camera.CameraUpdateFactory;
import com.viettel.vtmsdk.geometry.LatLng;
import com.viettel.vtmsdk.geometry.LatLngBounds;
import com.viettel.vtmsdk.maps.MapView;
import com.viettel.vtmsdk.maps.OnMapReadyCallback;
import com.viettel.vtmsdk.maps.Style;
import com.viettel.vtmsdk.maps.VTMap;

import java.util.ArrayList;
import java.util.List;

public class GeoServiceActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private VTMap vtMap;
    private GeoService geoService;
    private Button btnSearch;
    private EditText edtSearch;
    private View lnSearchCondition;
    private CheckBox cbSearchAround;
    private RadioGroup radioGroup;
    private EditText edtRadius;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
        setContentView(R.layout.activity_geo_service);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);

        btnSearch = findViewById(R.id.btnSearch);
        edtSearch = findViewById(R.id.edtSearch);
        edtRadius= findViewById(R.id.edtRadius);
        lnSearchCondition = findViewById(R.id.lnSearchCondition);
        cbSearchAround = findViewById(R.id.cbSearchAround);
        radioGroup = findViewById(R.id.radioGroup);

        cbSearchAround.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lnSearchCondition.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final List<LatLng> listLatLng = new ArrayList<>();
                if (cbSearchAround.isChecked()) {
                    geoService = new GeoService().accessToken(getString(R.string.access_token));
                    geoService.limit(10);
                    geoService.offset(0);
                    geoService.getLocations(edtSearch.getText().toString().trim(), getSearchType(), vtMap.getCameraPosition().target, Integer.valueOf(edtRadius.getText().toString()), new GeoService.GeoServiceListener() {
                        @Override
                        public void onGeoServicePreProcess(GeoService param1GeoService) {

                        }

                        @Override
                        public void onGeoServiceCompleted(GeoServiceResult param1GeoServiceResult) {
                            for (Marker mk : vtMap.getMarkers()) {
                                vtMap.removeMarker(mk);
                            }

                            for (GeoObjItem item : param1GeoServiceResult.getItems()) {
                                MarkerOptions markerOptions = new MarkerOptions().position(item.getLocation())
                                        .title(item.getName()).snippet(item.getAddress());
                                vtMap.addMarker(markerOptions);
                                listLatLng.add(item.getLocation());
                            }

                            if (listLatLng.isEmpty()) {
                                Toast.makeText(GeoServiceActivity.this, "Không tìm thấy kết quả", Toast.LENGTH_LONG).show();
                                return;
                            }

                            LatLngBounds latLngBounds = null;
                            if (listLatLng.size() >= 2) {
                                latLngBounds = new LatLngBounds.Builder().includes(listLatLng).build();
                                CameraPosition position = vtMap.getCameraForLatLngBounds(latLngBounds, new int[]{50, 100, 50, 100});
                                vtMap.setCameraPosition(position);
                            } else {
                                CameraPosition position = new CameraPosition.Builder()
                                        .target(listLatLng.get(0))
                                        .zoom(13)
                                        .build();
                            }
                        }
                    });
                } else {
                    geoService.getLocations(edtSearch.getText().toString().trim(), new GeoService.GeoServiceListener() {
                        @Override
                        public void onGeoServicePreProcess(GeoService param1GeoService) {

                        }

                        @Override
                        public void onGeoServiceCompleted(GeoServiceResult param1GeoServiceResult) {
                            for (Marker mk : vtMap.getMarkers()) {
                                vtMap.removeMarker(mk);
                            }

                            for (GeoObjItem item : param1GeoServiceResult.getItems()) {
                                MarkerOptions markerOptions = new MarkerOptions().position(item.getLocation())
                                        .title(item.getName()).snippet(item.getAddress());
                                vtMap.addMarker(markerOptions);
                                listLatLng.add(item.getLocation());
                            }

                            if (listLatLng.isEmpty()) {
                                Toast.makeText(GeoServiceActivity.this, "Không tìm thấy kết quả", Toast.LENGTH_LONG).show();
                                return;
                            }

                            LatLngBounds latLngBounds = null;
                            if (listLatLng.size() >= 2) {
                                latLngBounds = new LatLngBounds.Builder().includes(listLatLng).build();
                                CameraPosition position = vtMap.getCameraForLatLngBounds(latLngBounds, new int[]{50, 100, 50, 100});
                                vtMap.setCameraPosition(position);
                            } else {
                                CameraPosition position = new CameraPosition.Builder()
                                        .target(listLatLng.get(0))
                                        .zoom(13)
                                        .build();
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    public void onMapReady(@NonNull VTMap VTMap) {
        this.vtMap = VTMap;
        VTMap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {
            public void onStyleLoaded(@NonNull Style style) {

                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(16.04791610056455, 108.21643351855755))
                        .zoom(10)
                        .build();

                vtMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 2000);
                vtMap.setInfoWindowAdapter(new VTMap.InfoWindowAdapter() {
                    @Nullable
                    @Override
                    public View getInfoWindow(@NonNull Marker marker) {
                        return null;
                    }
                });

                vtMap.addOnMapClickListener(new VTMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull final LatLng point) {
                        new GeoService().accessToken(getString(R.string.access_token))
                        .limit(10)
                        .offset(0)
                        .getAddress(point, new GeoService.GeoServiceListener() {
                            @Override
                            public void onGeoServicePreProcess(GeoService param1GeoService) {

                            }

                            @Override
                            public void onGeoServiceCompleted(GeoServiceResult param1GeoServiceResult) {
                                if (!param1GeoServiceResult.getItems().isEmpty()) {
                                    for (Marker mk : vtMap.getMarkers()) {
                                        vtMap.removeMarker(mk);
                                    }
                                    MarkerOptions markerOptions = new MarkerOptions().position(point)
                                            .snippet(param1GeoServiceResult.getItem(0).getAddress());

                                    vtMap.addMarker(markerOptions);
                                    Toast.makeText(GeoServiceActivity.this, param1GeoServiceResult.getItem(0).getAddress(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        //get addresses of multi point
                        List<LatLng> listLatLng = new ArrayList<>();
                        listLatLng.add(new LatLng(16.04791610056455, 108.21643351855755));
                        listLatLng.add(new LatLng(21.044844,105.852367));

                        new GeoService().accessToken(getString(R.string.access_token))
                        .getAddresses(listLatLng, new GeoService.GeoServiceListener() {
                            @Override
                            public void onGeoServicePreProcess(GeoService param1GeoService) {

                            }

                            @Override
                            public void onGeoServiceCompleted(GeoServiceResult param1GeoServiceResult) {

                            }
                        });

                        return true;
                    }
                });
            }
        });
    }

    private int getSearchType() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rdAccommodationt_13:
                return 13;
            case R.id.rdAutomobile_15:
                return 15;
            case R.id.rdFinancy_21:
                return 21;
            case R.id.rdMedicine_23:
                return 23;
            case R.id.rdRestaurant_25:
                return 25;
            default:
                return 0;
        }
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
