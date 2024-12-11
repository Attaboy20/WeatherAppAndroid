package com.example.hw4weather;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw4weather.databinding.ActivityDailyBinding;

import java.util.ArrayList;


public class DailyAdapter extends RecyclerView.Adapter<DailyViewHolder> {
    private ArrayList<DayRecord> days;
    private Context context;

    private boolean isCelsius;

    public DailyAdapter(ArrayList<DayRecord> days, Context context, Boolean isCelsius) {
        this.days = days;
        this.context = context;
        this.isCelsius = isCelsius;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_dayrecord, parent, false);
        return new DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder holder, int position) {
        DayRecord day = days.get(position);
        holder.bind(day, isCelsius);
    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public void setCelsius(boolean isCelsius) {
        this.isCelsius = isCelsius;
        for (DayRecord day : days) {
            day.setCelsius(isCelsius);
        }
        notifyDataSetChanged();
    }
}