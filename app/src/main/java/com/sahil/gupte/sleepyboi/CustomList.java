package com.sahil.gupte.sleepyboi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;

public class CustomList extends RecyclerView.Adapter<CustomList.RecyclerViewHolder> {
    private int count = 0;
    private final Activity context1;
    public final HashMap<Integer, RecyclerView.ViewHolder> holderHashMap = new HashMap<>();

    public CustomList(Activity context) {
        context1 = context;

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerViewHolder holder) {
        holderHashMap.put(holder.getAdapterPosition(),holder);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerViewHolder holder) {
        holderHashMap.remove(holder.getAdapterPosition());
        super.onViewAttachedToWindow(holder);

    }

    @Override
    public CustomList.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomList.RecyclerViewHolder holder, final int position) {
        SharedPreferences pref = context1.getSharedPreferences("place"+(position+1), 0);
        final String placeAddress = pref.getString("address", null);
        final long lat = pref.getLong("lat", 0);
        final long lon = pref.getLong("lng", 0);
        Log.d("test", "onBindViewHolder: "+lat);
        final int clockHour = pref.getInt("clockHour", -1);
        final int clockMin = pref.getInt("clockMin", -1);
        holder.placeName.setText(placeAddress);
        String hour = (clockHour < 10) ? "0"+clockHour : ""+clockHour;
        String min = (clockMin < 10) ? "0"+clockMin : ""+clockMin;
        holder.time.setText(hour + ":" + min);

        holder.frame.setOnTouchListener(
                new SwipeDismissTouchListener(
                holder.frame,
                null,
                new SwipeDismissTouchListener.DismissCallbacks() {

                    @Override
                    public void onClick(View view, Object token) {
                        Log.d("test", "onClick: inside onlcick");
                        Intent intent = new Intent(context1, MapsActivity.class);
                        intent.putExtra("lat",  lat);
                        intent.putExtra("lng",  lon);
                        intent.putExtra("placeAddress", placeAddress);
                        intent.putExtra("editMap", false);
                        context1.startActivity(intent);
                    }

                    @Override
                    public boolean canDismiss(Object token) {
                        return true;
                    }
                    @Override
                    public void onDismiss(View view, Object token) {
                        Intent intent = new Intent(context1, AddItemActivity.class);
                        intent.putExtra("placeName", placeAddress);
                        intent.putExtra("placeAddress", placeAddress);
                        intent.putExtra("lat",  lat);
                        intent.putExtra("lng",  lon);
                        intent.putExtra("clockHour", clockHour);
                        intent.putExtra("clockMin", clockMin);
                        intent.putExtra("count", position+1);
                        intent.putExtra("editMap", true);
                        context1.startActivity(intent);
                    }

                }
                ));

    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addItem() {
        count++;
    }

    public void removeItem() {
        if (count > 1) {
            count--;
        }
    }

    public void setCount(int count) {
        this.count = count;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        final TextView placeName;
        final TextView time;
        final LinearLayout frame;

        RecyclerViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.time);
            placeName = view.findViewById(R.id.placeName);
            frame = view.findViewById(R.id.content_frame);

        }
    }
}