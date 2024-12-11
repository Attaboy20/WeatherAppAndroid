package com.example.hw4weather;


import java.util.List;
import java.util.Map;

public class Weather {

    private double latitude;
    private double longitude;
    private String resolvedAddress;
    //private List<Day> days;

    private List<Alert> alerts;
    private CurrentConditions currentConditions;

    private List<DayRecord> days;

    public List<DayRecord> getDays() {
        return days;
    }


    public Weather(double latitude, double longitude, String resolvedAddress, List<Alert> alerts, CurrentConditions currentConditions) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.resolvedAddress = resolvedAddress;
//        this.days = days;
        this.alerts = alerts;
        this.currentConditions = currentConditions;
    }

    public CurrentConditions getCurrentConditions() {
        return currentConditions;
    }

    public void setDays(List<DayRecord> days) {
        this.days = days;
    }
    public List<Alert> getAlerts() {
        return alerts;
    }

//    public List<Day> getDays() {
//        return days;
//    }

    public String getResolvedAddress() {
        return resolvedAddress;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String toString() {
        return "Weather{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", resolvedAddress='" + resolvedAddress + '\'' +
                ", alerts=" + alerts +
                ", currentConditions=" + currentConditions +
                ", days=" + days +
                '}';
    }
}
