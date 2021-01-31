package com.example.reddrip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Acceuil extends AppCompatActivity {
    TextView TVDateDeNaissance;
    TextView TVGroupeSanguin;
    Button btnPrf;
    Button btndonner;
    Button btnchercher;
    DatabaseReference reference;
    String num;

    LinearLayout layoutList;
    Button deconnexion;

    private  static  final int REQUEST_CALL =1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil);
        btnPrf = findViewById(R.id.btnProfile);
        btndonner = findViewById(R.id.btndonner);
        deconnexion = findViewById(R.id.deconnexion);
        layoutList = findViewById(R.id.layout_list_accueil);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String email = sharedPreferences.getString("email","");

        // Getting the reference for the table Annonce in our database
        reference = FirebaseDatabase.getInstance().getReference().child("Annonce");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // We clear all the views when the data is changed
                layoutList.removeAllViews();

                // here we get each object in the table and we inflat our layout with the generated view
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Annonce annonceDon = snapshot1.getValue(Annonce.class);
                    if(!annonceDon.getEmail().equals(email)) {
                    addView(annonceDon.getNom(),annonceDon.getSexe(),annonceDon.getGs(),annonceDon.getAge(),annonceDon.getEmail(),annonceDon.getDescription(),annonceDon.getNum_tel(),annonceDon.getAdresse(),annonceDon.getType().equals("Donnateur"));
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        Intent i = getIntent();
        String password = i.getStringExtra("password");
        String nom = i.getStringExtra("nom");
        String prenom = i.getStringExtra("prenom");
        String gs = i.getStringExtra("gs");
        String num = i.getStringExtra("num");
        String adresse = i.getStringExtra("adresse");


        btnPrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Acceuil.this, UserProfile.class);
                startActivity(intent);
            }
        });

        // When cicked this button start Recherche Activity
        btndonner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Acceuil.this, Recherche.class);
                startActivity(intent);
            }
        });

        // When cicked this button start Login Activity and delete the informations in sharedpreferences
        deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences;
                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear().commit();
                Intent intent = new Intent(Acceuil.this, Login.class);
                startActivity(intent);
            }
        });


    }

    public void addView(String nom,String sexe,String gs,int age,String email,String description,String numero,String adresse,boolean don){
        // generate a view with the "activity_user_profile3" layout
        View newView = getLayoutInflater().inflate(R.layout.activity_user_profile3,null,false);



        // setting the values for our views
        TextView viewNom = newView.findViewById(R.id.textView8);
        TextView viewSexe = newView.findViewById(R.id.textView9);
        TextView viewGs = newView.findViewById(R.id.textView10);
        TextView viewAge = newView.findViewById(R.id.textView);
        TextView viewEmail = newView.findViewById(R.id.date_card);
        TextView viewDescription = newView.findViewById(R.id.descr);

        ImageView imageView = newView.findViewById(R.id.type_card);
        Button button2 = newView.findViewById(R.id.remove_card);
        Button appeler = newView.findViewById(R.id.appel);




        viewNom.setText(nom);
        viewSexe.setText(sexe);
        viewGs.setText(gs);
        viewAge.setText(Integer.toString(age));
        viewEmail.setText(email);
        viewDescription.setText(description);
        if(don) {
            imageView.setImageResource(R.drawable.plus);
        }else{
            imageView.setImageResource(R.drawable.remove);
        }
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q="+adresse);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        appeler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = numero;
                makePhoneCall(numero);
            }
        });

        layoutList.addView(newView);
    }

    // checking our permissions : if we don't have the permission to launch the call app we ask for it
    // when we do have that permission we launch it
    private void makePhoneCall(String number){
        if (ContextCompat.checkSelfPermission(Acceuil.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Acceuil.this , new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }else
        {
            String dial = "tel:"+number;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(dial)));
        }
    }

    // in case we didn't have the permission this method is called
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall(num);
            }
            else
            {
                Toast.makeText(this,"Permission DENIED",Toast.LENGTH_SHORT).show();
            }
        }
    }
}