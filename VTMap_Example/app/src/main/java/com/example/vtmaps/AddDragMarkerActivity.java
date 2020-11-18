package com.example.vtmaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolDragListener;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolLongClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.viettel.maps.v3.control.maptype.MapTypeControl;
import com.viettel.vtmsdk.MapVT;
import com.viettel.vtmsdk.camera.CameraPosition;
import com.viettel.vtmsdk.geometry.LatLng;
import com.viettel.vtmsdk.maps.MapView;
import com.viettel.vtmsdk.maps.OnMapReadyCallback;
import com.viettel.vtmsdk.maps.Style;
import com.viettel.vtmsdk.maps.VTMap;
import com.viettel.vtmsdk.style.sources.GeoJsonOptions;
import com.viettel.vtmsdk.utils.BitmapUtils;

public class AddDragMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private VTMap vtMap;
    SymbolManager symbolManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
        setContentView(R.layout.activity_add_drag_marker);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull VTMap VTMap) {
        this.vtMap = VTMap;
        VTMap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {
            public void onStyleLoaded(@NonNull Style style) {
                vtMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(16.04791610056455, 108.21643351855755))
                        .zoom(10)
                        .build());
                style.addImage("ID_ICON",
                        BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.map_marker)),
                        false);



                MapTypeControl mapTypeControl =new MapTypeControl(mapView,vtMap);
                mapTypeControl.addToMap(AddDragMarkerActivity.this);
                mapTypeControl.setOnMapTypeChangedListener(new MapTypeControl.OnMapTypeChangedListener() {
                    @Override
                    public void onMapTypeChanged(Style style) {
                        addDragMarker(style);
                    }
                });

            }
        });
    }

    private void addDragMarker(Style style) {
        GeoJsonOptions geoJsonOptions = new GeoJsonOptions().withTolerance(0.4f);

        symbolManager = new SymbolManager(mapView, vtMap, style, null, geoJsonOptions);
        symbolManager.addClickListener(new OnSymbolClickListener() {
            @Override
            public void onAnnotationClick(Symbol symbol) {
                Toast.makeText(AddDragMarkerActivity.this,
                        String.format("Symbol clicked %s", symbol.getId()),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        symbolManager.addLongClickListener(new OnSymbolLongClickListener() {
            @Override
            public void onAnnotationLongClick(Symbol symbol) {
                Toast.makeText(AddDragMarkerActivity.this,
                        String.format("Symbol long clicked %s", symbol.getId()),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // set non data driven properties
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setTextAllowOverlap(true);

        SymbolOptions symbolOptions = new SymbolOptions()
                .withLatLng(new LatLng(16.04791610056455, 108.21643351855755))
                .withIconImage("ID_ICON")
                //.withTextField("Adsad")
                //.withIconSize(1.3f)
                //.withSymbolSortKey(10.0f)
                //.withData(element)
                .withDraggable(true);
        symbolManager.create(symbolOptions);
        symbolManager.addDragListener(new OnSymbolDragListener() {
            @Override
            public void onAnnotationDragStarted(Symbol annotation) {
            }

            @Override
            public void onAnnotationDrag(Symbol annotation) {

            }

            @Override
            public void onAnnotationDragFinished(Symbol annotation) {
                Toast.makeText(AddDragMarkerActivity.this, String.format(

                        "ID: %s\nLatLng:%f, %f",
                        annotation.getId(),
                        annotation.getLatLng().getLatitude(), annotation.getLatLng().getLongitude()), Toast.LENGTH_LONG).show();
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

        if (symbolManager != null) {
            symbolManager.onDestroy();
        }

        mapView.onDestroy();
    }

}
