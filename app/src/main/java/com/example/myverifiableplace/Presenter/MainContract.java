package com.example.myverifiableplace.Presenter;

public interface MainContract {

    interface View{

    }

    interface Presenter{

        void loadMap();

        void getData();

        void attachView(View view);

        void detachView();

    }

}
