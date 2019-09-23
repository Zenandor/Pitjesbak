package com.example.android.pitjesbak;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FinishActivity extends AppCompatActivity {
    static FinishActivity finishActivity;

    Button home;
    TextView congrats;

    String winner;

    String lobbyKey;

    String con;

    FirebaseDatabase db;
    DatabaseReference lobbies,lobby, users;

    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        finishActivity = this;

        congrats = findViewById(R.id.congratulations);
        lobbyKey = getIntent().getStringExtra("lobbyKey");
        db = FirebaseDatabase.getInstance();
        lobby = db.getReference("lobbies").child(lobbyKey);
        users = db.getReference("users");

        try {
            listener = lobby.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child("winner").getValue(Long.class) == 1) {
                        winner = dataSnapshot.child("player1").getValue().toString();
                    } else if (dataSnapshot.child("winner").getValue(Long.class) == 2) {
                        winner = dataSnapshot.child("player2").getValue().toString();
                    }

                    if (winner.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        con = "you won the game! \n Congratulations!!!";

                        if (!dataSnapshot.child("left").exists()) {
                            users.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    int wins = Integer.parseInt(dataSnapshot.child(UID).child("wins").getValue(Long.class).toString());
                                    wins++;

                                    users.child(UID).child("wins").setValue(wins);
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }


                    } else {
                        con = "You lost! \n Better luck next time!";
                    }
                    congrats.setText(con);

                    home = findViewById(R.id.home);

                    home.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("lobbyKey", lobbyKey);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            FinishActivity.this.finish();
                            startActivity(intent);

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Throwable t){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("lobbyKey", lobbyKey);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            FinishActivity.this.finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lobby.removeEventListener(listener);
    }
}
