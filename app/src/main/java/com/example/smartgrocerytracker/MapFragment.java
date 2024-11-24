package com.example.smartgrocerytracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentSlideshowBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // When map is loaded
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        // When clicked on the map

                        // Initialize marker options
                        MarkerOptions markerOptions = new MarkerOptions();

                        // Set the position of the marker
                        markerOptions.position(latLng);

                        // Set the title of the marker (latitude and longitude)
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                        // Remove all markers from the map
                        googleMap.clear();

                        // Animate camera to the marker position
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                        // Add the marker on the map
                        googleMap.addMarker(markerOptions);
                    }
                });
            }
        });


        // Return view
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}