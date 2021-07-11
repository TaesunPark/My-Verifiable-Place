package com.example.myverifiableplace.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;


@Dao
public interface LocationDao {

    @Insert
    Completable insert(Location location);

    @Delete
    Completable delete(Location location);

    @Query("SELECT * FROM locationTable")
    Flowable<List<Location>> getLocations();


}
