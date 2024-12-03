package com.example.smartgrocerytracker.ui.maps;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.R;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private final List<String[]> placeList;

    public PlaceAdapter(List<String[]> placeList) {
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        String[] place = placeList.get(position);
        holder.name.setText(place[0]);
        holder.address.setText(place[1]);
        holder.status.setText(place[2]);
        holder.distance.setText(place[3]);
        holder.estTime.setText(place[4]);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, status, distance, estTime;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.place_name);
            address = itemView.findViewById(R.id.place_address);
            status = itemView.findViewById(R.id.place_status);
            distance = itemView.findViewById(R.id.place_distance);
            estTime = itemView.findViewById(R.id.place_est_time);
        }
    }
}