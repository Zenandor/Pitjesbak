package com.example.android.pitjesbak;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LeaderboardAcitivity extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference users;
    Query orderedUsers;

    private ArrayList<String> emails = new ArrayList<>();
    private ArrayList<String> wins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        db = FirebaseDatabase.getInstance();
        users = db.getReference("users");


        getData();

    }

    private void initRecyclerView(){
        System.out.println(emails);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        lbAdapter adapter = new lbAdapter(emails, wins, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getData(){
        orderedUsers = users.orderByChild("wins");
        orderedUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot user: dataSnapshot.getChildren()){
                        String email = user.child("email").getValue(String.class);
                        System.out.println(email);
                       emails.add(0, email);
                       wins.add(0, user.child("wins").getValue(Long.class).toString());
                    }
                }

                initRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
