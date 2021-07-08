package com.example.myverifiableplace.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "locationTable")
public class Location {
    @PrimaryKey
    @NonNull
    private String name;

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    private String address;

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // 위도
    @NonNull
    private double latitude;

    // 경도
    @NonNull
    private double longitude;

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
