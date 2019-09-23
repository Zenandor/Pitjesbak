package com.example.android.pitjesbak;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class GameActivity extends AppCompatActivity {

    Button roll, stop;

    TextView dice1, dice2, dice3, stoefen, player, other, current_score_player, current_score_other, lives_player, lives_other;

    CheckBox[] checks = new CheckBox[3];

    private int[] dice = new int[3];

    int min = 1;
    int max = 6;

    private int[] dice_scores = new int[3];
    String dice_total;

    boolean sn_4, sn_5, sn_6;

    boolean has_stoeft = false;

    int rolls_left;

    int turn = 1;
    int round = 1;

    int playing;
    int otherPlayer;

    int playerCheck;

    boolean left = false;

    String lobbyKey;

    FirebaseDatabase db;
    DatabaseReference lobby;

    ValueEventListener listener;

    Toast rollToast;

    Handler disconnect_handler;
    Runnable disconnect_runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        lobbyKey= getIntent().getStringExtra("lobbyKey");

        db = FirebaseDatabase.getInstance();
        lobby = db.getReference("lobbies").child(lobbyKey);

        roll = findViewById(R.id.roll);
        stop = findViewById((R.id.stop));

        stoefen = findViewById(R.id.stoefen);
        stoefen.setVisibility(INVISIBLE);

        roll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roll();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });


        dice1 = findViewById(R.id.dice1);
        dice2 = findViewById(R.id.dice2);
        dice3 = findViewById(R.id.dice3);

        checks[0] = (CheckBox) findViewById(R.id.check1);
        checks[1] = (CheckBox) findViewById(R.id.check2);
        checks[2] = (CheckBox) findViewById(R.id.check3);

        player = findViewById(R.id.player);
        current_score_player = findViewById(R.id.current_score_player);
        lives_player = findViewById(R.id.lives_player);

        other = findViewById(R.id.other);
        current_score_other = findViewById(R.id.current_score_other);
        lives_other = findViewById(R.id.lives_other);



        /*
        disconnect_handler = new Handler();

        disconnect_runnable = new Runnable() {
            public void run() {
                leaveGame();
            }
        };
        */

          listener = lobby.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    playerCheck = getIntent().getIntExtra("player", 1);
                    if(playerCheck == 1) {
                        player.setText(dataSnapshot.child("player1").getValue(String.class));
                        current_score_player.setText(dataSnapshot.child("current_score_p1").getValue(String.class));
                        lives_player.setText(dataSnapshot.child("lives_p1").getValue(Long.class).toString());

                        other.setText(dataSnapshot.child("player2").getValue(String.class));
                        current_score_other.setText(dataSnapshot.child("current_score_p2").getValue(String.class));
                        lives_other.setText(dataSnapshot.child("lives_p2").getValue(Long.class).toString());
                    }else{
                        player.setText(dataSnapshot.child("player2").getValue(String.class));
                        current_score_player.setText(dataSnapshot.child("current_score_p2").getValue(String.class));
                        lives_player.setText(dataSnapshot.child("lives_p2").getValue(Long.class).toString());

                        other.setText(dataSnapshot.child("player1").getValue(String.class));
                        current_score_other.setText(dataSnapshot.child("current_score_p1").getValue(String.class));
                        lives_other.setText(dataSnapshot.child("lives_p1").getValue(Long.class).toString());
                    }

                     has_stoeft = dataSnapshot.child("has_stoeft").getValue(Boolean.class);

                    turn = dataSnapshot.child("turn").getValue(int.class);
                    round = dataSnapshot.child("round").getValue(int.class);

                    if(round%2 == 0) {
                        //even round (2,4,6) => player2 goes first
                        if (turn == 1 ) {
                            playing = 2;
                            otherPlayer = 1;
                        } else {
                            playing = 1;
                            otherPlayer = 2;
                        }
                    }else{
                        //uneven round (1,3,5) => player1 goes first
                        if (turn == 1) {
                            playing = 1;
                            otherPlayer = 2;
                        } else {
                            playing = 2;
                            otherPlayer = 1;
                        }
                    }


                    dice1.setText(dataSnapshot.child("dice1").getValue(Long.class).toString());
                    dice2.setText(dataSnapshot.child("dice2").getValue(Long.class).toString());
                    dice3.setText(dataSnapshot.child("dice3").getValue(Long.class).toString());

                    rolls_left = dataSnapshot.child("rolls_left").getValue(int.class);

                    if(playing == playerCheck){
                        roll.setEnabled(true);

                        /*
                        disconnect_handler.postDelayed(disconnect_runnable, 20000);
                        System.out.println("timer started");
                        */

                        if(rolls_left == 3){
                            for (int i = 0; i < 3; i++) {
                                checks[i].setEnabled(false);
                            }
                            stop.setEnabled(false);
                        }
                        if (rolls_left < 3) {

                            if(turn == 1){
                                if (rolls_left == 2) {
                                    stoefen.setVisibility(VISIBLE);
                                } else {
                                    stoefen.setVisibility(INVISIBLE);
                                }
                            }


                            if(rolls_left == 0){
                                roll.setEnabled(false);
                            }

                            stop.setEnabled(true);
                            for (int i = 0; i < 3; i++) {
                                checks[i].setEnabled(true);
                            }
                        }
                    }else{
                        /*
                        disconnect_handler.removeCallbacks(disconnect_runnable);
                        System.out.println("removed timer");
                        */

                        roll.setEnabled(false);
                        stop.setEnabled(false);
                    }




                    if(dataSnapshot.child("round_winner").exists()) {

                        /*
                        disconnect_handler.removeCallbacks(disconnect_runnable);
                        disconnect_handler.postDelayed(disconnect_runnable, 20000);
                        */

                        if (dataSnapshot.child("winner").exists()) {

                            /*
                            if(playing == playerCheck){

                                disconnect_handler.removeCallbacks(disconnect_runnable);
                                System.out.println("timer removed");

                            }
                            */

                            Intent intent = new Intent(getApplicationContext(), FinishActivity.class);
                            intent.putExtra("lobbyKey", lobbyKey);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            GameActivity.this.finish();
                        } else {

                            LayoutInflater inflater = (LayoutInflater)
                                    getSystemService(LAYOUT_INFLATER_SERVICE);
                            View popupView = inflater.inflate(R.layout.popupwindow, null);

                            // create the popup window
                            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                            boolean focusable = true; // lets taps outside the popup also dismiss it
                            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                            TextView popupText = popupView.findViewById(R.id.winner);
                            Button popupButton = popupView.findViewById(R.id.next_round);

                            String winnerPlayer = "player" + dataSnapshot.child("round_winner").getValue(Integer.class);
                            String winnerText = dataSnapshot.child(winnerPlayer).getValue(String.class) + " has won the round!";

                            // show the popup window
                            // which view you pass in doesn't matter, it is only used for the window tolken
                            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                            popupText.setText(winnerText);
                            // dismiss the popup window when touched
                            popupButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    /*
                                    if (playing == playerCheck) {

                                        disconnect_handler.removeCallbacks(disconnect_runnable);
                                        System.out.println("timer removed");

                                    }
                                    */

                                    popupWindow.dismiss();
                                    if (dataSnapshot.child("readyup").exists()) {
                                        lobby.child("readyup").setValue(2);
                                    } else {
                                        lobby.child("readyup").setValue(1);
                                    }
                                }

                            });

                            if(dataSnapshot.child("readyup").exists()){
                                if(dataSnapshot.child("readyup").getValue(Integer.class) == 2){
                                    //disconnect_handler.removeCallbacks(disconnect_runnable);
                                    lobby.child("current_score_p1").setValue("0 points");
                                    lobby.child("current_score_p2").setValue("0 points");
                                    lobby.child("round_winner").removeValue();
                                    lobby.child("has_stoeft").setValue(false);
                                    lobby.child("rolls_left").setValue(3);
                                    lobby.child("turn").setValue(1);
                                    lobby.child("readyup").removeValue();
                                    round++;
                                    lobby.child("round").setValue(round);
                                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                                    intent.putExtra("player", playerCheck);
                                    intent.putExtra("lobbyKey", lobbyKey);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    GameActivity.this.finish();
                                }
                            }

                        }
                    }




                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void leaveGame() {
        left = true;

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        GameActivity.this.finish();

    }

    /*
    @Override
    public void onUserInteraction() {
        if(playerCheck == playing){
            disconnect_handler.removeCallbacks(disconnect_runnable);
            disconnect_handler.postDelayed(disconnect_runnable, 20000);
            System.out.println("timer restarted");
        }

    }
    */

    @Override
    public void onBackPressed() {
        lobby.removeEventListener(listener);
        playerCheck = getIntent().getIntExtra("player", 1);
        if(playerCheck == 1){
            lobby.child("winner").setValue(2);
            lobby.child("round_winner").setValue(2);
        }else{
            lobby.child("winner").setValue(1);
            lobby.child("round_winner").setValue(1);

        }

        super.onBackPressed();
    }

    public void roll(){
        if(rolls_left > 0){
            rolls_left -= 1;
        }
        lobby.child("rolls_left").setValue(rolls_left);

        if (rolls_left > 0) {
            if(rollToast != null){
                rollToast.cancel();
            }
            rollToast = Toast.makeText(GameActivity.this, rolls_left + " rolls left", Toast.LENGTH_SHORT);
            rollToast.show();
        } else if (rolls_left == 0) {
            rollToast = Toast.makeText(GameActivity.this, "no rolls left", Toast.LENGTH_SHORT);
            rollToast.show();
            roll.setEnabled(false);
        }


        sn_4 = false;
        sn_5 = false;
        sn_6 = false;

        // generate numbers
        for (int i = 0; i < 3; i++) {
            if (checks[i].isChecked() == false) {
                Random r = new Random();
                int random = r.nextInt(((max - min) + 1)) + min;
                dice[i] = random;
            }else{
                if(i == 0){
                    dice[0] = Integer.parseInt(dice1.getText().toString());
                }else if(i == 1){
                    dice[1] = Integer.parseInt(dice2.getText().toString());
                }else if(i == 2){
                    dice[2] = Integer.parseInt(dice3.getText().toString());
                }
            }
        }

        lobby.child("dice1").setValue(dice[0]);
        lobby.child("dice2").setValue(dice[1]);
        lobby.child("dice3").setValue(dice[2]);



        // Set scores
        for (int i = 0; i < 3; i++) {
            if (dice[i] == 4) {
                sn_4 = true;
            }

            if (dice[i] == 5) {
                sn_5 = true;
            }

            if (dice[i] == 6) {
                sn_6 = true;
            }
        }

        if (dice[0] == dice[1] && dice[1] == dice[2]) {
            if (dice[0] == 1) {
                dice_total = "3 azen";
            } else {
                dice_total = "zand";
            }
        } else if (sn_4 == true && sn_5 == true && sn_6 == true) {
            dice_total = "soixante-neuf";
        } else {
            for (int i = 0; i < 3; i++) {
                if (dice[i] == 1) {
                    dice_scores[i] = 100;
                } else if (dice[i] == 6) {
                    dice_scores[i] = 60;
                } else {
                    dice_scores[i] = dice[i];
                }
            }

            dice_total = (dice_scores[0] + dice_scores[1] + dice_scores[2]) + " points";

        }
        if(playing == 1){
            lobby.child("current_score_p1").setValue(dice_total);
        }else{
            lobby.child("current_score_p2").setValue(dice_total);
        }

    };

    public void stop(){


        if(turn == 1){
            if(rolls_left == 2){
                lobby.child("has_stoeft").setValue(true);
            }

            lobby.child("turn").setValue(2);
            lobby.child("rolls_left").setValue(3);

            /*
            if(playing == playerCheck){
                disconnect_handler.removeCallbacks(disconnect_runnable);
                System.out.println("timer removed");
            }
            */

        }else if(turn == 2){
            int winner = 0;
            int worth = 0;
            switch (current_score_player.getText().toString()) {
                case "3 azen":
                    if (current_score_other.getText().toString().equals("3 azen")) {
                        winner = 0;
                        worth = 0;
                    } else {
                        winner = playing;
                        worth = Integer.parseInt(lives_player.getText().toString());
                    }
                    break;
                case "soixante-neuf":
                    if (current_score_other.getText().toString().equals("3 azen")) {
                        winner = otherPlayer;
                        worth = Integer.parseInt(lives_other.getText().toString());
                    } else if (current_score_other.getText().toString().equals("soixante-neuf")) {
                        winner = 0;
                        worth = 0;
                    } else {
                        winner = playing;
                        worth = 3;
                    }
                    break;
                case "zand":
                    if (current_score_other.getText().toString().equals("3 azen") || current_score_other.getText().toString().equals("soixante-neuf")) {
                        winner = otherPlayer;
                        if(current_score_other.getText().toString().equals("3 azen")){
                            worth = Integer.parseInt(lives_other.getText().toString());
                        }else{
                            worth = 3;
                        }
                    } else if (current_score_other.getText().toString().equals("zand")) {
                        winner = 0;
                        worth = 0;
                    } else {
                        winner = playing;
                        worth = 2;
                    }
                    break;
                default:
                    if (current_score_other.getText().toString().equals("3 azen") || current_score_other.getText().toString().equals("soixante-neuf") || current_score_other.getText().toString().equals("zand")) {
                        winner = otherPlayer;
                        if(current_score_other.getText().toString().equals("3 azen")){
                            worth = Integer.parseInt(lives_other.getText().toString());
                        }else if(current_score_other.getText().toString().equals("soixante-neuf")){
                            worth = 3;
                        }else if(current_score_other.getText().toString().equals("zand")){
                            worth = playing;
                        }
                    } else {
                        String[] playing_parts = current_score_player.getText().toString().split(" ");
                        int score_playing = Integer.parseInt(playing_parts[0]);
                        String[] other_parts = current_score_other.getText().toString().split(" ");
                        int score_other = Integer.parseInt(other_parts[0]);
                        if (score_other > score_playing) {
                            winner = otherPlayer;
                            worth = 1;
                        } else if (score_other < score_playing) {
                            winner = playing;
                            worth = 1;
                        } else {
                            winner = 0;
                            worth = 0;
                        }
                    }
            }



            int lives_otherPlayer = Integer.parseInt(lives_other.getText().toString());
            int lives_playing = Integer.parseInt(lives_player.getText().toString());

             if( winner == otherPlayer){
                lives_otherPlayer -= worth;
                if(has_stoeft){
                    lives_otherPlayer --;
                }
                String winner_lives = "lives_p" + otherPlayer;
                lobby.child(winner_lives).setValue(lives_otherPlayer);
            }else if(winner == playing){
                lives_playing -= worth;
                if(has_stoeft){
                    lives_otherPlayer += 2;
                }

                String winner_lives = "lives_p" + playing;
                String loser_lives = "lives_p" + otherPlayer;
                lobby.child(winner_lives).setValue(lives_playing);
                lobby.child(loser_lives).setValue(lives_otherPlayer);
            }

            if(lives_otherPlayer <= 0){
                lobby.child("winner").setValue(otherPlayer);
            }
            if(lives_playing <= 0){
                lobby.child("winner").setValue(playing);
            }

            lobby.child("round_winner").setValue(winner);




        }
    }

    @Override
    protected void onDestroy() {
        lobby.removeEventListener(listener);
        if(left == true){
            lobby.child("winner").setValue(otherPlayer);
            lobby.child("round_winner").setValue(otherPlayer);

        }

        super.onDestroy();



    }
}
