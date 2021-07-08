package com.example.myverifiableplace.Map;

import com.example.myverifiableplace.Model.DatabaseManager;
import com.example.myverifiableplace.Model.Location;

import io.reactivex.disposables.CompositeDisposable;

public class MapPresenter {

    private MapView mapView;
    private DatabaseManager databaseManager;

    private CompositeDisposable disposable;

    public MapPresenter(MapView mapView, DatabaseManager databaseManager){
        this.mapView = mapView;
        this.databaseManager = databaseManager;

        disposable = new CompositeDisposable();

        mapView.loadMapFragment();
    }

    public void saveLocation(Location location){
//        disposable.add(databaseManager.locationDao().insert)

    }
}
