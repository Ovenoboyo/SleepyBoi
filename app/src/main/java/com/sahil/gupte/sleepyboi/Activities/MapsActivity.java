package com.sahil.gupte.sleepyboi.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.sahil.gupte.sleepyboi.Constants;
import com.sahil.gupte.sleepyboi.Customs.PlaceInfoHolder;
import com.sahil.gupte.sleepyboi.R;
import com.sahil.gupte.sleepyboi.Services.NavigationService;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_VIEWPORT;

@SuppressWarnings("deprecation")
public class MapsActivity extends Activity implements OnMapReadyCallback, PermissionsListener {

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final int REQUEST_CODE_LOCATIONREQUEST = 2;
    private static final String API_KEY = Constants.API_KEY;
    private final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private final MapActivityLocationCallback callback = new MapActivityLocationCallback(this);
    private MapView mapView;
    private MapboxMap mapboxMap;
    private int count, clockMin, clockHour;
    private Double latitude;
    private Double longitude;
    private Double OriginLatitude;
    private Double OriginLongitude;
    private LocationEngine locationEngine;
    private PermissionsManager permissionsManager;
    private Marker marker;
    private boolean editMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        Mapbox.getInstance(this, API_KEY);
        setContentView(R.layout.activity_maps);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        Intent myIntent = getIntent();
        count = myIntent.getIntExtra(Constants.count, 0);
        latitude = Double.longBitsToDouble(myIntent.getLongExtra(Constants.latitudeKey, 0));
        longitude = Double.longBitsToDouble(myIntent.getLongExtra(Constants.longitudeKey, 0));
        editMap = myIntent.getBooleanExtra("editMap", false);

        Button confirm = findViewById(R.id.confirm);

        if (!editMap) {
            confirm.setOnClickListener(view -> startService());
        } else {
            confirm.setOnClickListener(view -> switchActivity());
        }
    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.TRAFFIC_NIGHT,
                style -> {
                    enableLocationComponent(style);
                    initSearchFab();
                    SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

                    symbolManager.addClickListener(symbol -> {
                    });

                    symbolManager.addLongClickListener(symbol -> {
                    });

                    symbolManager.setIconAllowOverlap(true);
                    symbolManager.setIconTranslate(new Float[]{-4f, 5f});
                    symbolManager.setIconRotationAlignment(ICON_ROTATION_ALIGNMENT_VIEWPORT);
                });

        if (latitude != 0 && longitude != 0) {
            marker = mapboxMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("" + latitude + ", " + longitude));
        }
        if (editMap) {
            mapboxMap.addOnMapClickListener(point -> {
                if (marker != null) {
                    marker.setPosition(point);
                    marker.setTitle("" + point.getLatitude() + ", " + point.getLongitude());
                }
                return true;
            });
        }
    }

    @SuppressWarnings({"MissingPermission"})
    // Initialise location tracker
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).setAlwaysShow(true);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        builder.setAlwaysShow(true);
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(locationSettingsRequest);

            task.addOnCompleteListener(task1 -> {
                try {
                    LocationSettingsResponse response = task1.getResult(ApiException.class);
                    LocationComponent locationComponent = mapboxMap.getLocationComponent();

                    LocationComponentActivationOptions locationComponentActivationOptions =
                            LocationComponentActivationOptions.builder(this, loadedMapStyle)
                                    .useDefaultLocationEngine(false)
                                    .build();

                    locationComponent.activateLocationComponent(locationComponentActivationOptions);

                    locationComponent.setLocationComponentEnabled(true);

                    locationComponent.setCameraMode(CameraMode.TRACKING);
                    locationComponent.zoomWhileTracking(15);

                    locationComponent.setRenderMode(RenderMode.COMPASS);

                    initLocationEngine();

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        MapsActivity.this,
                                        REQUEST_CODE_LOCATIONREQUEST);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            finish();
                            break;
                    }
                }
            });
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void initSearchFab() {
        findViewById(R.id.fab_location_search).setOnClickListener(view -> {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : API_KEY)
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(10)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(MapsActivity.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        });
    }

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }


    private PlaceInfoHolder setPlaceInfoHolder(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.getLatitude(), latLng.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
            addresses = null;
        }
        return new PlaceInfoHolder(Objects.requireNonNull(addresses).get(0).getLatitude(), addresses.get(0).getLongitude(), addresses.get(0).getAddressLine(0), "test", count);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void switchActivity() {
        PlaceInfoHolder placeInfoHolder = setPlaceInfoHolder(marker.getPosition());
        Intent intent = new Intent(this, AddItemActivity.class);
        intent.putExtra(Constants.placeName, placeInfoHolder.getAddress());
        intent.putExtra(Constants.placeAddress, placeInfoHolder.getAddress());
        intent.putExtra(Constants.latitudeKey, placeInfoHolder.getLatitude());
        intent.putExtra(Constants.longitudeKey, placeInfoHolder.getLongitude());
        intent.putExtra(Constants.count, count);
        finish();
        startActivity(intent);
    }

    private void startService() {
        Intent intent = new Intent(this, NavigationService.class);
        intent.putExtra(Constants.latitudeKey, Double.doubleToRawLongBits(latitude));
        intent.putExtra(Constants.longitudeKey, Double.doubleToRawLongBits(longitude));
        intent.putExtra("Orglat", Double.doubleToRawLongBits(OriginLatitude));
        intent.putExtra("Orglng", Double.doubleToRawLongBits(OriginLongitude));
        startService(intent);
        finish();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Need location perms", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (!granted) {
            Toast.makeText(this, "Need location perms", Toast.LENGTH_LONG).show();
            finish();
        } else {
            if (mapboxMap.getStyle() != null) {
                enableLocationComponent(mapboxMap.getStyle());
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_LOCATIONREQUEST:
                final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        if (mapboxMap.getStyle() != null) {
                            enableLocationComponent(mapboxMap.getStyle());
                        }
                        Toast.makeText(MapsActivity.this, states.isLocationPresent() + "", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(MapsActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;

            case REQUEST_CODE_AUTOCOMPLETE:
                if (resultCode == Activity.RESULT_OK) {
                    CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).latitude(),
                                            ((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).longitude()))
                                    .zoom(14)
                                    .build()), 4000);

                    marker.setPosition(new LatLng(((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).latitude(),
                            ((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).longitude()));
                    marker.setTitle("" + ((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).latitude() + ", " + ((Point) Objects.requireNonNull(selectedCarmenFeature.geometry())).longitude());
                    break;

                }
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private static class MapActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapsActivity> activityWeakReference;

        MapActivityLocationCallback(MapsActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        public void onSuccess(LocationEngineResult result) {
            MapsActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                    activity.OriginLatitude = result.getLastLocation().getLatitude();
                    activity.OriginLongitude = result.getLastLocation().getLongitude();
                    if (activity.marker == null && activity.latitude == 0 && activity.longitude == 0) {
                        activity.marker = activity.mapboxMap.addMarker(new MarkerOptions().position(new LatLng(result.getLastLocation().getLatitude(), result.getLastLocation().getLongitude())).title("" + result.getLastLocation().getLatitude() + ", " + result.getLastLocation().getLongitude()));
                    }
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can not be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            MapsActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
