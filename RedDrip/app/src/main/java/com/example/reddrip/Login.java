package com.example.reddrip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Map;

public class Login extends AppCompatActivity {
    //Elements layout
    Button btnConnexion;
    EditText etEmail, etPassword;
    TextView tvRedirect;

    //Shared preferences
    SharedPreferences preferences;


    //Variables intent
    String nomFromDatabase;
    String prenomFromDatabase;
    String emailFromDataBase;
    String passwordFromDatabase;
    String numFromDatabase;
    String adresseFromDatabase;
    String gsFromDataBase;

    //Variables layout
    String userEnteredEmail;
    String userEnteredPassword;

    //Database
    DatabaseReference reference;
    Query checkUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        etEmail = findViewById(R.id.etLoginEmail);
        tvRedirect = findViewById(R.id.tvLoginRedirectRegister);
        etPassword = findViewById(R.id.etLoginPassword);
        btnConnexion = findViewById(R.id.btnConnexion);
        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEnteredEmail = etEmail.getText().toString().trim();
                userEnteredPassword = etPassword.getText().toString().trim();
                reference = FirebaseDatabase.getInstance().getReference("User");
                //We use a query to select a line from the database where the entered email address matches
                //with the emails saved into our database
                checkUser = reference.orderByChild("email").equalTo(userEnteredEmail);

                checkUser.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        //if the data snapshot exists it means that the email address exists into our database
                        //So we have to check if the password is correct
                        if (snapshot.exists()) {
                            etEmail.setError(null);
                            //We get the data we got from the database and save it into a map
                            Map<String, String> map = (Map<String, String>) snapshot.getValue();
                            passwordFromDatabase = map.get("password");
                            if (passwordFromDatabase.equals(userEnteredPassword)) {
                                nomFromDatabase = map.get("nom");
                                prenomFromDatabase = map.get("prenom");
                                emailFromDataBase = map.get("email");
                                passwordFromDatabase = map.get("password");
                                gsFromDataBase = map.get("gs");
                                numFromDatabase = map.get("numTel");
                                adresseFromDatabase = map.get("adresse");

                                //We put the information of the user in sharedPreferences so we can use them when starting tha application for a second time
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("nom", nomFromDatabase);
                                editor.putString("prenom", prenomFromDatabase);
                                editor.putString("email", emailFromDataBase);
                                editor.putString("password", passwordFromDatabase);
                                editor.putString("num", numFromDatabase);
                                editor.putString("adresse", adresseFromDatabase);
                                editor.putString("gs", gsFromDataBase);
                                editor.commit();


                                //We put the information of the user in an intent to send extras to Acceuil activity
                                Intent redirectLoginAcceuil = new Intent(Login.this, Acceuil.class);
                                redirectLoginAcceuil.putExtra("email", emailFromDataBase);
                                redirectLoginAcceuil.putExtra("password", passwordFromDatabase);
                                redirectLoginAcceuil.putExtra("nom", nomFromDatabase);
                                redirectLoginAcceuil.putExtra("prenom", prenomFromDatabase);
                                redirectLoginAcceuil.putExtra("gs", gsFromDataBase);
                                redirectLoginAcceuil.putExtra("num", numFromDatabase);
                                redirectLoginAcceuil.putExtra("adresse", adresseFromDatabase);
                                startActivity(redirectLoginAcceuil);
                            } else {
                                //If the password is incorrect we set an error message into the
                                //Edit text
                                etPassword.setError("Mot de passe incorrect");
                                etPassword.requestFocus();
                            }

                        } else {
                            //If the snapshot is empty it means that user using the entered email address
                            //isn't registered yet
                            etEmail.setError("Utilisateur inexistant");
                            etEmail.requestFocus();
                        }
                    }


                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        System.out.println("onChildChanged");

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        System.out.println("onChildRemoved");

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        System.out.println("onChildMoved");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("onCancelled");
                    }
                });

            }
        });

        //clickable text view redirect us to register page if needed
        tvRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirectLoginRegister = new Intent(Login.this, Register.class);
                startActivity(redirectLoginRegister);

            }
        });
    }


}