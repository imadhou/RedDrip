package com.example.reddrip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {

    LinearLayout layoutList;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile2);

        layoutList = findViewById(R.id.layout_list);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String email = sharedPreferences.getString("email","");
        reference = FirebaseDatabase.getInstance().getReference().child("Annonce");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                layoutList.removeAllViews();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Annonce annonceDon = snapshot1.getValue(Annonce.class);
                    if(annonceDon.getEmail().equals(email)) {
                        addView(annonceDon.getDescription(), annonceDon.getType().equals("Donnateur"),snapshot1.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void removeView(View view,String key){
        Toast.makeText(UserProfile.this, "remove "+key, Toast.LENGTH_LONG).show();
        DatabaseReference dbremove;
            dbremove = FirebaseDatabase.getInstance().getReference("Annonce").child(key);
        dbremove.removeValue();
        layoutList.removeView(view);
    }

    public void addView(String date,boolean don,String key){
        View newView = getLayoutInflater().inflate(R.layout.activity_cards,null,false);
        TextView date_card = newView.findViewById(R.id.date_card);
        ImageView type_card = newView.findViewById(R.id.type_card);
        Button button2 = newView.findViewById(R.id.remove_card);

        date_card.setText(date);
        if(don) {
            type_card.setImageResource(R.drawable.plus);
        }else{
            type_card.setImageResource(R.drawable.remove);
        }
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(newView,key);

            }
        });

        layoutList.addView(newView);
    }

}