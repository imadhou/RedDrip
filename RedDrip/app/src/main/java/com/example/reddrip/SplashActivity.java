package com.example.reddrip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    private final static int SPLASH_SCREEN_TIMEOUT = 3000;
    SharedPreferences preferences = null;
    DatabaseReference reference;
    Query checkUser;
    String savedEmail;
    String savedPassword;
    Query check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //We get the default shared preferences and we put the values of the saved email and password into variables

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        reference = FirebaseDatabase.getInstance().getReference("User");
        checkUser = reference.orderByChild("email").equalTo(savedEmail);
        savedEmail = preferences.getString("email", "");
        savedPassword = preferences.getString("password", "");
        check = reference.orderByChild("email").equalTo(savedEmail);

        //Here we start the service
        Intent intent = new Intent(this, AnnoncesProches.class);
        String sang = preferences.getString("gs", "");
        intent.putExtra("sang", sang);
        startService(intent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //If the shared preferences exist we redirect the user to "Acceuil" activity using his saved email and password
                //Else we redirect him to the the Login activity
                if (savedEmail.trim().equals("") || savedPassword.trim().equals("")) {
                    System.out.println("Breakpoint");
                    Intent firstTime = new Intent(SplashActivity.this, Login.class);
                    startActivity(firstTime);
                    finish();

                } else {
                    Intent redirectRegister = new Intent(SplashActivity.this, Acceuil.class);
                    startActivity(redirectRegister);
                    finish();
                }
            }
        }, 3000);

    }
}

