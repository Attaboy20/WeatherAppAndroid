package com.example.hw4weather;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw4weather.databinding.ActivityDailyBinding;

import java.util.ArrayList;
import java.util.List;

public class DailyActivity extends AppCompatActivity {

    private ActivityDailyBinding binding;
    private DailyAdapter dailyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDailyBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = binding.dailyRecyclerViewFifteen;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<DayRecord> dayRecords = (ArrayList<DayRecord>) getIntent().getSerializableExtra("dayRecords");
        if (dayRecords == null) {
            dayRecords = new ArrayList<>();
        }
        boolean isCelsius = getIntent().getBooleanExtra("isCelsius", false);
        dailyAdapter = new DailyAdapter(dayRecords, this, isCelsius);
        recyclerView.setAdapter(dailyAdapter);
        String cityName = getIntent().getStringExtra("cityName");
        binding.locationFifteenDay.setText(cityName + " 15 Day Forecast");
        dailyAdapter.setCelsius(isCelsius);

    }


}


