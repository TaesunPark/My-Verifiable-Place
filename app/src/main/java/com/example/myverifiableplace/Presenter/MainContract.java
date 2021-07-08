package com.example.myverifiableplace.Presenter;

public interface MainContract {

    interface View{

    }

    interface Presenter{

        void loadMapFragment();

        void getData();

        void attachView(View view);

        void detachView();

    }

}
