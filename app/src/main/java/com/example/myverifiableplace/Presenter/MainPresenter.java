package com.example.myverifiableplace.Presenter;

import com.example.myverifiableplace.Model.AppDatabase;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;


    @Override
    public void loadMapFragment() {

    }

    @Override
    public void getData() {

    }

    @Override
    public void attachView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }
}
