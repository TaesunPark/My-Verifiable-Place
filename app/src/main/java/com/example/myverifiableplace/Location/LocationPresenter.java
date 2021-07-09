package com.example.myverifiableplace.Location;

import com.example.myverifiableplace.Data.DatabaseManager;
import com.example.myverifiableplace.Data.Location;
import com.example.myverifiableplace.Map.MapView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class LocationPresenter {

    private LocationView locationView;
    private DatabaseManager databaseManager;

    private CompositeDisposable disposable;

    public LocationPresenter(LocationView locationView, DatabaseManager databaseManager){
        this.locationView = locationView;
        this.databaseManager = databaseManager;
        disposable = new CompositeDisposable();
        getLocations();
    }

    private void getLocations()
    {
        disposable.add(databaseManager.locationDao().getLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(locations -> locationView.updateLocation(locations)));
    }

    public void deleteLocation(Location location)
    {
        disposable.add(databaseManager.locationDao().delete(location)
                .subscribeOn(Schedulers.io())
                .subscribe());


    }

    public void dispose()
    {
        disposable.dispose();
    }

}
