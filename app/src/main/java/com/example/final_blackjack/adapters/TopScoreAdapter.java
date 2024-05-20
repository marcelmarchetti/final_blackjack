package com.example.final_blackjack.adapters;


import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_blackjack.R;
import com.example.final_blackjack.model.Player;
import com.example.final_blackjack.model.Score;

import java.util.ArrayList;
import java.util.List;

public class TopScoreAdapter extends RecyclerView.Adapter<TopScoreAdapter.ScoreViewHolder> {

    private List<Player> playerList;

    public TopScoreAdapter(List<Player> playerList) {
        this.playerList = (playerList != null) ? playerList : new ArrayList<>(); // Manejar null
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        Player player = playerList.get(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.tvScore.setText(String.valueOf(player.maxPlayerScore));
            holder.tvName.setText(player.playerName.toString());
        }
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView tvScore;
        TextView tvName;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvScore = itemView.findViewById(R.id.tvTopScore);
            tvName = itemView.findViewById(R.id.tvPlayerName);
        }
    }
}
