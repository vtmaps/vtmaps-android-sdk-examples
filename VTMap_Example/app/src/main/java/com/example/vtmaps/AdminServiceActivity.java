package com.example.vtmaps;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.plugins.annotation.Fill;
import com.mapbox.mapboxsdk.plugins.annotation.FillManager;
import com.mapbox.mapboxsdk.plugins.annotation.FillOptions;
import com.mapbox.mapboxsdk.plugins.annotation.OnFillClickListener;
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

public class AdminServiceActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private VTMap vtMap;
    private AdminService adminService;
    private FillManager fillManager;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
        setContentView(R.layout.activity_admin_service);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        radioGroup = findViewById(R.id.radioGroup);

        adminService = new AdminService()
                .accessToken(getString(R.string.access_token));


        Toast.makeText(AdminServiceActivity.this, "click trên bản đồ để lấy vị trí cần tìm", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull VTMap VTMap) {
        this.vtMap = VTMap;
        VTMap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {
            public void onStyleLoaded(@NonNull Style style) {
                //set zoom and center
                vtMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(16.04791610056455, 108.21643351855755))
                        .zoom(10)
                        .build());
                fillManager = new FillManager(mapView, vtMap, style);
                vtMap.addOnMapClickListener(new VTMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        adminService.levelType(getLevelType());
                        adminService.getFeature(point, new AdminService.AdminServiceListener() {
                            @Override
                            public void onAdminServiceCompleted(AdminServiceResult adminServiceResult) {
                                showAdminArea(adminServiceResult);
                            }

                            @Override
                            public void onAdminServicePreProcess(AdminService service) {

                            }
                        });
                        return true;
                    }
                });
            }
        });
    }

    private AdminLevelType getLevelType() {
        if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton_xa) {
            return AdminLevelType.COMMUNE;
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton_huyen) {
            return AdminLevelType.DISTRICT;
        } else {
            return AdminLevelType.PROVINCE;
        }
    }

    private void showAdminArea(AdminServiceResult adminServiceResult) {
        fillManager.deleteAll();
        if (adminServiceResult.getItems() != null && !adminServiceResult.getItems().isEmpty() && adminServiceResult.getItems().get(0) != null) {
            Toast.makeText(AdminServiceActivity.this, adminServiceResult.getItems().get(0).getName(), Toast.LENGTH_LONG).show();
            FillOptions fillOptions = new FillOptions()
                    .withLatLngs(adminServiceResult.getItems().get(0).getCoordinates())
                    .withFillColor("red");
            fillManager.create(fillOptions);

            fillManager.addClickListener(new OnFillClickListener() {
                @Override
                public void onAnnotationClick(Fill fill) {
                }
            });
        }
    }

    private void getLocationByCode() {
        AdminService service = new AdminService().accessToken(getString(R.string.access_token));
        service.getFeature("79", new AdminService.AdminServiceListener() {
            @Override
            public void onAdminServiceCompleted(AdminServiceResult param1AdminServiceResult) {
                System.out.println(param1AdminServiceResult.getStatus());
                if (param1AdminServiceResult.getItems() != null && !param1AdminServiceResult.getItems().isEmpty()) {
                    System.out.println(param1AdminServiceResult.getItems().get(0).getName());
                }
            }

            @Override
            public void onAdminServicePreProcess(AdminService service) {

            }
        });
    }

    private void getLocationByCircle() {
        AdminService service = new AdminService().accessToken(getString(R.string.access_token));
        service.getFeature(new LatLng(16.04791610056455, 108.21643351855755),600000, new AdminService.AdminServiceListener() {
            @Override
            public void onAdminServiceCompleted(AdminServiceResult param1AdminServiceResult) {
                System.out.println(param1AdminServiceResult.getStatus());
                if (param1AdminServiceResult.getItems() != null && !param1AdminServiceResult.getItems().isEmpty()) {
                    System.out.println(param1AdminServiceResult.getItems().get(0).getName());
                }
            }

            @Override
            public void onAdminServicePreProcess(AdminService service) {

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
}
