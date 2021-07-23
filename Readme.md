# 개인 프로젝트 보고서

### 주제

MVP, Singleton 패턴을 활용한 현재 위치 저장 및 공유 서비스

### 개발 배경

평소 길을 가다가 기억하고 싶은 장소들이 있다.

여행을 하다 가도 기억하고 싶은 장소가 있다. 해외여행이면 더욱 그렇다.

하지만 사람들은 그 때 생각하고 지나간다. 이번에 만든 MVP, Singleton 패턴을 활용한 이 프로젝트는 현재 위치를 저장하는 서비스이다. 이 서비스를 이용하면, 현재 위치, 주소를 실시간으로 받아볼 수 있으며, 그 장소를 저장하고 싶으면 버튼 하나로 위치 및 정보를 저장할 수 있다. 

물론 저장 리스트도 볼 수 있다. 3주차에 1, 2주차 공부한 이론들을 토대로 짧은 기간 동안 만들어 봤지만, 개인적으로 아이디어가 너무 좋아 서비스를 확장하고 싶은 마음이 있어서 창의 학기가 끝나도 개인적으로 발전시킬 계획이다.

### 개발 환경 구성

- 개발 툴

    Android-Studio

- 클라이언트 환경

    OS : Android 6.0 이상

    개발 언어 : JAVA

- 외부 라이브러리

    Google Map - 구글 맵 API 기반 서비스

    Room Database - 앱 데이터베이스로 Room 활용

    RxJava - 데이터베이스 접근할 때 스레드 비동기 처리를 위해 RxJava 방식 사용.

### 전체 클래스 구조

![Untitled](https://user-images.githubusercontent.com/59998914/125204466-6ae22f00-e2b8-11eb-80d2-d6332905384d.png)

![Untitled 1](https://user-images.githubusercontent.com/59998914/125204467-6b7ac580-e2b8-11eb-94a9-773a4dfef90e.png)
### MVP 패턴의 M, Model 부분을 담고 있는 Data 패키지

![Untitled 2](https://user-images.githubusercontent.com/59998914/125204468-6b7ac580-e2b8-11eb-8b85-8f2021252c9d.png)

Database Manager 클래스를 먼저 살펴보면

RoomDatabase를 상속 받아서 관련 기능을 다룬다.

모든 클래스에서 데이터베이스를 호출할 수 있게 싱글턴 패턴을 적용했다.

추상 클래스인 LocationDao를 담고있는 데 이 클래스는 Room Database에 접근할 기능들을 담당한다. 따라서 모든 클래스에서 DatabaseManager 클래스를 생성하고, LocationDao를 사용해 Database에 접근을 할 수 있다.

```java
package com.example.myverifiableplace.Data;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myverifiableplace.Data.Location;
import com.example.myverifiableplace.Data.LocationDao;

@Database(entities = {Location.class}, version = 1)
public abstract class DatabaseManager extends RoomDatabase {
    public abstract LocationDao locationDao();

    // 싱글톤 구현
    private static DatabaseManager instance = null;

    public static synchronized DatabaseManager getInstance(Application application) {

        if (instance == null) {
            synchronized (DatabaseManager.class) {
                instance = Room.databaseBuilder(application, DatabaseManager.class, 
												"location_database")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return instance;
    }
}
```

다음으로 Location (Dto) 클래스를 살펴보면 

![Untitled 3](https://user-images.githubusercontent.com/59998914/125204447-661d7b00-e2b8-11eb-8f2f-c0dadb425586.png)

장소에 대한 Dto 클래스이다.

장소의 이름 name, 장소의 주소 address, 장소에 대한 세부 내용 memo,

장소의 경도 longitude, 장소의 위도 latitude로 구성되어 있다.

또한 데이터베이스에 만들어 질 테이블 명을 "locationTable"으로 칭하였다.

```java
package com.example.myverifiableplace.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "locationTable")
public class Location {
    @PrimaryKey
    @NonNull
    private String name;
    @NonNull
    private String address;
    // 위도
    @NonNull
    private double latitude;
    // 경도
    @NonNull
    private double longitude;

    @NonNull
    private String memo;

    public Location(String name, String address,String memo, double latitude, 
				double longitude){
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.memo = memo;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo() {
        return memo;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}


다음으로 LocationDao 클래스를 살펴보면, 

![Untitled 4](https://user-images.githubusercontent.com/59998914/125204450-66b61180-e2b8-11eb-9f7f-3e9a6eabfa29.png)

RoomDatabase에 접근해서 데이터들을 입력, 삭제, 가져올 수 있는 기능들을 담당하고 있다.

쿼리 문으로 원하는 테이블에 정보들을 가져올 수 있는 기능을 만들 수 있다.

이 프로젝트 내에서 필요한 장소를 테이블에 저장하는 insert(Location location) 함수와, 

장소를 테이블에서 제거하는 delete(Location location) 함수, 그리고 어플을 처음 실행할 때 저장한 데이터를 가져올 수 있게 해주는 select 쿼리문을 가진 getLocations() 함수로 이루어져 있다.

각 Location은 Database의 locationTable 테이블 클래스이다.

getLocations 함수의 Select 쿼리 문은 전체 저장되어 있는 장소를 가져오는 쿼리문으로써

SELECT * FROM locationTable 로 작성했다.

```java
package com.example.myverifiableplace.Data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface LocationDao {

    @Insert
    Completable insert(Location location);

    @Delete
    Completable delete(Location location);

    @Query("SELECT * FROM locationTable")
    Flowable<List<Location>> getLocations();

}
```

### Map 패키지

![Untitled 5](https://user-images.githubusercontent.com/59998914/125204451-674ea800-e2b8-11eb-84ff-ade67ce346bf.png)

구조로 보자면 MapPresenter에서 View와, Model에 관한 연결을 해줘야한다.

그래서 MapPresenter 클래스부터 살펴보겠다.

![Untitled 6](https://user-images.githubusercontent.com/59998914/125204453-674ea800-e2b8-11eb-950b-ca5abd165e55.png)

MapPresenter 클래스에서는 Singleton 패턴으로 만든 DatabaseManager을 이용해서 Model과 연결해서 이미 저장되어 있는 location들을 가져오는 setLocation() 함수와

View인 Activity에서 버튼을 클릭해 위치를 저장해서 Model에 저장해야 하기 때문에 데이터베이스에 insert하는 saveLocation() 함수로 이루어져 있다.

```java
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
```

view에서 처리를 해줘야하는 기능을 모은 MapView 클래스이다.

![Untitled 7](https://user-images.githubusercontent.com/59998914/125204454-67e73e80-e2b8-11eb-90f5-06e4e5f6247b.png)

```java
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
```

View를 담당하는 ActivityMain 클래스이다.

![Untitled 8](https://user-images.githubusercontent.com/59998914/125204455-67e73e80-e2b8-11eb-8167-fa57918a5b32.png)

MainActivity 에서는 전반적인 엑티비티에서 이루어지는 동작들을 처리한다.

loadMapFragment() 함수로 구글 맵 프레그먼트와 연동하고 실행을 하는데,

핸드폰에서 위치 권한을 허용해야지, 현재 위치를 받아올 수 있다.

그래서 위치 권한을 확인, 허용하는 동작을 한다.

```java
@Override
    public void loadMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
																															.findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            mMap = googleMap;

            setDefaultLocation();

            //런타임 퍼미션 처리
            // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                    hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

                startLocationUpdates(); // 3. 위치 업데이트 시작
            }else { 

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, 
																																REQUIRED_PERMISSIONS[0])) {

                    Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인",
																															 new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            ActivityCompat.requestPermissions( MainActivity.this, 
																															REQUIRED_PERMISSIONS,
													                                    PERMISSIONS_REQUEST_CODE);
                        }
                    }).show();

                } else {

                    ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE);
                }

            }

            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        });
    }
```

또한, 현재 위치의 경도, 위도를 받아와서 주소로 바꿔주는 getCurrentAddress(LatLng latlng) 함수를 만들어줬다. 이 함수에선 Geocoder를 이용해서 경,위도를 주소로 변환해준다.

```java
public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }
```

현재 위치를 카메라로 잡기 위해 setCurrentLocation(Location location) 함수를 만들어주었다.

```java
public void setCurrentLocation(android.location.Location location) {

        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);

    }
```

현재 위치, 주소를 공유하기 위해 shareLocation() 함수를 만들어 주었다.

```java
public void shareLocation(){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "위도 : "+currentPosition.latitude+
				"\n"+"경도 : "+currentPosition.longitude+"\n"+"주소 : " + markerTitle);
        startActivity(Intent.createChooser(sharingIntent,"Share using text"));
    }
```

구글 맵 라이브러리를 사용함으로 써 현재 위치에 마커를 표시할 수 있다.

```java
if(v.equals(binding.starButton)){
            dialogBinding.textViewLatitudeDialog.setText("위도 : "+currentPosition.
						latitude);
            dialogBinding.textViewLongitudeDialog.setText("경도 : "+currentPosition.
						longitude);
            dialogBinding.editTextNowLocation.setText(markerTitle);
            saveDiaLog.show();
            Window window = saveDiaLog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, 
						WindowManager.LayoutParams.WRAP_CONTENT);
        }
else if (v.equals(dialogBinding.buttonSaveDialog)){
            // 마커 표시
            // 룸에 저장
            markerTitle +=  "//"+dialogBinding.edixTextLocationNameDialog.getText();

            markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(true);
            currentMarker = mMap.addMarker(markerOptions);

            mPresenter.saveLocation(new Location(dialogBinding
										.edixTextLocationNameDialog.getText().toString(),
                    dialogBinding.editTextNowLocation.getText().toString(),
                    dialogBinding.editTextLocationMemoDialog.getText().toString(),
                    currentPosition.latitude, currentPosition.longitude));

            saveDiaLog.dismiss();
        }
```

### Location 패키지

![Untitled 9](https://user-images.githubusercontent.com/59998914/125204456-687fd500-e2b8-11eb-947a-40c0a3c08f14.png)

구조로 보자면 LocationPresenter에서 View와, Model에 관한 연결을 해줘야한다.

Location 패키지는 어떤 기능들을 모아놨냐면, 위치를 저장하면 그걸 Recycleview로 List로 만드는

거다. 그래서 LocationAdapter가 존재한다.

LocationView에는 리스트를 처음 불러오는 updateLocation만 존재한다.

```java
package com.example.myverifiableplace.Location;

import com.example.myverifiableplace.Data.Location;

import java.util.List;

public interface LocationView {

    // 룸에 있는 데이터 로드
    void updateLocation(List<Location> locations);

}
```

LocationAdapter 클래스는 데이터베이스에서 받아온 위치 리스트들을 관리하는 데

LocationViewHolder 클래스를 통해 리사이클뷰에 접근할 수 있다.

Model과 View의 중추역할인 Presenter 역할을 하는 LocationPresenter 클래스는 

Database에서 위치 데이터를 가져와서 리사이클뷰에 뿌려주는 getLocations() 함수와

라시아클뷰에서 데이터를 삭제하면 데이터베이스에서 빠지는 deleteLocation(Location location)으로 이루어져 있다.

```java
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
```

### 서비스 시나리오

![Untitled 10](https://user-images.githubusercontent.com/59998914/125204458-687fd500-e2b8-11eb-8c68-25a0285c6e90.png)

처음 어플을 실행시키면 현재 위치와 내 위치를 받아볼 수 있다.

![Untitled 11](https://user-images.githubusercontent.com/59998914/125204460-69186b80-e2b8-11eb-86af-38b3d210337e.png)

별 모양을 누르면 현재 위치를 저장할 수 있다.

장소 이름은 현재 위치하고 있는 장소 이름을 치면 된다. 물론 안쳐도 상관없다.

세부 기록은 내가 기록하고 싶은 메모장처럼 기록하면 된다. 물론 안쳐도 상관없다.

현재 주소와, 경도, 위도는 자동으로 표시된다.

"예" 버튼을 누르면 아래 사진과 같이 데이터베이스에 저장이 된다.

![Untitled 12](https://user-images.githubusercontent.com/59998914/125204461-69b10200-e2b8-11eb-897e-587c9a3f6618.png)

그리고 3번째 사진을 보는 거와 같이 마커가 표시된다.

![Untitled 13](https://user-images.githubusercontent.com/59998914/125204462-6a499880-e2b8-11eb-9069-33eb14ffb13d.png)

저장 목록을 누르면 내가 저장한 장소와 기록들을 ListView로 받아볼 수 있다.

![Untitled 14](https://user-images.githubusercontent.com/59998914/125204463-6a499880-e2b8-11eb-9f7d-68e973c42d4b.png)

위치 공유를 누르면 내 현재 위치를 각종 어플을 통해서 상대방에게 보낼 수 있다.

예시 그림을 보면 카카오톡으로 위치를 보낸 걸 확인할 수 있다.

![Untitled 15](https://user-images.githubusercontent.com/59998914/125204465-6ae22f00-e2b8-11eb-8723-7d574b812834.png)

또한 이전에 저장했던 가고 싶은 위치를 클릭하고 오른쪽 하단에 이미지 버튼을 누르면

어떻게 가야할 지 구글 맵에서 제공하는 길찾기 시스템을 이용할 수 있다.

### 결론

MVP, Singleton 패턴에 대해 이해를 충분히 하고 적용을 해서 패턴에 대한 이해,

자바에 대한 이해를 도왔다. 앞으로 개발할 때 그 서비스에 맞는 디자인 패턴을 찾아

적용할 것이고, 창의학기가 끝났지만 이 서비스 또한 발전해볼 계획이다.
