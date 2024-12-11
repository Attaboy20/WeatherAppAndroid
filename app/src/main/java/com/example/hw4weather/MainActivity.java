package com.example.hw4weather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.Geocoder;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hw4weather.databinding.ActivityMainBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.apache.commons.lang3.StringUtils;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private boolean isCelsius = true;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private FusedLocationProviderClient fusedLocationClient;

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    private ConnectivityManager connectivityManager;

    private final ArrayList<Weather> weather = new ArrayList<>();

    private VisualCrossingDownloader downloader;

    private TextView addressResolved;

    private RecyclerView recyclerView;
    private LineChart chartView;
    private ChartMaker chartMaker;
    private String cityName;

    private ConnectivityManager.NetworkCallback networkCallback;
    private List<DayRecord> dayRecords = new ArrayList<>();

    private SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        chartView = binding.chartView;
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addressResolved = binding.currentLocationBanner;
        binding.pickLocationButton.setOnClickListener(v -> showAddressDialog());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
            getLastLocation();
        }
        chartMaker = new ChartMaker(this, binding);
        binding.changeToMetric.setOnClickListener(v -> changeToMetric());
        binding.getCurrentLocationButton.setOnClickListener(v -> getLastLocation());
        binding.openMapButton.setOnClickListener(v -> {featureNotImplemented();});
        binding.shareButton.setOnClickListener(v -> {featureNotImplemented();});
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) binding.mainSwipe;
        swipeRefreshLayout.setOnRefreshListener(() -> {
            ((SwipeRefreshLayout) binding.mainSwipe).setRefreshing(false);
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            finish();
            startActivity(intent);
        });

        binding.openDateViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] address = addressResolved.getText().toString().split(",");
                String cityNameForTextView = address[0];
                openDailyActivity(v, cityNameForTextView);
            }
        });

    }

    private void featureNotImplemented() {
        Toast.makeText(this, "Feature not implemented", Toast.LENGTH_SHORT).show();
    }


    private void openDailyActivity(View v, String cityName) {
        Intent intent = new Intent(this, DailyActivity.class);
        intent.putExtra("dayRecords", new ArrayList<>(dayRecords));
        intent.putExtra("cityName", cityName);
        intent.putExtra("isCelsius", binding.changeToMetric.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.units_c).getConstantState()));
        startActivity(intent);
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            fetchWeatherDataForLocation(location);
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchWeatherDataForLocation(Location location) {
        String cityName = getCityNameFromLocation(location);
        this.cityName = cityName;
        fetchWeatherDataForCity(cityName);
    }


    private String getCityNameFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setIcon(R.drawable.alert);
            builder.setMessage("An error occurred while trying to get the location. Please try again.");
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            e.printStackTrace();
        }
        return null;
    }

    private void showAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a Location");
        builder.setMessage("For US locations, enter as 'City', or 'City, State'.\n \n For international locations, enter as 'City', Country'");
        final EditText input = new EditText(this);
        input.setHint("City name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String cityName = input.getText().toString().trim();
            if (!cityName.isEmpty()) {
                fetchWeatherDataForCity(cityName);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void fetchWeatherDataForCity(String cityName) {
        if (!isNetworkAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Connection");
            builder.setIcon(R.drawable.alert);
            builder.setMessage("No network connection available. Please try again later.");
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
            return;
        }
        Context context = this;
        HourlyAdapter adapter = new HourlyAdapter(new ArrayList<>(), recyclerView);
        recyclerView.setAdapter(adapter);
        VisualCrossingDownloader downloader = new VisualCrossingDownloader(this, adapter);
        downloader.getWeatherDataForCity(cityName, new VisualCrossingDownloader.WeatherCallback() {
            @Override
            public void onSuccess(ArrayList<Weather> weatherData, List<TimeRecord> timeRecords, TreeMap<String, Double> weatherEntries, List<DayRecord> dayRecords) {

                MainActivity.this.dayRecords = dayRecords;
                chartMaker.makeChart(weatherEntries, System.currentTimeMillis());
                binding.changeToMetric.setImageResource(R.drawable.units_f);
                Weather currentWeather = weatherData.get(0);
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EE, MMMM dd, h:mm a", Locale.getDefault());
                String formattedDateTime = now.format(formatter);
                CurrentConditions currentConditions = currentWeather.getCurrentConditions();
                String[] address = currentWeather.getResolvedAddress().split(",");
                String cityNameForTextView = address[0];
                String formattedText = String.format("%s, %s", StringUtils.capitalize(cityNameForTextView), formattedDateTime);
                addressResolved.setText(formattedText);
                double temp = Double.parseDouble(currentConditions.getTemp());
                String bigTemp = String.valueOf(temp) + "°F";
                binding.bigTemp.setText(bigTemp);
                String feelsLike = "Feels like: " + currentConditions.getFeelsLike() + "°F";
                binding.feelsLikeText.setText(feelsLike);
                String clouds = currentConditions.getConditions() + " (" + currentConditions.getCloudCover() + "% clouds)";
                binding.forecastTextview.setText(clouds);
                binding.humidity.setText(String.format("Humidity: %s%%", currentConditions.getHumidity()));
                binding.UVIndex.setText(String.format("UV Index: %s", currentConditions.getUvIndex()));
                binding.visibilityTextView.setText(String.format("Visibility: %s mi", currentWeather.getCurrentConditions().getVisibility()));
                String windDirection = getDirection(Double.parseDouble(currentConditions.getWindSpeed()));
                binding.windsSpeed.setText(String.format("Winds: %s at %s mph gusting to %s mph", windDirection, currentConditions.getWindSpeed(), currentConditions.getWindGust()));
                binding.visibilityTextView.setText(String.format("Visibility: %s mi", currentWeather.getCurrentConditions().getVisibility()));
                String sunrise = currentWeather.getCurrentConditions().getSunriseEpoch();
                String sunset = currentWeather.getCurrentConditions().getSunsetEpoch();
                String sunriseReadable = "Sunrise: " + convertEpochToReadable(sunrise);
                String sunsetReadable = "Sunset: " + convertEpochToReadable(sunset);
                binding.sunrise.setText(sunriseReadable);
                binding.sunset.setText(sunsetReadable);
                ColorMaker.setColorGradient(binding.getRoot(), temp, "F");

                String iconName = currentWeather.getCurrentConditions().getIcon();
                if (iconName.contains("-")) {
                    iconName = iconName.replace("-", "_");
                }
                int resourceId = getResources().getIdentifier(iconName, "drawable", getPackageName());
                binding.bigWeatherIcon.setImageResource(resourceId);
                if (timeRecords != null && timeRecords.size() > 0) {
                    TimeRecord firstDay = timeRecords.get(0);
                    adapter.updateData(timeRecords);
                }
            }

            @Override
            public void onError(String message) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Error");
                builder.setIcon(R.drawable.alert);
                builder.setMessage("The specified location could not be found. Please try again.");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                builder.show();
                getLastLocation();
            }
        });

    }

    private void changeToMetric() {
        if (binding.changeToMetric.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.units_c).getConstantState())) {
            changeToImperial();
            return;
        }
        if (binding.bigTemp.getText().toString().equals("70oF")) {
            return;
        }
        double tempF = Double.parseDouble(binding.bigTemp.getText().toString().replace("°F", ""));
        double tempC = (tempF - 32) * 5 / 9;
        binding.bigTemp.setText(String.format(Locale.getDefault(), "%.1f°C", tempC));
        binding.changeToMetric.setImageResource(R.drawable.units_c);
        ColorMaker.setColorGradient(binding.getRoot(), tempC, "C");
        double feelsLikeF = Double.parseDouble(binding.feelsLikeText.getText().toString().replace("Feels like: ", "").replace("°F", ""));
        double feelsLikeC = (feelsLikeF - 32) * 5 / 9;
        binding.feelsLikeText.setText(String.format(Locale.getDefault(), "Feels like: %.1f°C", feelsLikeC));
        HourlyAdapter adapter = (HourlyAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateTemperatureUnit(true);
        }
        TreeMap<String, Double> weatherEntries = chartMaker.getWeatherEntries();
        if (weatherEntries != null) {
            TreeMap<String, Double> weatherEntriesCelsius = new TreeMap<>();
            for (Map.Entry<String, Double> entry : weatherEntries.entrySet()) {
                double tempCelsius = (entry.getValue() - 32) * 5 / 9;
                weatherEntriesCelsius.put(entry.getKey(), tempCelsius);
            }
            chartMaker.makeChart(weatherEntriesCelsius, System.currentTimeMillis());



        }
    }

    private void changeToImperial() {
        if (binding.changeToMetric.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.units_f).getConstantState())) {
            changeToMetric();
            return;
        }
        // Convert current temperature to Fahrenheit
        double tempC = Double.parseDouble(binding.bigTemp.getText().toString().replace("°C", ""));
        double tempF = tempC * 9 / 5 + 32;
        binding.bigTemp.setText(String.format(Locale.getDefault(), "%.1f°F", tempF));
        binding.changeToMetric.setImageResource(R.drawable.units_f);
        ColorMaker.setColorGradient(binding.getRoot(), tempF, "F");

        // Convert feels like temperature to Fahrenheit
        double feelsLikeC = Double.parseDouble(binding.feelsLikeText.getText().toString().replace("Feels like: ", "").replace("°C", ""));
        double feelsLikeF = feelsLikeC * 9 / 5 + 32;
        binding.feelsLikeText.setText(String.format(Locale.getDefault(), "Feels like: %.1f°F", feelsLikeF));

        // Convert temperatures in RecyclerView
        HourlyAdapter adapter = (HourlyAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.updateTemperatureUnit(false);
        }

        // Convert temperatures in the chart
        TreeMap<String, Double> weatherEntries = chartMaker.getWeatherEntries();
        if (weatherEntries != null) {
            TreeMap<String, Double> weatherEntriesFahrenheit = new TreeMap<>();
            for (Map.Entry<String, Double> entry : weatherEntries.entrySet()) {
                double tempFahrenheit = entry.getValue() * 9 / 5 + 32;
                weatherEntriesFahrenheit.put(entry.getKey(), tempFahrenheit);
            }
            chartMaker.makeChart(weatherEntriesFahrenheit, System.currentTimeMillis());
            isCelsius = false;

        }
    }

    private String convertEpochToReadable(String epoch) {
        return new java.text.SimpleDateFormat("h:mm a").format(new java.util.Date(Integer.parseInt(epoch) * 1000L));
    }

    private boolean isNetworkAvailable() {
        if (connectivityManager == null) {
            return false;
        }
        Network currentNetwork = connectivityManager.getActiveNetwork();
        return currentNetwork != null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X";
    }


}

