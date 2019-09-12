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
import com.sahil.gupte.sleepyboi.Listeners.SwipeDismissTouchListener;
import com.sahil.gupte.sleepyboi.R;

import java.util.HashMap;

public class CustomList extends RecyclerView.Adapter<CustomList.RecyclerViewHolder> {
    private final Activity context1;
    private final HashMap<Integer, RecyclerView.ViewHolder> holderHashMap = new HashMap<>();
    private int count = 0;

    public CustomList(Activity context) {
        context1 = context;
        SharedPreferences pref = context1.getSharedPreferences("place0", 0);
        Log.d("test", "CustomList: " + pref.contains("address"));
        while (pref.contains("address")) {
            count++;
            pref = context1.getSharedPreferences("place" + count, 0);
        }
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
        SharedPreferences pref = context1.getSharedPreferences("place" + (position), 0);
        final String placeAddress = pref.getString("address", null);
        final String placeName = pref.getString("name", null);
        final long lat = pref.getLong(Constants.latitudeKey, 0);
        final long lon = pref.getLong(Constants.longitudeKey, 0);
        final int clockHour = pref.getInt("clockHour", -1);
        final int clockMin = pref.getInt("clockMin", -1);

        holder.placeName.setText(placeAddress);
        String hour = (clockHour < 10) ? "0" + clockHour : "" + clockHour;
        String min = (clockMin < 10) ? "0" + clockMin : "" + clockMin;
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
                                intent.putExtra("clockHour", clockHour);
                                intent.putExtra("clockMin", clockMin);
                                intent.putExtra("count", position);
                                intent.putExtra("editMap", true);
                                context1.startActivity(intent);
                            }

                            @Override
                            public void onDismissRight() {
                                //broken
                                pref.edit().clear().apply();
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