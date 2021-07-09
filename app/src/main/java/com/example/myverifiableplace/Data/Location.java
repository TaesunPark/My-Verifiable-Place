package com.example.myverifiableplace.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "locationTable")
public class Location {


    @PrimaryKey
    @NonNull
    private String name;
    @NonNull
    private String address;
    // 위도
    @NonNull
    private double latitude;
    // 경도
    @NonNull
    private double longitude;

    public Location(String name, String address, double latitude, double longitude){
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


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
