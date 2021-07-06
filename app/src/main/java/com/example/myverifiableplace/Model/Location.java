package com.example.myverifiableplace.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "locationTable")
public class Location {
    @PrimaryKey
    public int lid;

    // 위치 주소
    @ColumnInfo(name = "address")
    public String address;

    // 위도
    @ColumnInfo(name = "latitude")
    public double latitude;

    // 경도
    @ColumnInfo(name = "longitude")
    public double longitude;

}
