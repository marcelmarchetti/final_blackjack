package com.example.final_blackjack;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_blackjack.adapters.ScoreAdapter;
import com.example.final_blackjack.adapters.TopScoreAdapter;
import com.example.final_blackjack.model.Player;
import com.example.final_blackjack.model.Score;
import com.example.final_blackjack.model.db.dao.ScoreDao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TopScores extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TopScoreAdapter adapter;
    private List<Player> playerList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_top_scores);
        playerList = getIntent().getParcelableArrayListExtra("top10Players");
        System.out.println("TopScores " + playerList.size());
        // Configurar Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.matchHistory_menutitle));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTopScores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener datos (esto puede variar dependiendo de tu implementación de la base de datos)

        adapter = new TopScoreAdapter(playerList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
