package com.example.reddrip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Recherche extends AppCompatActivity {
    private static final String TAG = MapsActivity.class.getSimpleName();
    public int ADRESSE_FOR_RECHERCHE = 365;
    private String adresse;
    private RadioGroup sexeGrop;
    private RadioButton sexeChoix;
    private RadioGroup typeGrop;
    private RadioButton typeChoix;
    private EditText ageChoix;
    private EditText adresseChoix;
    private EditText descripChoix;
    private String email;
    private String nom;
    private String prenom;
    private String num_tel;
    private String type;
    static long maxid = 0;
    Spinner s;
    DatabaseReference reference;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        email = sharedPreferences.getString("email","");
        nom = sharedPreferences.getString("nom","");
        prenom = sharedPreferences.getString("prenom","");
        num_tel = sharedPreferences.getString("num","");
        s = findViewById(R.id.sang);
        adapter = ArrayAdapter.createFromResource(this, R.array.Gs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"resut");
        if (requestCode == 365){
            Log.d(TAG,"result mine");
            if (resultCode == Activity.RESULT_OK){
                Log.d(TAG,"resultok");
                String adresse = data.getStringExtra("adresse");
                EditText edit = (EditText) findViewById(R.id.adre);
                edit.setText(adresse);
                Log.d(TAG,adresse.toString());
            }else {
                Log.d(TAG,"not ok");
            }
        }
    }

    public void monAdresse(View view) {
        Log.d(TAG, "clicked");
        Intent intent = new Intent(this, MapsActivity.class);
        startActivityForResult(intent,365);
    }

    public void poster(View view) {
        ageChoix = findViewById(R.id.ag);
        adresseChoix = findViewById(R.id.adre);
        descripChoix = findViewById(R.id.desc);
        sexeGrop = findViewById(R.id.radiosex);
        int selected = sexeGrop.getCheckedRadioButtonId();
        sexeChoix = (RadioButton) findViewById(selected);
        typeGrop = findViewById(R.id.typeGroupe);
        int choised = typeGrop.getCheckedRadioButtonId();
        typeChoix = (RadioButton) findViewById(choised);

        s = findViewById(R.id.sang);

        if (sexeGrop.getCheckedRadioButtonId() == -1){
            Toast.makeText(this,"Vuillez indiquer votre sexe",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(ageChoix.getText())){
            ageChoix.setError("Introduire votre age svp");
        }else if(TextUtils.isEmpty(adresseChoix.getText())){
            adresseChoix.setError("Choisissez l'adresse de don");
        }else if (TextUtils.isEmpty(descripChoix.getText())){
            descripChoix.setError("Veuillez ajouter une petite description");
        }else {


            int age = Integer.parseInt(ageChoix.getText().toString());
            String adresse = adresseChoix.getText().toString();
            String sexe = sexeChoix.getText().toString();
            String description = descripChoix.getText().toString();
            String sang = s.getSelectedItem().toString().trim();
            String type = typeChoix.getText().toString();


            reference = FirebaseDatabase.getInstance().getReference().child("Annonce");
            Query query = reference.orderByChild("email");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        maxid = (snapshot.getChildrenCount());
                    Annonce annonce = new Annonce(email,num_tel,nom,prenom,sang,adresse,type,sexe,description,age);
                    Log.d("maxid", String.valueOf(maxid) + 1);
                    reference.child(String.valueOf(maxid + 1)).setValue(annonce);
                    return;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




            Toast.makeText(this,"Votre annonce a bien été enregistrée!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this,Acceuil.class);
            startActivity(intent);
            finish();
        }
    }

    public void annule(View view) {
        Intent intent = new Intent(this,Acceuil.class);
        startActivity(intent);
        finish();
    }
}