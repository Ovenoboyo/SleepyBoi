package com.sahil.gupte.sleepyboi.Activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sahil.gupte.sleepyboi.Constants;
import com.sahil.gupte.sleepyboi.Customs.PlaceInfoHolder;
import com.sahil.gupte.sleepyboi.Database.DatabaseHelper;
import com.sahil.gupte.sleepyboi.R;

import java.util.Objects;

public class AddItemActivity extends AppCompatActivity {

    private int clockHour = -1;
    private int clockMin = -1;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent myIntent = getIntent();
        count = myIntent.getIntExtra("count", 0);

        final double latitude = myIntent.getDoubleExtra(Constants.latitudeKey, 0);
        final double longitude = myIntent.getDoubleExtra(Constants.longitudeKey, 0);
        String placeName = myIntent.getStringExtra("placeName");
        final String placeAddress = myIntent.getStringExtra("placeAddress");

        clockHour = myIntent.getIntExtra("clockHour", -1);
        clockMin = myIntent.getIntExtra("clockMin", -1);

        FrameLayout timePicker = findViewById(R.id.timePicker);
        final TextView timeDisplay = findViewById(R.id.time_display);
        timePicker.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(AddItemActivity.this, (timePicker1, i, i1) -> {
                clockHour = i;
                clockMin = i1;
                String mins;
                if (i1 < 10) {
                    mins = "0" + i1;
                } else {
                    mins = String.valueOf(i1);
                }
                timeDisplay.setText(i + ":" + mins);
            }, (clockHour != -1) ? clockHour : 0, (clockMin != -1) ? clockMin : 0, true);
            timePickerDialog.show();
        });

        Button mapPicker = findViewById(R.id.mapPicker);
        TextView locationDisplay = findViewById(R.id.location_display);
        if (placeName != null) {
            locationDisplay.setText(placeName);
        }

        if (clockMin != -1 && clockHour != -1) {
            String hour = (clockHour < 10) ? "0" + clockHour : "" + clockHour;
            String min = (clockMin < 10) ? "0" + clockMin : "" + clockMin;
            timeDisplay.setText(hour + ":" + min);
        }

        mapPicker.setOnClickListener(view -> {
            Intent mapsActivity = new Intent(getBaseContext(), MapsActivity.class);
            mapsActivity.putExtra("count", count);
            mapsActivity.putExtra("clockHour", clockHour);
            mapsActivity.putExtra("clockMin", clockMin);
            mapsActivity.putExtra(Constants.latitudeKey, latitude);
            mapsActivity.putExtra(Constants.longitudeKey, longitude);
            mapsActivity.putExtra("editMap", true);
            startActivity(mapsActivity);
        });

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(view -> {

            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            PlaceInfoHolder placeInfoHolder = new PlaceInfoHolder(latitude, longitude, placeAddress, placeName);
            databaseHelper.addHandler(placeInfoHolder);

            Intent intent = new Intent(AddItemActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        savedInstanceState.putInt("clockHour", clockHour);
        savedInstanceState.putInt("clockHour", clockMin);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        clockHour = savedInstanceState.getInt("clockHour");
        clockMin = savedInstanceState.getInt("clockMin");
    }
}
