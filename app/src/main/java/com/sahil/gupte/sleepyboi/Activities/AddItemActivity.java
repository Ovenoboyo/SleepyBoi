package com.sahil.gupte.sleepyboi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sahil.gupte.sleepyboi.Constants;
import com.sahil.gupte.sleepyboi.Customs.PlaceInfoHolder;
import com.sahil.gupte.sleepyboi.Database.DatabaseHelper;
import com.sahil.gupte.sleepyboi.R;
import com.sahil.gupte.sleepyboi.Utils.ThemeUtils;

import java.util.Objects;

public class AddItemActivity extends AppCompatActivity {

    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this, getApplicationContext());
        setContentView(R.layout.activity_add_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent myIntent = getIntent();
        count = myIntent.getIntExtra(Constants.count, 0);

        final double latitude = myIntent.getDoubleExtra(Constants.latitudeKey, 0);
        final double longitude = myIntent.getDoubleExtra(Constants.longitudeKey, 0);
        final String placeAddress = myIntent.getStringExtra(Constants.placeAddress);
        final String placeName = myIntent.getStringExtra(Constants.placeName);

        TextView locationDisplay = findViewById(R.id.location_display);
        if (placeAddress != null) {
            locationDisplay.setText(placeAddress);
        }

        locationDisplay.setOnClickListener(view -> {
            Intent mapsActivity = new Intent(getBaseContext(), MapsActivity.class);
            mapsActivity.putExtra(Constants.placeName, placeName);
            mapsActivity.putExtra(Constants.count, count);
            mapsActivity.putExtra(Constants.latitudeKey, latitude);
            mapsActivity.putExtra(Constants.longitudeKey, longitude);
            mapsActivity.putExtra("editMap", true);
            finish();
            startActivity(mapsActivity);
        });

        final TextView nameInput = findViewById(R.id.nameInput);
        nameInput.setText(placeName);

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(view -> {
            if (!nameInput.getText().toString().trim().equals("")) {
                if (latitude != 0 && longitude != 0) {
                    if (placeAddress != null) {
                        DatabaseHelper databaseHelper = new DatabaseHelper(this);
                        PlaceInfoHolder placeInfoHolder = new PlaceInfoHolder(latitude, longitude, placeAddress, nameInput.getText().toString().trim(), count);
                        databaseHelper.addHandler(placeInfoHolder);
                        finish();
                        startActivity(new Intent(AddItemActivity.this, MainActivity.class));
                    } else {
                        displayToast("Address can not be empty");
                    }
                } else {
                    displayToast("Invalid longitude and latitude");
                }
            } else {
                displayToast("Name can not be empty");
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
        super.onBackPressed();
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
