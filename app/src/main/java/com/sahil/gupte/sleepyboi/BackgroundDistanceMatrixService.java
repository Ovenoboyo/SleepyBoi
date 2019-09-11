package com.sahil.gupte.sleepyboi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import java.io.IOException;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment.TAG;

public class BackgroundDistanceMatrixService extends Service {

    DirectionsRoute route;
    private static final String API_KEY = BuildConfig.ApiKey;

    public BackgroundDistanceMatrixService() {
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
            MapboxNavigation navigation = new MapboxNavigation(BackgroundDistanceMatrixService.this, API_KEY);
            NavigationRoute.builder(BackgroundDistanceMatrixService.this)
                    .accessToken(API_KEY)
                    .origin(Point.fromLngLat(originLongitude, originLatitude))
                    .destination(Point.fromLngLat(longitude, latitude))
                    .build()
                    .getRoute(new Callback<DirectionsResponse>() {
                        @Override
                        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
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
                        public void onFailure(Call<DirectionsResponse> call, Throwable t) {

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
        Intent intent = new Intent(BackgroundDistanceMatrixService.this, AlarmScreenActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
