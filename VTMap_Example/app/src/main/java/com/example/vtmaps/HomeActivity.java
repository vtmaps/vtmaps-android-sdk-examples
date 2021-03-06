package com.example.vtmaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        View vAddMaker = findViewById(R.id.vAddMaker);
        vAddMaker.setOnClickListener(this);
        View vAddDragMaker = findViewById(R.id.vAddDragMaker);
        vAddDragMaker.setOnClickListener(this);

        View vRouting = findViewById(R.id.vRouting);
        vRouting.setOnClickListener(this);
        View vAdminService = findViewById(R.id.vAdminService);
        vAdminService.setOnClickListener(this);

        View vSnapShotMap = findViewById(R.id.vSnapShotMap);
        vSnapShotMap.setOnClickListener(this);
        View vGeoService = findViewById(R.id.vGeoService);
        vGeoService.setOnClickListener(this);

        View vMapOjects = findViewById(R.id.vMapOjects);
        vMapOjects.setOnClickListener(this);

        View vMapTypeControl = findViewById(R.id.vMapTypeControl);
        vMapTypeControl.setOnClickListener(this);

        View vScaleBarControl = findViewById(R.id.vScalebarControl);
        vScaleBarControl.setOnClickListener(this);

        View vCustomLayer = findViewById(R.id.vCustomLayer);
        vCustomLayer.setOnClickListener(this);

        View vPlacePicker = findViewById(R.id.vPlacePicker);
        vPlacePicker.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vAddMaker:
                Intent intent = new Intent(this, AddMakerActivity.class);
                startActivity(intent);
                break;
            case R.id.vAddDragMaker:
                Intent intent1 = new Intent(this, AddDragMarkerActivity.class);
                startActivity(intent1);
                break;
            case R.id.vRouting:
                Intent intentRouting = new Intent(this, RoutingActivity.class);
                startActivity(intentRouting);
                break;
            case R.id.vAdminService:
                Intent intentAdminService = new Intent(this, AdminServiceActivity.class);
                startActivity(intentAdminService);
                break;
            case R.id.vSnapShotMap:
                Intent vSnapShotMap = new Intent(this, SnapshotMapActivity.class);
                startActivity(vSnapShotMap);
                break;
            case R.id.vGeoService:
                Intent vGeoService = new Intent(this, GeoServiceActivity.class);
                startActivity(vGeoService);
                break;
            case R.id.vMapOjects:
                Intent vMapOjects = new Intent(this, MapObjectActivity.class);
                startActivity(vMapOjects);
                break;
            case R.id.vCustomLayer:
                Intent vCustomLayer = new Intent(this, CustomLayerActivity.class);
                startActivity(vCustomLayer);
                break;
            case R.id.vMapTypeControl:
                Intent vMapTypeControl = new Intent(this, MapTypeControlActivity.class);
                startActivity(vMapTypeControl);
                break;
            case R.id.vScalebarControl:
                Intent vScalebarControl = new Intent(this, ScaleBarControlActivity.class);
                startActivity(vScalebarControl);
                break;
            case R.id.vPlacePicker:
                Intent vPlacePicker = new Intent(this, PlacePickerActivity.class);
                startActivity(vPlacePicker);
                break;
        }
    }
}
