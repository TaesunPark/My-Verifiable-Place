package com.example.myverifiableplace.Model;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Location.class}, version = 1)
public abstract class DatabaseManager extends RoomDatabase {
    public abstract LocationDao locationDao();

    // 싱글톤 구현
    private static DatabaseManager instance = null;

    public static synchronized DatabaseManager getInstance(Application application){

        if(instance == null){
            synchronized (DatabaseManager.class){
                instance = Room.databaseBuilder(application, DatabaseManager.class, "location_database")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return  instance;
    }
}