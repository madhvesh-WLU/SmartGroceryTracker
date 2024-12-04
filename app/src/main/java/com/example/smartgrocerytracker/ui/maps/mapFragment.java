package com.example.smartgrocerytracker.ui.maps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri; // Import for Uri
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.Place;
import com.example.smartgrocerytracker.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory; // Import for marker colors
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class mapFragment extends Fragment implements OnMapReadyCallback {
    // Request codes
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int SETTINGS_REQUEST_CODE = 101;
    private static final String TAG = "SlideshowFragment";
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private List<Place> placeList = new ArrayList<>(); // Updated to use Place objects

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

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
                TextView nameTextView = itemView.findViewById(R.id.place_name);
                TextView addressTextView = itemView.findViewById(R.id.place_address);
                TextView statusTextView = itemView.findViewById(R.id.place_status);
                TextView distanceTextView = itemView.findViewById(R.id.place_distance);
                TextView estTimeTextView = itemView.findViewById(R.id.place_est_time);

                Place place = placeList.get(position);
                nameTextView.setText(place.name);
                addressTextView.setText(place.address);
                statusTextView.setText(place.status);
                distanceTextView.setText(place.distance);
                estTimeTextView.setText(place.estTime);

                // Set the color of the status text based on the status
                if (place.status.equalsIgnoreCase("Open")) {
                    statusTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
                } else if (place.status.equalsIgnoreCase("Closed")) {
                    statusTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
                } else {
                    // Default color
                    statusTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.black));
                }


                // Set OnClickListener to open Google Maps with directions
                itemView.setOnClickListener(v -> {
                    String uri = "google.navigation:q=" + place.latitude + "," + place.longitude;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(requireContext(), "Google Maps not installed.", Toast.LENGTH_SHORT).show();
                    }
                });
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

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            requestLocationPermission();
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private void requestLocationPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show an explanation to the user
            new AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app requires location access to display nearby stores on the map.")
                    .setPositiveButton("Grant", (dialog, which) -> {
                        // Request the permission
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                        Toast.makeText(requireContext(), "Location permission is required to use this feature.", Toast.LENGTH_SHORT).show();
                    })
                    .create()
                    .show();
        } else {
            // No explanation needed; request the permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                enableUserLocation();
            } else {
                // Permission denied
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                if (!showRationale) {
                    // User selected "Don't ask again"
                    showSettingsRedirectDialog();
                } else {
                    // User denied without "Don't ask again"
                    Toast.makeText(requireContext(), "Location permission denied. Some features may not work.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showSettingsRedirectDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Permission Required")
                .setMessage("Location permission has been permanently denied. Please enable it in the app settings to use this feature.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    // Redirect to app settings
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Location permission denied. Some features may not work.", Toast.LENGTH_SHORT).show();
                })
                .create()
                .show();
    }

    private void fetchNearbyStores(Location location) {
        String apiKey = "AIzaSyDwXvYsGKTh2JU-C4jLRxslfdaZgwgjCHU"; // Replace with your actual API key

        // Fixed location: Waterloo coordinates
        double waterlooLat = 43.4643;
        double waterlooLng = -80.5204;

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("place")
                .appendPath("nearbysearch")
                .appendPath("json")
                .appendQueryParameter("location",  waterlooLat+ "," + waterlooLng)
                .appendQueryParameter("radius", "1000000")
                .appendQueryParameter("type", "store")
                .appendQueryParameter("name", "Walmart FreshCo Costco")
                .appendQueryParameter("key", apiKey);

        String url = uriBuilder.build().toString();

        Log.d(TAG, "Requesting nearby stores with URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        placeList.clear();

                        // Optional: Clear existing markers if needed
                        // mMap.clear();

                        // Re-add user's location marker if necessary
                        // LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        // mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject placeJson = results.getJSONObject(i);
                            String name = placeJson.getString("name");

                            // Convert name to lowercase for case-insensitive comparison
                            String lowerCaseName = name.toLowerCase();
                            if (lowerCaseName.contains("walmart") || lowerCaseName.contains("freshco") || lowerCaseName.contains("costco")) {
                                // Extract other details
                                String address = placeJson.optString("vicinity", "Unknown Address");
                                boolean openNow = placeJson.optJSONObject("opening_hours") != null &&
                                        placeJson.getJSONObject("opening_hours").optBoolean("open_now", false);
                                String status = openNow ? "Open" : "Closed";

                                // Get latitude and longitude
                                JSONObject locationObj = placeJson.getJSONObject("geometry").getJSONObject("location");
                                double latitude = locationObj.getDouble("lat");
                                double longitude = locationObj.getDouble("lng");

                                // Calculate actual distance
                                float[] resultsDistance = new float[1];
                                Location.distanceBetween(location.getLatitude(), location.getLongitude(), latitude, longitude, resultsDistance);
                                double distanceInMiles = resultsDistance[0] / 1609.34; // Convert meters to miles
                                String distStr = String.format("%.1f mi", distanceInMiles);

                                int estTime = (int) (distanceInMiles * 5); // Approximate travel time

                                Place place = new Place(name, address, status, distStr, estTime + " min", latitude, longitude);
                                placeList.add(place);

                                // **Add marker to map**
                                if (mMap != null) {
                                    LatLng placeLocation = new LatLng(latitude, longitude);
                                    mMap.addMarker(new MarkerOptions()
                                            .position(placeLocation)
                                            .title(name)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))); // Red marker
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();

                        if (placeList.isEmpty()) {
                            Toast.makeText(requireContext(), "No nearby stores found.", Toast.LENGTH_SHORT).show();
                        }

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