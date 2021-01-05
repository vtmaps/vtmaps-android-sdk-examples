package com.example.vtmaps;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.viettel.maps.util.AppInfo;
import com.viettel.maps.util.MapConfig;
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

public class MapV2Activity extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_v2);
        MapConfig.ENABLE_MAP_LOG = true;
        MapConfig.DEBUG_MODE = false;
        MapConfig.changeSRS(MapConfig.SRSType.SRS_4326);
        //AppInfo.setServerAddress("http","203.190.170.250", 80);
        AppInfo.setServerAddress("http","viettelmaps.vn", 80);
        AppInfo.setAppKey("a682ac669c8884e3697fa1d907e2de8");
        com.viettel.maps.MapView map = (com.viettel.maps.MapView) findViewById(R.id.mapViewVT);
        map.setMapType(MapConfig.MapType.TRANSPORT);
    }

}
