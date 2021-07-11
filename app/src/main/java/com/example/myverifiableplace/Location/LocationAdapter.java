package com.example.myverifiableplace.Location;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myverifiableplace.Data.Location;
import com.example.myverifiableplace.R;
import com.example.myverifiableplace.databinding.DialogStarBinding;
import com.example.myverifiableplace.databinding.LocationItemBinding;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<Location> data = new ArrayList<>();
    private LocationAdapterListener listener;

    public LocationAdapter(LocationAdapterListener listener){
        this.listener = listener;
    }

    public void setLocations(List<Location> locations){
        data = locations;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);

        return new LocationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        final Location location = data.get(position);
        holder.name.setText(location.getName());
        holder.address.setText(location.getAddress());
        holder.latitude.setText(String.valueOf("위도 :" +location.getLatitude()));
        holder.longitude.setText(String.valueOf("경도 :"+location.getLongitude()));
        holder.memo.setText(location.getMemo());
        holder.deleteButton.setOnClickListener(v -> listener.onDeleted(location));
        holder.selectButton.setOnClickListener(v -> listener.onSelected(location));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView address;
        TextView latitude;
        TextView longitude;
        TextView memo;
        Button selectButton;
        Button deleteButton;

        public LocationViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.textView_name_location_item);
            address = view.findViewById(R.id.textView_address_location_item);
            latitude = view.findViewById(R.id.textView_latitude_location_item);
            longitude = view.findViewById(R.id.textView_longitude_location_item);
            selectButton = view.findViewById(R.id.button_select_location_item);
            deleteButton = view.findViewById(R.id.button_delete_location_item);
            memo = view.findViewById(R.id.textView_memo_location_item);
        }
    }

    interface LocationAdapterListener
    {
        void onSelected(Location location);

        void onDeleted(Location location);

        void onUpdated(Location location);

    }

}
