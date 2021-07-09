package com.example.myverifiableplace.Location;

import com.example.myverifiableplace.Data.Location;

import java.util.List;

public interface LocationView {

    // 룸에 있는 데이터 로드
    void updateLocation(List<Location> locations);

}
