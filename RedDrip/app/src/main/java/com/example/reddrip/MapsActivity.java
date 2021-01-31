package com.example.reddrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String INTENT_FILTER = "HHHH";
    private GoogleMap mMap;
    private String locationProvider;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location mLocation;
    public static final int PERMISSIONS_REQUEST = 0;
    private Address foundAdresse = null;
    private String myadd;


    private EditText Elocation;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final float DEFAULT_ZOOM = 15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d(TAG, "Map created");
        locationProvider = LocationManager.GPS_PROVIDER;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        //listen for location changes on user's device and set up the search bar text and the location parameters
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the location provider
                Log.d(TAG, "Location changed");
                mLocation = location;
                if (mMap != null) {
                    updateMap();
                    Elocation = findViewById(R.id.locate);
                    List<Address> addresses = new ArrayList<>();

                    try {
                        addresses = new Geocoder(getApplicationContext(), Locale.getDefault()).getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                        Address a = addresses.get(0);
                        Elocation.setText(a.getAddressLine(0));
                        myadd = a.getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        Log.d(TAG, "Activity created");
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Remove the listener you previously added
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

    }


    //Update the map when it's ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Elocation = findViewById(R.id.locate);
        Elocation.requestFocus();
        Log.d(TAG, "Map initialized");

        mMap = googleMap;
        updateMap();

    }

    private void updateMap() {
        Log.d(TAG, "Updating map...");
        if (mMap != null) {
            mMap.clear();
            if (mLocation == null) {
                mLocation = new Location(locationProvider);
                mLocation.setLatitude(0);
                mLocation.setLongitude(0);
            }
            LatLng pin = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pin));
            mMap.addMarker(new MarkerOptions().position(pin).title("Current location"));

        }
    }

    @Override

    //request for user permession then for location updates at the start of the activity
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {

                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted, yay! Do something useful
                    requestLocationUpdates();

                } else {

                    // Permission was denied, boo! Disable the
                    // functionality that depends on this permission
                    Toast.makeText(this, "Permission denied to access device's location", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }


    //get the user location if the permession is granted
    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    locationProvider, 0, 1, locationListener);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);

        }
    }


    // search the location that the user entered in the search bar
    public void search(View view) {
        Elocation = findViewById(R.id.locate);
        if (TextUtils.isEmpty(Elocation.getText())) {
            Elocation.setError("Vueillez d'abord saisir un mot ici (code postale, rue..)!");
        } else {
            String loc = Elocation.getText().toString();
            Elocation.clearComposingText();
            Toast a = Toast.makeText(MapsActivity.this, loc, Toast.LENGTH_SHORT);
            a.show();
            geoLocate(loc);
        }
    }

    //making location from user input
    private void geoLocate(String adresse) {
        Log.d(TAG, "Geocoding ....");
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(adresse, 1);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }

        if (list.size() > 0) {
            this.foundAdresse = list.get(0);
            Toast.makeText(this, this.foundAdresse.getAddressLine(0), Toast.LENGTH_SHORT).show();
            Log.d(TAG, this.foundAdresse.toString());
            moveCamera(new LatLng(this.foundAdresse.getLatitude(), this.foundAdresse.getLongitude()), DEFAULT_ZOOM, this.foundAdresse.getAddressLine(0));
        }


    }

    //moving the camera according to the location found
    private void moveCamera(LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moving to " + latLng.latitude + ", " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);


    }

    //passing the adress to the requesting activity
    public void setAdresse(View view) {
        Intent intent = new Intent();
        if (this.foundAdresse != null) {
            intent.putExtra("adresse", this.foundAdresse.getAddressLine(0));
        } else {
            intent.putExtra("adresse", myadd);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}