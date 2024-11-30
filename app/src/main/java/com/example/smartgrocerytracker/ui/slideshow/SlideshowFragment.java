package com.example.smartgrocerytracker.ui.slideshow;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

public class SlideshowFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "SlideshowFragment";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<String[]> placeList = new ArrayList<>();

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

        // Initialize RecyclerView
        recyclerView = root.findViewById(R.id.nearby_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_item, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                View itemView = holder.itemView;
                TextView name = itemView.findViewById(R.id.place_name);
                TextView address = itemView.findViewById(R.id.place_address);
                TextView status = itemView.findViewById(R.id.place_status);
                TextView distance = itemView.findViewById(R.id.place_distance);
                TextView estTime = itemView.findViewById(R.id.place_est_time);

                String[] place = placeList.get(position);
                name.setText(place[0]);     // Name
                address.setText(place[1]);  // Address
                status.setText(place[2]);   // Open/Closed
                distance.setText(place[3]); // Distance
                estTime.setText(place[4]);  // Estimated time
            }

            @Override
            public int getItemCount() {
                Log.d(TAG, "RecyclerView item count: " + placeList.size());
                return placeList.size();
            }
        };
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));

                // Fetch and display nearby stores
                fetchNearbyStores(location);
            } else {
                Log.e(TAG, "Failed to retrieve location.");
                Toast.makeText(requireContext(), "Location not available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNearbyStores(Location location) {
        String apiKey = "AIzaSyDwXvYsGKTh2JU-C4jLRxslfdaZgwgjCHU"; // Replace with your API key
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + location.getLatitude() + "," + location.getLongitude() +
                "&radius=5000" +

                "&type=grocery_or_supermarket" + // You can change to "store" or "food" for broader results
                "&key=" + apiKey;

        Log.d(TAG, "Requesting nearby stores with URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d(TAG, "API Response: " + response.toString());
                        JSONArray results = response.getJSONArray("results");
                        placeList.clear();
                        Log.d(TAG, "Number of places fetched: " + results.length());
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            String name = place.getString("name");
                            String address = place.optString("vicinity", "Unknown Address");
                            boolean openNow = place.optJSONObject("opening_hours") != null &&
                                    place.getJSONObject("opening_hours").optBoolean("open_now", false);
                            String status = openNow ? "Open" : "Closed";

                            double distance = Math.random() * 5; // Mock distance for now
                            String distStr = String.format("%.1f mi", distance);

                            int estTime = (int) (distance * 5); // Mock: 5 min/mile
                            placeList.add(new String[]{name, address, status, distStr, estTime + " min"});
                        }

                        if (placeList.isEmpty()) {
                            Log.w(TAG, "No places found.");
                            Toast.makeText(requireContext(), "No nearby stores found.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Places added to RecyclerView: " + placeList.size());
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing places: " + e.getMessage());
                        Toast.makeText(requireContext(), "Error parsing store data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Request failed: " + error.getMessage());
                    Toast.makeText(requireContext(), "Failed to fetch stores.", Toast.LENGTH_SHORT).show();
                });
        queue.add(request);
    }
}
