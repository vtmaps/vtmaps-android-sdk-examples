package com.example.vtmaps;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.ui.v5.camera.CameraUpdateMode;
import com.mapbox.services.android.navigation.ui.v5.camera.NavigationCameraUpdate;
import com.mapbox.services.android.navigation.ui.v5.map.NavigationMapboxMap;
import com.mapbox.services.android.navigation.v5.navigation.MapboxOfflineRouter;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.navigation.OfflineError;
import com.mapbox.services.android.navigation.v5.navigation.OfflineRoute;
import com.mapbox.services.android.navigation.v5.navigation.OnOfflineRouteFoundCallback;
import com.mapbox.services.android.navigation.v5.navigation.OnOfflineTilesConfiguredCallback;
import com.viettel.vtmsdk.MapVT;
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
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RoutingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private VTMap vtMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Mapbox.getInstance(getApplicationContext(), "pk.eyJ1IjoibGVwcHJvOTAiLCJhIjoiY2s4cG94N2hpMDU4MTNlcGdvY2gyamQwayJ9.LrwJ0gNZ6ncU7yLeaHKxAQ");
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRouteOffline();
            }
        });

        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull VTMap VTMap) {
        this.vtMap=VTMap;
        vtMap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {

            public void onStyleLoaded(@NonNull Style style) {
                vtMap.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(16.04791610056455, 108.21643351855755))
                        .zoom(12)
                        .build());
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                final NavigationMapboxMap navigationMapboxMap = new NavigationMapboxMap(mapView, vtMap);

                NavigationRoute.Builder builder = NavigationRoute.builder(RoutingActivity.this)
                        .accessToken(MapVT.getAccessToken() != null ? MapVT.getAccessToken() : "")
                        .profile(DirectionsCriteria.PROFILE_DRIVING)//driving: cho oto va cycling: cho xe may
                        .alternatives(false)//cho phep hien thi tuyen duong goi y hay khong(trong truong hop tim thay 2 tuyen duong tro len)
                        .baseUrl("https://api.viettelmaps.com.vn:8080/gateway/routing/v1/")//set url server test
                        .packageId(MapVT.getPackageName());


                builder.origin(Point.fromLngLat(108.21059703174132, 16.066062180258257));
                builder.destination(Point.fromLngLat(108.21643351855755, 16.04791610056455));

                NavigationRoute navigationRoute = builder.build();
                navigationRoute.getRoute(new SimplifiedCallback() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        List<DirectionsRoute> routesFetched = response.body().routes();
                        navigationMapboxMap.drawRoutes(routesFetched);
                        navigationMapboxMap.addDestinationMarker(routesFetched.get(0).legs().get(routesFetched.get(0).legs().size() - 1).steps().get(routesFetched.get(0).legs().get(routesFetched.get(0).legs().size() - 1).steps().size() - 1).maneuver().location());
                        boundCameraToRoute(routesFetched.get(0), navigationMapboxMap);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        super.onFailure(call, throwable);

                    }
                });

            }
        });
    }

    public void boundCameraToRoute(DirectionsRoute route, NavigationMapboxMap map) {
        if (route != null) {
            List<Point> routeCoords = LineString.fromPolyline(route.geometry(),
                    Constants.PRECISION_6).coordinates();
            List<LatLng> bboxPoints = new ArrayList<>();
            for (Point point : routeCoords) {
                bboxPoints.add(new LatLng(point.latitude(), point.longitude()));
            }
            if (bboxPoints.size() > 1) {
                try {
                    LatLngBounds bounds = new LatLngBounds.Builder().includes(bboxPoints).build();
                    // left, top, right, bottom
                    int topPadding = 100;//directionFrame.getHeight() * 2;

                    CameraPosition position = map.retrieveMap().getCameraForLatLngBounds(bounds, new int[]{50, topPadding, 50, 100});
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(position);
                    NavigationCameraUpdate navigationCameraUpdate = new NavigationCameraUpdate(cameraUpdate);
                    navigationCameraUpdate.setMode(CameraUpdateMode.OVERRIDE);
                    map.retrieveCamera().update(navigationCameraUpdate, 1000);

                } catch (Exception exception) {

                }
            }
        }
    }

    private void fetchRouteOffline() {
        String offlinePath = Environment.getExternalStoragePublicDirectory("Offline").getAbsolutePath();
        final MapboxOfflineRouter offlineRouter = new MapboxOfflineRouter(offlinePath);


        /*OfflineTiles builder = OfflineTiles.builder()
                .baseUrl("https://files.viettelmaps.com.vn:8080/")
                .accessToken("123")
                .version("2020_02_02-03_00_00")
                .boundingBox(BoundingBox.fromLngLats(-7.778, 61.359, -6.102, 62.433))
                .build();

        offlineRouter.downloadTiles(builder, new RouteTileDownloadListener() {

            @Override
            public void onError(@NonNull OfflineError error) {
            }

            @Override
            public void onProgressUpdate(int percent) {
                Log.d("Downloading RoutingData", percent + "%");
            }

            @Override
            public void onCompletion() {
                Log.d("Download", " Complete");
            }
        });


        // String e = Environment.getExternalStoragePublicDirectory("Offline
        // ").getAbsolutePath();

       /* File f =new File(offlinePath+"/0/hh_2po.gph");
        if(!f.exists()){
            return;
        }*/
        offlineRouter.configure("2020_02_02-03_00_00", new OnOfflineTilesConfiguredCallback() {

            @Override
            public void onConfigured(int numberOfTiles) {
// Fetch offline route

                NavigationRoute.Builder onlineRouteBuilder = NavigationRoute.builder(RoutingActivity.this)
                        .origin(Point.fromLngLat(Double.parseDouble("108.165978"), Double.parseDouble("16.058063")))
                        .destination(Point.fromLngLat(Double.parseDouble("108.174966"), Double.parseDouble("16.059251")))
//                        .origin(Point.fromLngLat(Double.parseDouble("-6.993056"),Double.parseDouble("62.2191588") ), 30.0, 90.0)
//                        .destination(Point.fromLngLat(Double.parseDouble("-6.921777"),Double.parseDouble("62.071186") ))
                        //.baseUrl("https://api.mapbox.com").profile("walking")
                        .accessToken("pk.eyJ1IjoibGVwcHJvOTAiLCJhIjoiY2s4cG94N2hpMDU4MTNlcGdvY2gyamQwayJ9.LrwJ0gNZ6ncU7yLeaHKxAQ");//;//

                OfflineRoute offlineRoute = OfflineRoute.builder(onlineRouteBuilder).build();
                final Long start = Calendar.getInstance().getTimeInMillis();
                offlineRouter.findRoute(offlineRoute, new OnOfflineRouteFoundCallback() {
                    @Override
                    public void onRouteFound(@NonNull DirectionsRoute route) {
                        // navigationMapboxMap.removeRoute();
                        //navigationMapboxMap.drawRoute(route);
                        //boundCameraToRoute(route);
                        Toast.makeText(RoutingActivity.this, "Routing found in " + (Calendar.getInstance().getTimeInMillis() - start), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(@NonNull OfflineError error) {
                        //navigationMapboxMap.removeRoute();
                        Log.d("Error", error.getMessage());
                        Toast.makeText(RoutingActivity.this, "Routing error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onConfigurationError(@NonNull OfflineError error) {
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
