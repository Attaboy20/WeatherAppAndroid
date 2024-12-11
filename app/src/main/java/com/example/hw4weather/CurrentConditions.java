package com.example.hw4weather;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class CurrentConditions {

    @SerializedName("temp")
    private String temp;

    @SerializedName("feelslike")
    private String feelsLike;

    @SerializedName("humidity")
    private String humidity;

    @SerializedName("uvindex")
    private String uvIndex;

    @SerializedName("icon")
    private String icon;

    @SerializedName("conditions")
    private String conditions;

    @SerializedName("cloudcover")
    private String cloudCover;

    @SerializedName("winddir")
    private String windDirection;

    @SerializedName("windspeed")
    private String windSpeed;

    @SerializedName("windgust")
    private String windGust;

    @SerializedName("visibility")
    private String visibility;

    @SerializedName("sunriseEpoch")
    private String sunriseEpoch;

    @SerializedName("sunsetEpoch")
    private String sunsetEpoch;

    public CurrentConditions(String sunsetEpoch, String sunriseEpoch, String visibility, String windGust, String windSpeed, String windDirection, String cloudCover, String conditions, String icon, String uvIndex, String humidity, String feelsLike, String temp) {
        this.sunsetEpoch = sunsetEpoch;
        this.sunriseEpoch = sunriseEpoch;
        this.visibility = visibility;
        this.windGust = windGust;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.cloudCover = cloudCover;
        this.conditions = conditions;
        this.icon = icon;
        this.uvIndex = uvIndex;
        this.humidity = humidity;
        this.feelsLike = feelsLike;
        this.temp = temp;
    }

    public String getSunsetEpoch() {
        return sunsetEpoch;
    }

    public String getSunriseEpoch() {
        return sunriseEpoch;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getWindGust() {
        return windGust;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public String getCloudCover() {
        return cloudCover;
    }

    public String getConditions() {
        return conditions;
    }

    public String getIcon() {
        return icon;
    }

    public String getUvIndex() {
        return uvIndex;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public String getTemp() {
        return temp;
    }


    @NonNull
    @Override
    public String toString() {
        return "CurrentConditions{" +
                "temp='" + temp + '\'' +
                ", feelsLike='" + feelsLike + '\'' +
                ", humidity='" + humidity + '\'' +
                ", uvIndex='" + uvIndex + '\'' +
                ", icon='" + icon + '\'' +
                ", conditions='" + conditions + '\'' +
                ", cloudCover='" + cloudCover + '\'' +
                ", windDirection='" + windDirection + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", windGust='" + windGust + '\'' +
                ", visibility='" + visibility + '\'' +
                ", sunriseEpoch='" + sunriseEpoch + '\'' +
                ", sunsetEpoch='" + sunsetEpoch + '\'' +
                '}';
    }
}
