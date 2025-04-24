package com.example.busmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private final int LOCATION_PERMISSION_REQUEST = 100;
    private TextView durationTextView;

    // Bus routes with start and end coordinates
    private final Map<String, List<LatLng>> busRoutes = new HashMap<String, List<LatLng>>() {{
        // Route 52A: Liverpool to Magdalene Square
        put("52A", Arrays.asList(
                new LatLng(53.4084, -2.9916), // Liverpool
                new LatLng(53.4190, -2.9500)  // Magdalene Square
        ));
        // Route 7: Liverpool to Warrington Bus Interchange
        put("7", Arrays.asList(
                new LatLng(53.4084, -2.9916),
                new LatLng(53.3880, -2.5970)
        ));
        // Route 86A: Liverpool One to John Lennon Airport
        put("86A", Arrays.asList(
                new LatLng(53.4032, -2.9916),
                new LatLng(53.3371, -2.8540)
        ));
        // Route 10A: Liverpool One to St Helens Bus Station
        put("10A", Arrays.asList(
                new LatLng(53.4032, -2.9916),
                new LatLng(53.4539, -2.7360)
        ));
        // Route 1: Liverpool to Chester
        put("1", Arrays.asList(
                new LatLng(53.4084, -2.9916),
                new LatLng(53.1905, -2.8919)
        ));
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the route spinner and the duration TextView
        Spinner routeSpinner = findViewById(R.id.routeSpinner);
        durationTextView = findViewById(R.id.durationTextView);

        // Create an ArrayAdapter to populate the spinner with bus route names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, busRoutes.keySet().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(adapter);

        // Initialize the map fragment and set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up listener for when a route is selected in the spinner
        routeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                // Get the selected route and draw it on the map
                String route = (String) parent.getItemAtPosition(position);
                drawBusRoute(route);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableUserLocation();
    }

    // Enable the user's location on the map
    private void enableUserLocation() {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return;
        }

        // Enable location tracking on the map
        mMap.setMyLocationEnabled(true);

        // Get the user's last known location and move the camera to that location
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12));
            }
        });
    }

    // Draw the selected bus route on the map
    private void drawBusRoute(String routeName) {
        mMap.clear();  // Clear any existing routes or markers
        durationTextView.setText("");  // Reset the duration text

        // Get the bus route from the map based on the selected route name
        List<LatLng> stops = busRoutes.get(routeName);

        // If the route exists and has at least 2 stops, draw the route
        if (stops != null && stops.size() >= 2) {
            // Add markers for the start and end of the route
            mMap.addMarker(new MarkerOptions().position(stops.get(0)).title("Start: " + routeName));
            mMap.addMarker(new MarkerOptions().position(stops.get(1)).title("End: " + routeName));

            // Draw a polyline between the stops to represent the bus route
            mMap.addPolyline(new PolylineOptions()
                    .addAll(stops)
                    .width(8)
                    .color(android.graphics.Color.BLUE));

            // Move the camera to the start point of the route
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stops.get(0), 11));

            // Estimate the journey duration based on the straight-line distance
            float[] results = new float[1];
            Location.distanceBetween(
                    stops.get(0).latitude, stops.get(0).longitude,
                    stops.get(1).latitude, stops.get(1).longitude,
                    results);
            float distanceKm = results[0] / 1000;
            float estimatedTime = distanceKm / 30 * 60; // Assuming 30 km/h average speed
            String durationText = String.format("Estimated Duration: %.0f minutes", estimatedTime);
            durationTextView.setText(durationText);  // Display the estimated duration
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Handle location permission request results
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();  // If permission granted, enable user location
            } else {
                // Show a toast if permission is denied
                Toast.makeText(this, "Location permission is required to show your location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
