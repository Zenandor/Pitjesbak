package com.example.android.pitjesbak;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class FindOpponentActivity extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference lobbies;
    private DatabaseReference users;
    private DatabaseReference lobby;

    private String lobbyKey;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    TextView status, findingOpponent;

    TextView cancel;

    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findopponent);


        db = FirebaseDatabase.getInstance();
        lobbies = db.getReference("lobbies");
        users = db.getReference("users");

        lobbies.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(user.getUid()).exists()) {
                        lobbies.child(user.getUid()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        final Handler handler = new Handler();

        final Runnable findOpponent = new Runnable() {
            public void run() {
                OpponentExists();
            }
        };

        handler.postDelayed(findOpponent, 5000);

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lobbies.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(user.getUid()).exists()){
                            lobbies.child(user.getUid()).removeValue();
                        }else{
                            handler.removeCallbacks(findOpponent);
                        }
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        FindOpponentActivity.this.finish();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });











    }






    public void OpponentExists(){

        lobbies.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    joinOpponent();
                }else{
                    createLobby();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    };

    public void joinOpponent(){
        lobbies.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    if(!child.child("player2").exists()){
                        lobbyKey = child.getKey();
                        break;
                    }
                }
                if(lobbyKey == null){
                    createLobby();
                }else{
                    lobbies.child(lobbyKey).child("player2").setValue(user.getEmail());
                    Intent startGame = new Intent(getApplicationContext(), GameActivity.class);
                    startGame.putExtra("lobbyKey", lobbyKey);
                    startGame.putExtra("player", 2);
                    startGame.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(startGame);
                    FindOpponentActivity.this.finish();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void createLobby(){
        lobbies.child(user.getUid()).child("dice1").setValue(0);
        lobbies.child(user.getUid()).child("dice2").setValue(0);
        lobbies.child(user.getUid()).child("dice3").setValue(0);
        lobbies.child(user.getUid()).child("lives_p1").setValue(7);
        lobbies.child(user.getUid()).child("lives_p2").setValue(7);
        lobbies.child(user.getUid()).child("turn").setValue(1);
        lobbies.child(user.getUid()).child("has_stoeft").setValue(false);
        lobbies.child(user.getUid()).child("total_score_p1").setValue(0);
        lobbies.child(user.getUid()).child("total_score_p2").setValue(0);
        lobbies.child(user.getUid()).child("current_score_p1").setValue("0 points");
        lobbies.child(user.getUid()).child("current_score_p2").setValue("0 points");
        lobbies.child(user.getUid()).child("rolls_left").setValue(3);
        lobbies.child(user.getUid()).child("round").setValue(1);
        lobbies.child(user.getUid()).child("player1").setValue(user.getEmail());
        lobbyKey = user.getUid();

        lobby = lobbies.child(lobbyKey);

        listener = lobby.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("test");
                if(dataSnapshot.child("player2").exists()){
                    Intent startGame = new Intent(getApplicationContext(), GameActivity.class);
                    startGame.putExtra("lobbyKey", lobbyKey);
                    startGame.putExtra("player", 1);
                    startGame.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(startGame);
                    FindOpponentActivity.this.finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listener != null){
            lobby.removeEventListener(listener);
        }

    }
}
