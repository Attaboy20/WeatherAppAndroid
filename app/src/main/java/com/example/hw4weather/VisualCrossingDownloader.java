package com.example.hw4weather;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VisualCrossingDownloader {

    static final String API_KEY = "TY4FVHABQ8GZR273TLDUS2ARD";

    private static RequestQueue requestQueue;


    private static final String BASE_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";


    private static final String TAG = "VisualCrossingDownloader";

    private static HourlyAdapter adapter;
    private static ExecutorService executorService;

    public VisualCrossingDownloader(Context context, HourlyAdapter adapter) {
        requestQueue = Volley.newRequestQueue(context);
        this.adapter = adapter;
        executorService = Executors.newSingleThreadExecutor();
    }
    public static void getWeatherDataForCity(String cityName, final WeatherCallback callback) {
        executorService.execute(() -> {

            String url = BASE_URL + cityName + "?key=" + API_KEY;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            TreeMap<String, Double> weatherEntries = new TreeMap<>();
                            List<TimeRecord> timeRecords = new ArrayList<>();

                            try {
                                JSONObject currentConditionsJson = response.getJSONObject("currentConditions");

                                JSONArray daysJsonArray = response.getJSONArray("days");


                                // get first three days
                                for (int i = 0; i < 3 && i < daysJsonArray.length(); i++) {
                                    JSONObject dayJson = daysJsonArray.getJSONObject(i);
                                    JSONArray hoursJsonArray = dayJson.getJSONArray("hours");

                                    String dayLabel;
                                    if (i == 0) {
                                        dayLabel = "Today";
                                    } else if (i == 1) {
                                        dayLabel = "Tomorrow";
                                    } else {
                                        Calendar cal = Calendar.getInstance();
                                        cal.add(Calendar.DAY_OF_YEAR, i);
                                        dayLabel = new SimpleDateFormat("EEEE").format(cal.getTime());
                                    }

                                    for (int j = 0; j < hoursJsonArray.length(); j++) {
                                        JSONObject hourJson = hoursJsonArray.getJSONObject(j);
                                        Calendar hourCal = Calendar.getInstance();
                                        String timestamp = hourJson.getString("datetime");
                                        double tempChart = hourJson.getDouble("temp");
                                        weatherEntries.put(timestamp, tempChart);

                                        if (i == 0 && hourCal.before(Calendar.getInstance())) {
                                            continue;
                                        }
                                        String time = new SimpleDateFormat("h a").format(hourCal.getTime());
                                        String icon = hourJson.getString("icon");
                                        double temp = hourJson.getDouble("temp");
                                        String conditions = hourJson.getString("conditions");
                                        TimeRecord entry = new TimeRecord(time, temp, conditions, icon, dayLabel);
                                        timeRecords.add(entry);
                                    }
                                }

                                adapter.updateData(timeRecords);
                                Gson gson = new Gson();
                                CurrentConditions currentConditions = gson.fromJson(currentConditionsJson.toString(), CurrentConditions.class);
                                List<DayRecord> dayRecords = new ArrayList<>();
                                for (int i = 0; i < daysJsonArray.length(); i++) {
                                    JSONObject dayJson = daysJsonArray.getJSONObject(i);
                                    JSONArray hoursJsonArray = dayJson.getJSONArray("hours");

                                    Double morningTemp = getHourTemp(hoursJsonArray, 8);
                                    Double afternoonTemp = getHourTemp(hoursJsonArray, 13);
                                    Double eveningTemp = getHourTemp(hoursJsonArray, 17);
                                    Double nightTemp = getHourTemp(hoursJsonArray, 23);
                                    String date = new SimpleDateFormat("EEEE, MM/dd").format(new SimpleDateFormat("yyyy-MM-dd").parse(dayJson.getString("datetime")));

                                    DayRecord dayRecord = new DayRecord(
                                            dayJson.getDouble("tempmin"),
                                            dayJson.getDouble("tempmax"),
                                            dayJson.getDouble("feelslike"),
                                            dayJson.getDouble("humidity"),
                                            dayJson.getDouble("precipprob"),
                                            eveningTemp,
                                            morningTemp,
                                            nightTemp,
                                            afternoonTemp,
                                            dayJson.getDouble("uvindex"),
                                            dayJson.getString("description"),
                                            dayJson.getString("icon"),
                                            null,
                                            date
                                    );
                                    dayRecords.add(dayRecord);
                                }

                                response.getJSONArray("alerts");
                                Weather weather = new Weather(
                                        response.getDouble("latitude"),
                                        response.getDouble("longitude"),
                                        response.getString("resolvedAddress"),
                                        gson.fromJson(response.getJSONArray("alerts").toString(), new TypeToken<List<Alert>>() {
                                        }.getType()),
                                        currentConditions
                                );
                                weather.setDays(dayRecords);
                                ArrayList<Weather> weatherData = new ArrayList<>();
                                weatherData.add(weather);
                                callback.onSuccess(weatherData, timeRecords, weatherEntries, dayRecords);
                            } catch (Exception e) {
                                Log.e("VisualCrossingDownloader", "Error parsing JSON data.", e);

                                callback.onError("Failed to parse weather data");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VisualCrossingDownloader", "Error fetching data", error);
                            callback.onError("Failed to fetch weather data");
                        }
                    }
            );


            requestQueue.add(jsonObjectRequest);
        });
    }


    private static Double getHourTemp(JSONArray hoursJsonArray, int hourIndex) {
        try {
            if (hoursJsonArray.length() > hourIndex) {
                return hoursJsonArray.getJSONObject(hourIndex).getDouble("temp");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting hour temperature.", e);
        }
        return null;
    }

    public interface WeatherCallback {
        void onSuccess(ArrayList<Weather> weatherData, List<TimeRecord> timeRecords, TreeMap<String, Double> weatherEntries, List<DayRecord> dayRecords);
        void onError(String message);
    }

}

