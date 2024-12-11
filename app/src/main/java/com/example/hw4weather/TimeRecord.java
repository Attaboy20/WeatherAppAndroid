package com.example.hw4weather;

import java.io.Serializable;

public class TimeRecord implements Serializable {

    private final String time;
    private double temp;

    private final String conditions;

    private final String icon;
    private final String dayLabel;

    public TimeRecord(String time, double temp, String conditions, String icon, String dayLabel) {
        this.time = time;
        this.temp = temp;
        this.icon = icon;
        this.conditions = conditions;
        this.dayLabel = dayLabel;
    }

    public String getTime() {
        return time;
    }

    public double getTemp() {
        return temp;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public String getConditions() {
        return conditions;
    }
    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "TimeRecord{" +
                "time='" + time + '\'' +
                ", temp=" + temp +
                ", icon='" + icon + '\'' +
                ", conditions='" + conditions + '\'' +
                ", dayLabel='" + dayLabel + '\'' +
                '}';
    }

    public void convertTempToCelsius() {
        this.temp = (this.temp - 32) * 5 / 9;
    }

    public void convertTempToFahrenheit() {
        this.temp = this.temp * 9 / 5 + 32;
    }


}
