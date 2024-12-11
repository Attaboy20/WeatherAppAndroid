package com.example.hw4weather;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw4weather.databinding.ActivityDailyBinding;
import com.example.hw4weather.databinding.RecyclerDayrecordBinding;

public class DailyViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "DailyViewHolder";
    TextView dateText;
    TextView tempText;
    TextView feelsLikeText;
    TextView conditionsText;
    TextView humidityText;
    TextView precipText;
    TextView uvText;
    TextView morningTemp;
    TextView afternoonTemp;
    TextView eveningTemp;
    TextView nightTemp;

    private DayRecord day;
    private final RecyclerDayrecordBinding binding;

    public DailyViewHolder(@NonNull View itemView) {
        super(itemView);
        this.binding = RecyclerDayrecordBinding.bind(itemView);
    }

    public void bind(DayRecord day, boolean isCelsius) {
        this.day = day;
        if (day == null) {
            return;
        }
        if (isCelsius) {
            binding.recyclerBigTemp.setText(day.getTempMax() + "°C/" + day.getTempMin() + "°C");
            binding.recyclerMorningTemp.setText(day.getMorning() + "°C");
            binding.recyclerAfternoonTemp.setText(day.getAfternoon() + "°C");
            binding.recyclerEveningTemp4.setText(day.getEvening() + "°C");
            binding.recyclerNightTemp.setText(day.getNight() + "°C");
        } else {
            binding.recyclerBigTemp.setText(day.getTempMax() + "°F/" + day.getTempMin() + "°F");
            binding.recyclerMorningTemp.setText(day.getMorning() + "°F");
            binding.recyclerAfternoonTemp.setText(day.getAfternoon() + "°F");
            binding.recyclerEveningTemp4.setText(day.getEvening() + "°F");
            binding.recyclerNightTemp.setText(day.getNight() + "°F");
        }
        binding.recyclerDate.setText(day.getDate());
        binding.recyclerConditions.setText(day.getConditions());
        String precip = "(" + day.getPrecip() + "% precip.)";
        binding.recyclerPrecip.setText(precip);
        String UV = "UV Index: " + day.getUVIndex();
        binding.recyclerUV.setText(UV);
        ColorMaker.setColorGradient(binding.getRoot(), day.getTempMax(), isCelsius ? "C" : "F");
    }




}

