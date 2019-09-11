package com.sahil.gupte.sleepyboi;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class AddItemActivity extends AppCompatActivity {

    int clockHour = -1, clockMin = -1;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent myIntent = getIntent();
        count = myIntent.getIntExtra("count", 0);

        final long latitude = myIntent.getLongExtra("lat", 0);
        final long longitude = myIntent.getLongExtra("lng", 0);
        Log.d("test", "onCreate: "+latitude+", "+longitude);
        String placeName = myIntent.getStringExtra("placeName");
        final String placeAddress = myIntent.getStringExtra("placeAddress");

        clockHour = myIntent.getIntExtra("clockHour", -1);
        clockMin = myIntent.getIntExtra("clockMin", -1);

        FrameLayout timePicker = findViewById(R.id.timePicker);
        final TextView timeDisplay = findViewById(R.id.time_display);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddItemActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        clockHour = i;
                        clockMin = i1;
                        String mins;
                        if (i1 < 10) {
                            mins = "0"+i1;
                        } else {
                            mins = String.valueOf(i1);
                        }
                        timeDisplay.setText(i + ":" + mins);
                    }
                }, (clockHour != -1) ? clockHour : 0, (clockMin != -1) ? clockMin : 0,true);
                timePickerDialog.show();
            }
        });

        Button mapPicker = findViewById(R.id.mapPicker);
        TextView locationDisplay = findViewById(R.id.location_display);
        if (placeName != null) {
            locationDisplay.setText(placeName);
        }

        if (clockMin != -1 && clockHour != -1) {
            String hour = (clockHour < 10) ? "0"+clockHour : ""+clockHour;
            String min = (clockMin < 10) ? "0"+clockMin : ""+clockMin;
            timeDisplay.setText(hour + ":" + min);
        }

        mapPicker.setOnClickListener(view -> {
            Intent mapsActivity = new Intent(getBaseContext(), MapsActivity.class);
            mapsActivity.putExtra("count", count);
            mapsActivity.putExtra("clockHour", clockHour);
            mapsActivity.putExtra("clockMin", clockMin);
            mapsActivity.putExtra("lat", latitude);
            mapsActivity.putExtra("lng", longitude);
            mapsActivity.putExtra("editMap", true);
            startActivity(mapsActivity);
        });

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(view -> {
            Log.d("test", "onClick: "+latitude);
            SharedPreferences pref = getSharedPreferences("place"+count, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putLong("lat", latitude);
            editor.putLong("lng", longitude);
            editor.putString("address", placeAddress);
            editor.putInt("clockHour", clockHour);
            editor.putInt("clockMin", clockMin);
            editor.apply();
            Intent intent = new Intent(AddItemActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("clockHour", clockHour);
        savedInstanceState.putInt("clockHour", clockMin);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        clockHour = savedInstanceState.getInt("clockHour");
        clockMin = savedInstanceState.getInt("clockMin");
    }
}
