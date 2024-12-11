package com.example.hw4weather;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hw4weather.databinding.ActivityHourlyBinding;

import java.util.List;

public class HourlyAdapter extends RecyclerView.Adapter<HourlyViewHolder> {


    private static final String TAG = "HourlyAdapter";

    private final List<TimeRecord> timeRecords;

    private RecyclerView recyclerView;

    public HourlyAdapter(List<TimeRecord> timeRecords, RecyclerView recyclerView) {
        this.timeRecords = timeRecords;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ActivityHourlyBinding binding = ActivityHourlyBinding.inflate(layoutInflater, parent, false);
        return new HourlyViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        TimeRecord timeRecord = timeRecords.get(position);
        holder.bind(timeRecord);
    }

    @Override
    public int getItemCount() {
        return timeRecords.size();
    }

    public void updateData(List<TimeRecord> newRecords) {
        this.timeRecords.clear();
        this.timeRecords.addAll(newRecords);
        notifyDataSetChanged();
    }

    public List<TimeRecord> getTimeRecords() {
        return timeRecords;
    }

    public void updateTemperatureUnit(boolean isCelsius) {
        for (int i = 0; i < getItemCount(); i++) {

            HourlyViewHolder holder = (HourlyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                holder.updateTemperatureDisplay(isCelsius);
            }
        }
    }
}

