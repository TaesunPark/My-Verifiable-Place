package com.example.myverifiableplace.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.Toast;

import com.example.myverifiableplace.Data.DatabaseManager;
import com.example.myverifiableplace.Data.Location;
import com.example.myverifiableplace.Location.LocationActivity;
import com.example.myverifiableplace.R;
import com.example.myverifiableplace.databinding.ActivityMainBinding;
import com.example.myverifiableplace.databinding.DialogStarBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MapView, View.OnClickListener {

    private MapPresenter mPresenter;
    private GoogleMap mMap;
    private Location mSelectedLocation = null;
    private LocationManager mLocationManager;
    private ActivityMainBinding binding;
    private DialogStarBinding dialogBinding;

    // 추가
    private Marker currentMarker = null;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소
    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 100000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 50000; // 0.5초


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;
    android.location.Location mCurrentLocatiion;
    LatLng currentPosition;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private android.location.Location location;
    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    private String markerTitle;
    private MarkerOptions markerOptions;
    Dialog saveDiaLog;
    LatLng currentLatLng;
    String markerSnippet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        binding.button2.setOnClickListener(this);
        binding.button3.setOnClickListener(this);
        dialogBinding = DialogStarBinding.inflate(getLayoutInflater());
        mLayout = binding.layoutMain;
        View view = binding.getRoot();
        binding.starButton.setOnClickListener(this);
        dialogBinding.buttonNoDialog.setOnClickListener(this);
        dialogBinding.buttonSaveDialog.setOnClickListener(this);

        setContentView(view);

        saveDiaLog = new Dialog(this);
        saveDiaLog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        saveDiaLog.setContentView(dialogBinding.dialogLayout);

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(500);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mPresenter = new MapPresenter(this, DatabaseManager.getInstance(getApplication()));



    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        mPresenter.dispose();
    }



    @Override
    public void loadMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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

                // 2. 이미 퍼미션을 가지고 있다면
                // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

                startLocationUpdates(); // 3. 위치 업데이트 시작
            }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

                // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                    Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                            ActivityCompat.requestPermissions( MainActivity.this, REQUIRED_PERMISSIONS,
                                    PERMISSIONS_REQUEST_CODE);
                        }
                    }).show();


                } else {
                    // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                    // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                    ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE);
                }

            }

            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        });
    }

    @Override
    public void locationAdded(boolean isSucessful, String error) {

    }

    @Override
    public void updateLocation(List<Location> locations) {

        LatLng currentLatLng;

        for(int i=0; i<locations.size();i++){

            markerOptions = new MarkerOptions();
            currentLatLng = new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude());
            markerOptions.position(currentLatLng);
            markerOptions.title(locations.get(i).getAddress()+"//"+locations.get(i).getName());
            markerOptions.snippet("위도:" + String.valueOf(locations.get(i).getLongitude())
                    + " 경도:" + String.valueOf(locations.get(i).getLatitude()));
            markerOptions.draggable(true);
            currentMarker = mMap.addMarker(markerOptions);

        }


    }


    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }

    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<android.location.Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                markerTitle = getCurrentAddress(currentPosition);
                markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);

                binding.textView.setText("현재 위치 :" + markerTitle);
                binding.textView2.setText("경도 :"+location.getLongitude());
                binding.textView3.setText("위도 :"+location.getLatitude());

                setCurrentLocation(location);

                mCurrentLocatiion = location;
            }


        }

    };

    public void setCurrentLocation(android.location.Location location) {

        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);

    }

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

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void shareLocation(){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/html");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "위도 : "+currentPosition.latitude+"\n"+"경도 : "+currentPosition.longitude+"\n"+"주소 : " + markerTitle);
        startActivity(Intent.createChooser(sharingIntent,"Share using text"));
    }

    @Override
    public void onClick(View v) {

        if(v.equals(binding.starButton)){
            dialogBinding.textViewLatitudeDialog.setText("위도 : "+currentPosition.latitude);
            dialogBinding.textViewLongitudeDialog.setText("경도 : "+currentPosition.longitude);
            dialogBinding.editTextNowLocation.setText(markerTitle);
            saveDiaLog.show();
            Window window = saveDiaLog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        } else if (v.equals(binding.button2)) {
            Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
            startActivity(intent);
        } else if (v.equals(binding.button3)){
            shareLocation();
        } else if (v.equals(dialogBinding.buttonNoDialog)){
            saveDiaLog.dismiss();
        } else if (v.equals(dialogBinding.buttonSaveDialog)){
            // 마커 표시
            // 룸에 저장
            markerTitle =  "대전광역시 대덕구 송촌동 472-6"+"//"+"송촌 주먹구이";

            markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(36.3632169,127.4387083));
            markerOptions.title(markerTitle);
            markerOptions.snippet("위도:" + String.valueOf(36.3632169) + "\n" +
                                            "경도:" + String.valueOf(127.4387083));
            markerOptions.draggable(true);
            currentMarker = mMap.addMarker(markerOptions);

            mPresenter.saveLocation(new Location(dialogBinding.edixTextLocationNameDialog.getText().toString(),
                    dialogBinding.editTextNowLocation.getText().toString(),
                    dialogBinding.editTextLocationMemoDialog.getText().toString(),
                    currentPosition.latitude, currentPosition.longitude));

            saveDiaLog.dismiss();
        }

    }
}