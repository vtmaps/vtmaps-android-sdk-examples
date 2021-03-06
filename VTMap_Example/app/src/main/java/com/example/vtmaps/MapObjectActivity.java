package com.example.vtmaps;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.plugins.annotation.Circle;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.plugins.annotation.Fill;
import com.mapbox.mapboxsdk.plugins.annotation.FillManager;
import com.mapbox.mapboxsdk.plugins.annotation.FillOptions;
import com.mapbox.mapboxsdk.plugins.annotation.Line;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.plugins.annotation.OnCircleClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.OnFillClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.OnLineClickListener;
import com.mapbox.turf.TurfTransformation;
import com.viettel.vtmsdk.MapVT;
import com.viettel.vtmsdk.annotations.Marker;
import com.viettel.vtmsdk.annotations.MarkerOptions;
import com.viettel.vtmsdk.camera.CameraPosition;
import com.viettel.vtmsdk.geometry.LatLng;
import com.viettel.vtmsdk.maps.MapView;
import com.viettel.vtmsdk.maps.OnMapReadyCallback;
import com.viettel.vtmsdk.maps.Style;
import com.viettel.vtmsdk.maps.VTMap;
import com.viettel.vtmsdk.style.layers.Layer;
import com.viettel.vtmsdk.style.layers.Property;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.turf.TurfConstants.UNIT_METERS;

public class MapObjectActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private VTMap vtMap;
    private FillManager fillManager;
    private CircleManager circleManager;
    private LineManager lineManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
        setContentView(R.layout.activity_map_object);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull VTMap VTMap) {
        this.vtMap = VTMap;
        VTMap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {
            public void onStyleLoaded(@NonNull Style style) {
                //set zoom and center
                vtMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(21.030360474622, 105.84781770108))
                        .zoom(14)
                        .build());
                drawPolygon(style);
                drawCircle(style);
                drawPolyLine(style);
                drawCircleWithPhysicalUnit(style);
            }
        });
    }

    private void drawPolygon(Style style) {

        final List<List<LatLng>> latLngListOne = new ArrayList<>();
        latLngListOne.add(new ArrayList<LatLng>() {{
            add(new LatLng(21.030360474622, 105.84781770108));
            add(new LatLng(21.027034535443, 105.84777478574));
            add(new LatLng(21.025918736493, 105.85187320112));
        }});


        final List<List<List<LatLng>>> latLngList = new ArrayList<List<List<LatLng>>>() {{
            add(latLngListOne);
        }};

        fillManager = new FillManager(mapView, vtMap, style);

        List<FillOptions> options = new ArrayList<>();
        for (List<List<LatLng>> lists : latLngList) {
            options.add(new FillOptions().withLatLngs(lists).withFillColor("red"));
        }
        fillManager.create(options);
        fillManager.addClickListener(new OnFillClickListener() {
            @Override
            public void onAnnotationClick(Fill fill) {
                Toast.makeText(MapObjectActivity.this, "Polygon clicked", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void drawCircleWithPhysicalUnit(Style style) {
        FillManager fillManager = new FillManager(mapView, vtMap, style);
        Polygon polygon = getTurfPolygon(Point.fromLngLat(105.84781770108, 21.030360474622), 1000, 360, UNIT_METERS);
        FillOptions option = new FillOptions().withGeometry(polygon).withFillColor("blue").withFillOpacity(0.5f);
        fillManager.create(option);
    }

    private Polygon getTurfPolygon(@NonNull Point centerPoint, @NonNull double radius,
                                   @NonNull int steps, @NonNull String units) {
        return TurfTransformation.circle(centerPoint, radius, steps, units);
    }

    private void drawCircle(Style style) {
        circleManager = new CircleManager(mapView, vtMap, style);
        final List<LatLng> listPolygonLaLng = new ArrayList<>();
        listPolygonLaLng.add(new LatLng(21.030360474622, 105.84781770108));
        listPolygonLaLng.add(new LatLng(21.027034535443, 105.84777478574));

        CircleOptions option = new CircleOptions().withLatLng(new LatLng(21.028558030164, 105.85386876462)).withCircleColor("yellow").
                withCircleRadius(50f).withCircleStrokeWidth(2f).withCircleStrokeColor("red").withCircleOpacity(0.6f);

        circleManager.create(option);
        circleManager.addClickListener(new OnCircleClickListener() {
            @Override
            public void onAnnotationClick(Circle circle) {
                Toast.makeText(MapObjectActivity.this, "Circle clicked", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void drawPolyLine(Style style) {
        lineManager = new LineManager(mapView, vtMap, style,"com.mapbox.annotations.points");
        final List<LatLng> listPolygonLaLng = new ArrayList<>();
        listPolygonLaLng.add(new LatLng(21.03352862410621, 105.84525978605035));
        listPolygonLaLng.add(new LatLng(21.028309714074638, 105.84475905989018));
        listPolygonLaLng.add(new LatLng(21.032095439236514, 105.84270608267445));

        LineOptions option = new LineOptions().withLatLngs(listPolygonLaLng).withLineColor("orange").withLineJoin(Property.LINE_JOIN_ROUND).withLineWidth(5f);

        lineManager.create(option);
        lineManager.addClickListener(new OnLineClickListener() {
            @Override
            public void onAnnotationClick(Line line) {
                Toast.makeText(MapObjectActivity.this, "Line clicked", Toast.LENGTH_LONG).show();
            }
        });

        List<Layer> styles = style.getLayers();
        String id =   styles.get(styles.size()-1).getId();
        for(Layer id1: styles){
            Log.d("LEP",id1.getId());
        }
        MarkerOptions option1 = new MarkerOptions();
        option1.setPosition(new LatLng(16.04791610056455, 108.21643351855755));
        option1.setTitle("Marker");
        option1.setSnippet("This is content");
        Marker simpleMarker = vtMap.addMarker(option1);
        List<Layer> styles1 = style.getLayers();
        String id1 =   styles1.get(styles1.size()-1).getId();

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
