package com.example.android.pitjesbak;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class lbAdapter extends RecyclerView.Adapter<lbAdapter.ViewHolder>{

    private ArrayList<String> emails = new ArrayList<>();
    private ArrayList<String> wins = new ArrayList<>();
    private Context context;


    public lbAdapter(ArrayList<String> emails, ArrayList<String> wins, Context context) {
        this.emails = emails;
        this.wins = wins;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.email.setText(emails.get(position));
        holder.wins.setText(wins.get(position)) ;
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView email;
        TextView wins;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView){
            super(itemView);
            email = itemView.findViewById(R.id.lb_player_email);
            wins = itemView.findViewById(R.id.lb_player_wins);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }

    }
}
