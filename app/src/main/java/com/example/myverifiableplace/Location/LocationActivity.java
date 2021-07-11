package com.example.myverifiableplace.Location;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myverifiableplace.Data.DatabaseManager;
import com.example.myverifiableplace.Data.Location;
import com.example.myverifiableplace.databinding.ActivityLocationBinding;
import com.example.myverifiableplace.databinding.LocationItemBinding;

import java.util.List;

public class LocationActivity extends AppCompatActivity  implements LocationView, LocationAdapter.LocationAdapterListener {


    ActivityLocationBinding binding;
    private LocationPresenter mPresenter;
    private LocationAdapter mLocationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.activityLocation);


        mLocationAdapter = new LocationAdapter(this);
        binding.recycelerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recycelerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        binding.recycelerView.setAdapter(mLocationAdapter);

        mPresenter = new LocationPresenter(this, DatabaseManager.getInstance(getApplication()));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        mPresenter.dispose();
    }

    @Override
    public void onSelected(Location location) {

    }

    @Override
    public void onDeleted(Location location) {
        new AlertDialog.Builder(LocationActivity.this)
                .setTitle("삭제 메시지")
                .setMessage("장소를 삭제하시겠습니까 " + location.getName() + "?")
                .setPositiveButton("YES", (dialog, which) -> mPresenter.deleteLocation(location))
                .setNegativeButton("NO", null)
                .show();
    }

    @Override
    public void onUpdated(Location location) {

    }

    @Override
    public void updateLocation(List<Location> locations) {
        if(locations.isEmpty())
        {
            binding.recycelerView.setVisibility(View.GONE);
        }
        else
        {
            binding.recycelerView.setVisibility(View.VISIBLE);
            mLocationAdapter.setLocations(locations);
        }
    }
}
