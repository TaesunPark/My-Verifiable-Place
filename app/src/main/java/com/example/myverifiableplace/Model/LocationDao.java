package com.example.myverifiableplace.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface LocationDao {

    @Insert
    Completable insert(Location location);

    @Delete
    Completable delete(Location location);

    @Query("SELECT * FROM locationTable")
    Flowable<List<Location>> getLocations();

}
