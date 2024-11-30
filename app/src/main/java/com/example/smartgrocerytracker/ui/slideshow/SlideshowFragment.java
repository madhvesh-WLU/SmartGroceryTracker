package com.example.smartgrocerytracker.ui.slideshow;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class SlideshowFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        // Initialize MapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.map_fragment, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(this);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        return root;
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Check Location Permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Enable My Location Layer
        mMap.setMyLocationEnabled(true);

        // Ensure GPS is enabled
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(requireContext(), "Please enable GPS for accurate location", Toast.LENGTH_LONG).show();
            return;
        }

        // Get and Show User's Live Location
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));

                // Show nearby grocery stores
                showNearbyGroceryStores(location);
            } else {
                Log.e("LocationDebug", "Failed to retrieve user details: location is null");
                Toast.makeText(requireContext(), "Failed to retrieve user details. Try again.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("LocationDebug", "Failed to retrieve user details: " + e.getMessage());
            Toast.makeText(requireContext(), "Failed to retrieve user details. Try again.", Toast.LENGTH_SHORT).show();
        });
    }


    private void showNearbyGroceryStores(Location location) {
        String apiKey = "AIzaSyDwXvYsGKTh2JU-C4jLRxslfdaZgwgjCHU";
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + location.getLatitude() + "," + location.getLongitude() +
                "&radius=5000" +
                "&type=grocery_or_supermarket" +
                "&key=" + apiKey;

        // Use Volley to make a network request
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            JSONObject geometry = place.getJSONObject("geometry");
                            JSONObject placeLocationObj = geometry.getJSONObject("location");

                            double lat = placeLocationObj.getDouble("lat");
                            double lng = placeLocationObj.getDouble("lng");
                            String name = place.getString("name");

                            // Add marker to map
                            LatLng placeLocation = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions().position(placeLocation).title(name));
                        }
                    } catch (Exception e) {
                        Log.e("PlacesError", "Error parsing places data: " + e.getMessage());
                    }
                },
                error -> Log.e("VolleyError", "Failed to fetch places: " + error.getMessage())
        );

        queue.add(request);
    }
}
