package com.sahil.gupte.sleepyboi.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.sahil.gupte.sleepyboi.Activities.AlarmScreenActivity;
import com.sahil.gupte.sleepyboi.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationService extends Service {

    private static final String API_KEY = Constants.API_KEY;
    private DirectionsRoute route;

    public NavigationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        launchAlarmScreen();


        double latitude = Double.longBitsToDouble(intent.getLongExtra("lat", 0));
        double longitude = Double.longBitsToDouble(intent.getLongExtra("lng", 0));
        double originLatitude = Double.longBitsToDouble(intent.getLongExtra("Orglat", 0));
        double originLongitude = Double.longBitsToDouble(intent.getLongExtra("Orglng", 0));

        new Thread(() -> {
            MapboxNavigation navigation = new MapboxNavigation(NavigationService.this, API_KEY);
            NavigationRoute.builder(NavigationService.this)
                    .accessToken(API_KEY)
                    .origin(Point.fromLngLat(originLongitude, originLatitude))
                    .destination(Point.fromLngLat(longitude, latitude))
                    .build()
                    .getRoute(new Callback<DirectionsResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
                            if (response.body() != null) {
                                route = response.body().routes().get(0);
                                if (route != null) {
                                    navigation.startNavigation(route);
                                    displayToast("Navigation started");
                                }
                                navigation.addProgressChangeListener((location, routeProgress) -> {
                                    if (routeProgress.distanceRemaining() < 800) {
                                        launchAlarmScreen();
                                        navigation.stopNavigation();
                                    }
                                });
                            } else {
                                displayToast("Unable to get route");
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) {

                        }
                    });
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void launchAlarmScreen() {
        Intent intent = new Intent(NavigationService.this, AlarmScreenActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
