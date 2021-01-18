package com.example.vtmaps;

import android.graphics.Color;
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
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
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
import com.viettel.vtmsdk.style.layers.LineLayer;
import com.viettel.vtmsdk.style.layers.Property;
import com.viettel.vtmsdk.style.sources.GeoJsonOptions;
import com.viettel.vtmsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.viettel.vtmsdk.style.expressions.Expression.color;
import static com.viettel.vtmsdk.style.expressions.Expression.exponential;
import static com.viettel.vtmsdk.style.expressions.Expression.get;
import static com.viettel.vtmsdk.style.expressions.Expression.interpolate;
import static com.viettel.vtmsdk.style.expressions.Expression.literal;
import static com.viettel.vtmsdk.style.expressions.Expression.product;
import static com.viettel.vtmsdk.style.expressions.Expression.stop;
import static com.viettel.vtmsdk.style.expressions.Expression.switchCase;
import static com.viettel.vtmsdk.style.expressions.Expression.zoom;
import static com.viettel.vtmsdk.style.layers.PropertyFactory.lineCap;
import static com.viettel.vtmsdk.style.layers.PropertyFactory.lineColor;
import static com.viettel.vtmsdk.style.layers.PropertyFactory.lineJoin;
import static com.viettel.vtmsdk.style.layers.PropertyFactory.lineWidth;

public class RoutingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private VTMap vtMap;
    DirectionsRoute route;
    static final String PRIMARY_ROUTE_PROPERTY_KEY = "primary-route";
    static final String ROUTE_SOURCE_ID = "vtmap-navigation-route-source";
    static final String ROUTE_LAYER_ID = "vtmap-navigation-route-layer";
    static final String ROUTE_SHIELD_LAYER_ID = "vtmap-navigation-route-shield-layer";
    static final String CONGESTION_KEY = "congestion";
    static final String MODERATE_CONGESTION_VALUE = "moderate";
    static final String HEAVY_CONGESTION_VALUE = "heavy";
    static final String SEVERE_CONGESTION_VALUE = "severe";
    NavigationMapboxMap navigationMapboxMap ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapVT.getInstance(getApplicationContext(), getString(R.string.access_token));
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (route != null) {
                    NavigationLauncherOptions.Builder optionsBuilder = NavigationLauncherOptions.builder()
                            .shouldSimulateRoute(true);

                    CameraPosition initialPosition = new CameraPosition.Builder()
                            .target(new LatLng(16.04791610056455, 108.21643351855755))
                            .zoom(10)
                            .build();
                    optionsBuilder.initialMapCameraPosition(initialPosition);
                    optionsBuilder.mapStyle(Style.VTMAP_TRAFFIC_DAY);

                    optionsBuilder.directionsRoute(route);
                    NavigationLauncher.startNavigation(RoutingActivity.this, optionsBuilder.build());

                    /**
                     * File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                     * String databaseFilePath = downloadDirectory + "/" + "kingfarm.db";
                     * String offlineStyleUrl = "mapbox://styles/mapbox/navigation-guidance-day-v4";
                     * optionsBuilder.offlineMapOptions(new MapOfflineOptions(databaseFilePath, offlineStyleUrl));
                     */
                   //

                    //fetchRouteOffline();
                }
            }
        });

        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull final VTMap VTMap) {
        this.vtMap = VTMap;
        vtMap.setStyle(Style.VTMAP_TRAFFIC_DAY, new Style.OnStyleLoaded() {

            public void onStyleLoaded(@NonNull final Style style) {
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
                        .packageId(MapVT.getPackageName());

                builder.origin(Point.fromLngLat(108.21059703174132, 16.066062180258257));
                builder.destination(Point.fromLngLat(108.21643351855755, 16.04791610056455));

                NavigationRoute navigationRoute = builder.build();
                navigationRoute.getRoute(new SimplifiedCallback() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        List<DirectionsRoute> routesFetched = response.body().routes();
                        route = routesFetched.get(0);
                        navigationMapboxMap.drawRoutes(routesFetched);
                       // navigationMapboxMap.addDestinationMarker(routesFetched.get(0).legs().get(routesFetched.get(0).legs().size() - 1).steps().get(routesFetched.get(0).legs().get(routesFetched.get(0).legs().size() - 1).steps().size() - 1).maneuver().location());
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

    private void drawRoute(Style style) {
        float routeScale = 0.7f;
        int routeShieldColor = Color.parseColor("#2F7AC6");
        int alternativeRouteShieldColor = Color.parseColor("#727E8D");

        int routeDefaultColor = Color.parseColor("#56A8FB");
        int alternativeRouteDefaultColor = Color.parseColor("#8694A5");
        int routeSevereColor = Color.parseColor("#E93340");
        int routeModerateColor = Color.parseColor("#F3A64F");

        LineLayer routeShieldLayer = initializeRouteShieldLayer(
                style, routeScale, routeShieldColor
        );
        style.addLayer(routeShieldLayer);

        LineLayer routeLayer = initializeRouteLayer(
                style, true, routeScale, routeScale,
                routeDefaultColor, routeModerateColor, routeSevereColor,
                alternativeRouteDefaultColor, routeModerateColor,
                routeModerateColor
        );
        style.addLayer(routeLayer);

        GeoJsonOptions routeLineGeoJsonOptions = new GeoJsonOptions().withMaxZoom(20);
        GeoJsonSource routeLineSource = new GeoJsonSource(ROUTE_SOURCE_ID, LineString.fromPolyline(
                route.geometry(), PRECISION_6), routeLineGeoJsonOptions);
        style.addSource(routeLineSource);
    }

    LineLayer initializeRouteShieldLayer(Style style, float routeScale,
                                         int routeShieldColor) {
        LineLayer shieldLayer = style.getLayerAs(ROUTE_SHIELD_LAYER_ID);
        if (shieldLayer != null) {
            style.removeLayer(shieldLayer);
        }

        shieldLayer = new LineLayer(ROUTE_SHIELD_LAYER_ID, ROUTE_SOURCE_ID).withProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(
                        interpolate(
                                exponential(1.5f), zoom(),
                                stop(10f, 7f),
                                stop(14f, product(literal(10.5f),
                                        literal(routeScale))),
                                stop(16.5f, product(literal(15.5f),
                                        literal(routeScale))),
                                stop(19f, product(literal(24f),
                                        literal(routeScale))),
                                stop(22f, product(literal(29f),
                                        literal(routeScale)))
                        )
                ),
                lineColor(color(routeShieldColor)
                )
        );
        return shieldLayer;
    }

    LineLayer initializeRouteLayer(Style style, boolean roundedLineCap, float routeScale,
                                   float alternativeRouteScale, int routeDefaultColor, int routeModerateColor,
                                   int routeSevereColor, int alternativeRouteDefaultColor,
                                   int alternativeRouteModerateColor, int alternativeRouteSevereColor) {
        LineLayer routeLayer = style.getLayerAs(ROUTE_LAYER_ID);
        if (routeLayer != null) {
            style.removeLayer(routeLayer);
        }

        String lineCap = Property.LINE_CAP_ROUND;
        String lineJoin = Property.LINE_JOIN_ROUND;
        if (!roundedLineCap) {
            lineCap = Property.LINE_CAP_BUTT;
            lineJoin = Property.LINE_JOIN_BEVEL;
        }

        routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID).withProperties(
                lineCap(lineCap),
                lineJoin(lineJoin),
                lineWidth(
                        interpolate(
                                exponential(1.5f), zoom(),
                                stop(4f, product(literal(3f),
                                        literal(routeScale))),
                                stop(10f, product(literal(4f),
                                        literal(routeScale))),
                                stop(13f, product(literal(6f),
                                        literal(routeScale))),
                                stop(16f, product(literal(10f),
                                        literal(routeScale))),
                                stop(19f, product(literal(14f),
                                        literal(routeScale))),
                                stop(22f, product(literal(18f),
                                        literal(routeScale)))
                        )
                ),
                lineColor(color(routeDefaultColor)
                )
        );
        return routeLayer;
    }


    public void boundCameraToRoute(DirectionsRoute route, NavigationMapboxMap map) {
        if (route != null) {
            List<Point> routeCoords = LineString.fromPolyline(route.geometry(),
                    PRECISION_6).coordinates();
            List<LatLng> bboxPoints = new ArrayList<>();
            for (Point point : routeCoords) {
                bboxPoints.add(new LatLng(point.latitude(), point.longitude()));
            }
            if (bboxPoints.size() > 1) {
                try {
                    LatLngBounds bounds = new LatLngBounds.Builder().includes(bboxPoints).build();
                    // left, top, right, bottom
                    int topPadding = 100;//directionFrame.getHeight() * 2;

                    CameraPosition position = vtMap.getCameraForLatLngBounds(bounds, new int[]{50, topPadding, 50, 100});
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(position);
                    vtMap.moveCamera(cameraUpdate);

                    NavigationCameraUpdate navigationCameraUpdate = new NavigationCameraUpdate(cameraUpdate);
                    navigationCameraUpdate.setMode(CameraUpdateMode.OVERRIDE);
                    map.retrieveCamera().update(navigationCameraUpdate, 1000);

                    vtMap.moveCamera(cameraUpdate);

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
