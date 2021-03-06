package com.example.myverifiableplace.Map;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.example.myverifiableplace.Data.Location;
import com.example.myverifiableplace.Data.DatabaseManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MapPresenter {

    private MapView mapView;
    private DatabaseManager databaseManager;

    private CompositeDisposable disposable;

    public MapPresenter(MapView mapView, DatabaseManager databaseManager){
        this.mapView = mapView;
        this.databaseManager = databaseManager;
        disposable = new CompositeDisposable();
        mapView.loadMapFragment();
        setLocation();
    }

    public void saveLocation(Location location){
        disposable.add(databaseManager.locationDao().insert(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> mapView.locationAdded(true, null), throwable ->
                {
                    if(throwable instanceof SQLiteConstraintException){
                        mapView.locationAdded(false, "Location 존재");
                    }else{
                        mapView.locationAdded(false, "Location 추가 실패");
                    }
                }));

    }

    public void setLocation(){

        disposable.add(databaseManager.locationDao().getLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(locations ->
                        mapView.updateLocation(locations)
                ));
    }


    public void dispose()
    {
        disposable.dispose();
    }
}
