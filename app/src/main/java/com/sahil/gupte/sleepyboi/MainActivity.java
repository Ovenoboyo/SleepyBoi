package com.sahil.gupte.sleepyboi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView list;
    private CustomList listAdapter;
    private ArrayList<String> recyclerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listAdapter = new CustomList(this);
        list = findViewById(R.id.custom_list);
        list.setAdapter(listAdapter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(llm);

        int i = 1;
        SharedPreferences pref = getSharedPreferences("place"+i, 0);

        while (pref.contains("address")) {
            String placeName = pref.getString("address", null);
            recyclerList.add(placeName);
            i++;
            pref = getSharedPreferences("place"+i, 0);
        }

        Log.d("test", "onCreate: "+recyclerList);


        listAdapter.setCount(recyclerList.size());

        FrameLayout addNew = findViewById(R.id.add_button);
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent AddItemActivity = new Intent(getBaseContext(), AddItemActivity.class);
                AddItemActivity.putExtra("count", listAdapter.getItemCount());
                startActivity(AddItemActivity);
            }
        });

        //Button switch_map = findViewById(R.id.switch_map);

        /*switch_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(i);
            }
        }); */
    }
}
