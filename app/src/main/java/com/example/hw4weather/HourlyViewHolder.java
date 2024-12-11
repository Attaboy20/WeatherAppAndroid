package com.example.hw4weather;

import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw4weather.databinding.ActivityHourlyBinding;

import java.sql.Time;


public class HourlyViewHolder extends RecyclerView.ViewHolder {
    final ActivityHourlyBinding binding;
    private TimeRecord timeRecord;

    public HourlyViewHolder(ConstraintLayout binding) {
        super(binding);
        this.binding = ActivityHourlyBinding.bind(binding);
    }

    public void bind(TimeRecord timeRecord) {
        this.timeRecord = timeRecord;
        if (timeRecord == null) {
            Log.e("HourlyViewHolder", "TimeRecord is null");
            return;
        }


        binding.time.setText(timeRecord.getTime());
        binding.today.setText(timeRecord.getDayLabel());
        updateTemperatureDisplay(false);
        binding.smallConditions.setText(timeRecord.getConditions());

        String iconName = timeRecord.getIcon();
        if (iconName.contains("-")) {
            iconName = iconName.replace("-", "_");
        }
        int resourceId = itemView.getContext().getResources().getIdentifier(
                iconName, "drawable", itemView.getContext().getPackageName());

        if (resourceId != 0) {
            binding.smallWeatherIcon.setImageResource(resourceId);
        } else {
            binding.smallWeatherIcon.setImageResource(R.drawable.cloudy);
        }
    }
    public void updateTemperatureDisplay(boolean isCelsius) {
        if (timeRecord == null) {
            Log.e("HourlyViewHolder", "TimeRecord is null");
            return;
        }
        double temp = timeRecord.getTemp();
        if (isCelsius) {
            temp = (temp - 32) * 5 / 9;
            binding.smallTemp.setText(String.format("%.1f°C", temp));
        } else {
            binding.smallTemp.setText(String.format("%.1f°F", temp));
        }
    }


}

