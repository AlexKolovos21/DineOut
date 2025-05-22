package com.example.dineout.ui.screens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.dineout.R;
import com.example.dineout.data.Restaurant;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;
    private FloatingActionButton myLocationButton;
    private List<RestaurantLocation> restaurantLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize my location button
        myLocationButton = findViewById(R.id.my_location_button);
        myLocationButton.setOnClickListener(v -> {
            if (lastKnownLocation != null) {
                LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            } else {
                Toast.makeText(this, R.string.location_not_available, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableMyLocation();
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            lastKnownLocation = location;
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));
                            addRestaurantMarkers(currentLocation);
                        }
                    });
        }
    }

    private void addRestaurantMarkers(LatLng centerLocation) {
        // Athens city center coordinates
        LatLng athensCenter = new LatLng(37.9838, 23.7275);
        
        // List of restaurant locations in Athens
        restaurantLocations = new ArrayList<>();
        restaurantLocations.add(new RestaurantLocation(
            "The Italian Place",
            new LatLng(37.9785, 23.7321), // Plaka area
            "Authentic Italian cuisine"
        ));
        restaurantLocations.add(new RestaurantLocation(
            "Sushi Master",
            new LatLng(37.9755, 23.7345), // Syntagma area
            "Fresh and delicious sushi"
        ));
        restaurantLocations.add(new RestaurantLocation(
            "Burger Joint",
            new LatLng(37.9812, 23.7298), // Monastiraki area
            "Best burgers in town"
        ));
        restaurantLocations.add(new RestaurantLocation(
            "Greek Taverna",
            new LatLng(37.9768, 23.7312), // Psiri area
            "Traditional Greek cuisine"
        ));
        restaurantLocations.add(new RestaurantLocation(
            "Seafood Paradise",
            new LatLng(37.9795, 23.7335), // Thissio area
            "Fresh seafood and Mediterranean dishes"
        ));

        // Add markers for each restaurant
        for (RestaurantLocation restaurant : restaurantLocations) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(restaurant.location)
                    .title(restaurant.name)
                    .snippet(restaurant.description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            marker.setTag(restaurant);
        }

        // Move camera to Athens center
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(athensCenter, 14));
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null; // Use default info window background
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.marker_info_window, null);
        
        RestaurantLocation restaurant = (RestaurantLocation) marker.getTag();
        if (restaurant == null) return null;

        TextView nameText = view.findViewById(R.id.restaurant_name);
        TextView descriptionText = view.findViewById(R.id.restaurant_description);
        TextView distanceText = view.findViewById(R.id.distance_text);
        Button navigateButton = view.findViewById(R.id.navigate_button);

        nameText.setText(restaurant.name);
        descriptionText.setText(restaurant.description);

        if (lastKnownLocation != null) {
            float[] results = new float[1];
            Location.distanceBetween(
                lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(),
                restaurant.location.latitude, restaurant.location.longitude,
                results
            );
            float distanceInKm = results[0] / 1000;
            distanceText.setText(getString(R.string.distance_format, distanceInKm));
        } else {
            distanceText.setText(R.string.no_distance_available);
        }

        navigateButton.setOnClickListener(v -> {
            String uri = String.format("google.navigation:q=%f,%f",
                    restaurant.location.latitude, restaurant.location.longitude);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            
            // Try to start Google Maps navigation
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // If Google Maps is not installed, open in browser
                String browserUri = String.format("https://www.google.com/maps/dir/?api=1&destination=%f,%f",
                        restaurant.location.latitude, restaurant.location.longitude);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(browserUri));
                if (browserIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(this, R.string.no_navigation_app, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class RestaurantLocation {
        String name;
        LatLng location;
        String description;

        RestaurantLocation(String name, LatLng location, String description) {
            this.name = name;
            this.location = location;
            this.description = description;
        }
    }
} 