package com.example.reddrip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.util.Patterns;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.widget.AdapterView.OnItemSelectedListener;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Button btnSubmit, btnMaps;
    private EditText etNom, etPrenom, etAdresse, etmail, etPassword, etNum;
    private TextView tvRedirect;
    DatabaseReference ref;
    SharedPreferences preferences = null;
    String[] errors;
    public int ADRESSE_FOR_RECHERCHE = 1;
    private static final String TAG = Register.class.getSimpleName();
    //Patterns for ids verifications
    public static final Pattern EMAIL_ADDRESS = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    //The maxid is the id of the instance in our firebase databse this
    //variable is initilised at 0 and when an instance is added this variable
    //Get an incrementation, so the id of the first instance will be 1
    long maxid = 0;
    Spinner s;
    ArrayAdapter<CharSequence> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        s = findViewById(R.id.spinnerRegisterGs);
        tvRedirect = findViewById(R.id.tvRegisterRedirectLogin);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        adapter = ArrayAdapter.createFromResource(this, R.array.Gs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        //Notifications from firebase that say welcome when a new user is registered
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        // Log and toast
                        Log.d(TAG, token);
                        Toast.makeText(Register.this, "Notifications activées ", Toast.LENGTH_SHORT).show();
                    }
                });

        etNom = findViewById(R.id.etRegisterNom);
        etPrenom = findViewById(R.id.etRegisterPrenom);
        etmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterMdp);
        etNum = findViewById(R.id.etRegisterNum);
        etAdresse = findViewById(R.id.etRegisterAdresse);
        s = findViewById(R.id.spinnerRegisterGs);
        btnSubmit = findViewById(R.id.btnSubmit);
        ref = FirebaseDatabase.getInstance().getReference().child("User");
        //When a user is added, we get the id of the last user registered and put the value into maxid
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    maxid = (snapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //When submit button's clicked we get data from edit texts and put them into variables
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nom = etNom.getText().toString().trim();
                String prenom = etPrenom.getText().toString().trim();
                String email = etmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String num = etNum.getText().toString().trim();
                String adresse = etAdresse.getText().toString().trim();
                String gs;
                gs = s.getSelectedItem().toString().trim();
                SharedPreferences.Editor editor = preferences.edit();

                //We do a verification before putting data into our firebase database
                //If it's all good we send data to firebase
                if (validateEmail() && validateAdresse() && validateNom() && validatePassword() && validatePrenom() && validatePhoneNumber()) {
                    User usr = new User(nom, prenom, num, email, password, gs, adresse);
                    ref.child(String.valueOf(maxid + 1)).setValue(usr);
                    editor.putString("nom", nom);
                    editor.putString("prenom", prenom);
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.putString("num", num);
                    editor.putString("adresse", adresse);
                    editor.putString("gs", gs);
                    editor.commit();
                    Toast.makeText(Register.this, "Utilisateur enregistré", Toast.LENGTH_SHORT).show();
                    Toast.makeText(Register.this, "Données inserées", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Register.this, Acceuil.class);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("nom", nom);
                    intent.putExtra("prenom", prenom);
                    intent.putExtra("gs", gs);
                    intent.putExtra("num", num);
                    intent.putExtra("adresse", adresse);
                    startActivity(intent);
                }


            }
        });

        //clickable text view redirect us to login page if needed
        tvRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRedirectLogin = new Intent(Register.this, Login.class);
                startActivity(intentRedirectLogin);
            }
        });

        etAdresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, MapsActivity.class);
                startActivity(i);
            }
        });

        etAdresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Register.this, MapsActivity.class);
                startActivityForResult(i, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "resut");
        if (requestCode == 1) {
            Log.d(TAG, "result mine");
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "resultok");
                String adresse = data.getStringExtra("adresse");
                EditText edit = (EditText) findViewById(R.id.etRegisterAdresse);
                edit.setText(adresse);
                Log.d(TAG, adresse.toString());
            } else {
                Log.d(TAG, "not ok");
            }
        }
    }

    private boolean validateAdresse() {
        String adresse = etAdresse.getText().toString().trim();
        if (adresse.isEmpty()) {
            etAdresse.setError("L'adresse est requise");
            return false;
        } else
            return true;

    }

    private boolean validatePhoneNumber() {
        String num = etNum.getText().toString().trim();

        String numPattern = "^[0-9]{10,13}$";
        if (num.isEmpty()) {
            etNum.setError("Le numéro de téléphone est requis");
            return false;
        } else if (!num.matches(numPattern)) {
            etNum.setError("Syntaxe invalide");
            return false;
        } else
            return true;
    }

    private boolean validateNom() {
        String nom = etNom.getText().toString().trim();

        if (nom.isEmpty()) {
            etNom.setError("Le nom ne peut être vide");
            return false;
        } else
            return true;
    }

    private boolean validatePrenom() {
        String nom = etPrenom.getText().toString().trim();

        if (nom.isEmpty()) {
            etNom.setError("Le prenom ne peut être vide");
            return false;
        } else
            return true;
    }

    private boolean validateEmail() {
        String emailInput = etmail.getText().toString().trim();
        if (emailInput.isEmpty()) {
            etmail.setError("Veuillez renseigner ce champ");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            etmail.setError("Adresse email invalide");
            return false;
        } else {
            etmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String passwordInput = etPassword.getText().toString().trim();
        if (passwordInput.isEmpty()) {
            etPassword.setError("Mot de passe doit être renseigné");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            etPassword.setError("Mot de passe faible");
            return false;
        } else {
            etPassword.setError(null);
            return true;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        long text = parent.getItemIdAtPosition(position);
        String text1 = text + "";
        Toast.makeText(parent.getContext(), text1, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void next(View view) {
    }
}
