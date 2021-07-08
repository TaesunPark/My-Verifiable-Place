package com.example.myverifiableplace.Map;

public interface MapView {

    // 구글 지도 api 로드
    void loadMapFragment();

    // 위치 추가
    void locationAdded(boolean isSucessful, String error);

}
