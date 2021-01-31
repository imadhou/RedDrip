package com.example.reddrip;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AnnoncesProches extends IntentService implements LocationListener{

    private static final String CHANEL_ID = AnnoncesProches.class.toString();
    DatabaseReference reference;
    private String locationProvider;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Address mAddress;
    List<Annonce> annonceDons;
    Location mLocation;
    int k =0;
    String email;

    public AnnoncesProches() {
        super("AnnoncesProches");
        setIntentRedelivery(true);
    }


    @Override
    protected void onHandleIntent(Intent intent){


        //requesting for location updates every day and when the user moves 10km away from his last known location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 86400000, 10000, (LocationListener) this);

        if (intent != null){
            // getting the sanguine group of the connected user and getting a reference to the database

            String sang = intent.getStringExtra("sang");
            reference = FirebaseDatabase.getInstance().getReference("Annonce");

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
             email = sharedPreferences.getString("email","");

            //making a query to get all the announces that match user's sanguine group
            Query query = reference.orderByChild("gs").equalTo(sang);
            annonceDons = new ArrayList<>();

            //listening for database changes
            query.addListenerForSingleValueEvent(valueEventListener);
        }
    }



    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            annonceDons.clear();
            if (snapshot.exists()){

                //getting the addresses of the announcers that are blood receivers
                int i =0;
                List<DataSnapshot> adresses = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    if (snapshot1.child("type").getValue().toString().equals("Recepteur")){
                        if (!snapshot1.child("email").getValue().equals(email)){
                            adresses.add(i,snapshot1.child("adresse"));
                            i++;
                        }
                    }
                }
                k = i;


                List<Address> addressList = new ArrayList<>();
                List<List> lesadd = new ArrayList<>();
                Geocoder geocoder = new Geocoder(AnnoncesProches.this);



                //getting the correct address lines with the geocoder
                for (int h = 0; h < adresses.size(); h++){
                    String b = adresses.get(h).getValue().toString();
                    try {
                        addressList = geocoder.getFromLocationName(b,1);
                        lesadd.add(h,addressList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //Getting the locations (lat-long) from each address line
                List<Location> locations = new ArrayList<>();
                for (int c = 0; c < lesadd.size(); c++){
                    Address ab = (Address) lesadd.get(c).get(0);
                    Location tmp = new Location(LocationManager.GPS_PROVIDER);
                    tmp.setLatitude(ab.getLatitude());
                    tmp.setLongitude(ab.getLongitude());
                    locations.add(c,tmp);
                }


                //Getting the count of users that search blood that have the same sg as the user
                //and that are in max 10 km away from the user
                int numberDist = 0;
                for (int q = 0; q < locations.size(); q++){
                    float distance = mLocation.distanceTo(locations.get(q));
                    if (distance < 10000){
                        numberDist ++ ;
                    }
                }

                Log.d("numbdistan", String.valueOf(numberDist));

                //displaying a notification with an intent to the AccueilActivity if the count is more than 0

                if (numberDist != 0){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        NotificationChannel channel = new NotificationChannel("My not", "My not", NotificationManager.IMPORTANCE_HIGH);
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }


                    Intent intent2 = new Intent(AnnoncesProches.this,Acceuil.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(AnnoncesProches.this,"My not");
                    builder.setContentTitle("RedDrip");
                    builder.setContentText(numberDist+" personnes proches de chez vous cherchent du sang");
                    builder.setSmallIcon(R.drawable.logo);
                    builder.setAutoCancel(true);
                    builder.setContentIntent(PendingIntent.getActivity(AnnoncesProches.this,0,intent2,PendingIntent.FLAG_UPDATE_CURRENT));
                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(AnnoncesProches.this);
                    managerCompat.notify(1, builder.build());
                }



            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String myAddress = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
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


}