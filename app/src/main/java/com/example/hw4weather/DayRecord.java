package com.example.hw4weather;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DayRecord implements Serializable {
    @SerializedName("tempmin")
    private double tempMin;

    @SerializedName("tempmax")
    private double tempMax;
    private double feelslike;

    private Double humidity;

    @SerializedName("precipprob")
    private Double precip;
    private Double evening;
    private Double morning;
    private Double night;
    private Double afternoon;

    @SerializedName("uvindex")
    private double UVIndex;


    @SerializedName("description")
    private String conditions;
    private List<TimeRecord> hours;

    private String icon;

    private String dateTime;

    public DayRecord(double tempMin, double tempMax, double feelslike, Double humidity, Double precip, Double evening, Double morning, Double night, Double afternoon, double UVIndex, String conditions, String icon, String string, String dateTime) {
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.feelslike = feelslike;
        this.humidity = humidity;
        this.precip = precip;
        this.evening = evening;
        this.morning = morning;
        this.night = night;
        this.afternoon = afternoon;
        this.UVIndex = UVIndex;
        this.conditions = conditions;
        this.icon = icon;
        this.dateTime = dateTime;
    }


    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getPrecip() {
        return precip;
    }

    public Double getEvening() {
        return evening;
    }

    public Double getMorning() {
        return morning;
    }

    public Double getNight() {
        return night;
    }

    public Double getAfternoon() {
        return afternoon;
    }

    public double getUVIndex() {
        return UVIndex;
    }

    public String getIcon() {
        return icon;
    }

    public String getDateTime() {
        return dateTime;
    }

    public double getFeelslike() {
        return feelslike;
    }

    public String getConditions() {
        return conditions;
    }

    public List<TimeRecord> getHours() {
        return hours;
    }

    @NonNull
    @Override
    public String toString() {
        return "DayRecord{" +
                "tempMin=" + tempMin +
                ", tempMax=" + tempMax +
                ", feelslike=" + feelslike +
                ", humidity=" + humidity +
                ", precip=" + precip +
                ", evening=" + evening +
                ", morning=" + morning +
                ", night=" + night +
                ", afternoon=" + afternoon +
                ", UVIndex=" + UVIndex +
                ", conditions='" + conditions + '\'' +
                ", hours=" + hours +
                ", icon='" + icon + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }

    public String getDate() {
        return dateTime;
    }

    public void setCelsius(boolean isCelsius) {
        if (isCelsius) {
            tempMin = Double.parseDouble(String.format("%.1f", (tempMin - 32) * 5 / 9));
            tempMax = Double.parseDouble(String.format("%.1f", (tempMax - 32) * 5 / 9));
            feelslike = Double.parseDouble(String.format("%.1f", (feelslike - 32) * 5 / 9));
            afternoon = Double.parseDouble(String.format("%.1f", (afternoon - 32) * 5 / 9));
            evening = Double.parseDouble(String.format("%.1f", (evening - 32) * 5 / 9));
            morning = Double.parseDouble(String.format("%.1f", (morning - 32) * 5 / 9));
            night = Double.parseDouble(String.format("%.1f", (night - 32) * 5 / 9));
        }
    }
}


