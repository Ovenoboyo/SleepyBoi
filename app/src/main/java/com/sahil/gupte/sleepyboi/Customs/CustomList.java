package com.sahil.gupte.sleepyboi.Customs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sahil.gupte.sleepyboi.Activities.AddItemActivity;
import com.sahil.gupte.sleepyboi.Activities.MapsActivity;
import com.sahil.gupte.sleepyboi.Constants;
import com.sahil.gupte.sleepyboi.Database.DatabaseHelper;
import com.sahil.gupte.sleepyboi.Listeners.SwipeDismissTouchListener;
import com.sahil.gupte.sleepyboi.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomList extends RecyclerView.Adapter<CustomList.RecyclerViewHolder> {
    private final Activity context1;
    private final HashMap<Integer, RecyclerView.ViewHolder> holderHashMap = new HashMap<>();
    private ArrayList<Integer> countArray;
    private int count;
    private DatabaseHelper databaseHelper;

    public CustomList(Activity context) {
        context1 = context;
        databaseHelper = new DatabaseHelper(context1);
        countArray = databaseHelper.getCountArray();
        Log.d("test", "CustomList: "+countArray);
        count = countArray.size();
        Log.d("test", "CustomList: " + count);

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerViewHolder holder) {
        holderHashMap.put(holder.getAdapterPosition(), holder);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerViewHolder holder) {
        holderHashMap.remove(holder.getAdapterPosition());
        super.onViewAttachedToWindow(holder);

    }

    @NonNull
    @Override
    public CustomList.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomList.RecyclerViewHolder holder, final int position) {
        PlaceInfoHolder placeInfoHolder = databaseHelper.getHandler(countArray.get(position));
        final String placeAddress = placeInfoHolder.getAddress();
        final String placeName = placeInfoHolder.getName();
        final double lat = placeInfoHolder.getLatitude();
        final double lon = placeInfoHolder.getLongitude();

        holder.placeName.setText(placeAddress);
        holder.name.setText(placeName);

        holder.frame.setOnTouchListener(
                new SwipeDismissTouchListener(
                        holder.frame,
                        holder.bgTextStart,
                        holder.bgTextEnd,
                        null,
                        new SwipeDismissTouchListener.DismissCallbacks() {

                            @Override
                            public boolean canDismiss() {
                                return true;
                            }

                            @Override
                            public void onDismissLeft() {
                                Intent intent = new Intent(context1, AddItemActivity.class);
                                intent.putExtra("placeName", placeAddress);
                                intent.putExtra("placeAddress", placeAddress);
                                intent.putExtra(Constants.latitudeKey, lat);
                                intent.putExtra(Constants.longitudeKey, lon);
                                intent.putExtra("count", countArray.get(position));
                                intent.putExtra("editMap", true);
                                context1.startActivity(intent);
                            }

                            @Override
                            public void onDismissRight() {
                                databaseHelper.removeHandler(countArray.get(position));
                                countArray = databaseHelper.getCountArray();
                                count = countArray.size();
                                notifyDataSetChanged();
                            }

                        })
        );

        holder.frame.setOnClickListener(view -> {
            Intent intent = new Intent(context1, MapsActivity.class);
            intent.putExtra(Constants.latitudeKey, lat);
            intent.putExtra(Constants.longitudeKey, lon);
            intent.putExtra("placeAddress", placeAddress);
            intent.putExtra("editMap", false);
            intent.putExtra("count", countArray.get(position));
            context1.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void setCount(int count) {
        this.count = count;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        final TextView placeName;
        final TextView name;
        final LinearLayout frame;
        final TextView bgTextStart;
        final TextView bgTextEnd;

        RecyclerViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            placeName = view.findViewById(R.id.placeName);
            frame = view.findViewById(R.id.content_frame);
            bgTextStart = view.findViewById(R.id.bgTextStart);
            bgTextEnd = view.findViewById(R.id.bgTextEnd);

        }
    }
}