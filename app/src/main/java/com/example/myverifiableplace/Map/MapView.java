package com.example.myverifiableplace.Map;

import com.example.myverifiableplace.Data.Location;

import java.util.List;

public interface MapView {

    // 구글 지도 api 로드
    void loadMapFragment();

    // 위치 추가
    void locationAdded(boolean isSucessful, String error);

    // 룸에 있는 데이터 로드
    void updateLocation(List<Location> locations);

}
