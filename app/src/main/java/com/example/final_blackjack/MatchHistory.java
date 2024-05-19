package com.example.final_blackjack;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.final_blackjack.adapters.ScoreAdapter;
import com.example.final_blackjack.model.Score;
import com.example.final_blackjack.model.db.AppDatabase;
import com.example.final_blackjack.model.db.dao.ScoreDao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MatchHistory extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static AppDatabase db;
    private ScoreAdapter adapter;
    private List<Score> scoreList;
    private ScoreDao scoreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_history);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "app-database").build();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            scoreDao = db.scoreDao();
        }

        // Configurar Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Historial de Partidas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewScores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener datos (esto puede variar dependiendo de tu implementación de la base de datos)
        getScoresFromDatabase()
                .subscribe(resultScores -> {
                    scoreList = resultScores;
                    adapter = new ScoreAdapter(scoreList);
                    recyclerView.setAdapter(adapter);
                }, throwable -> {
                    // Manejar el error aquí, por ejemplo, mostrando un mensaje al usuario
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                });

        adapter = new ScoreAdapter(scoreList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Método para obtener los datos desde la base de datos
    private Single<List<Score>> getScoresFromDatabase() {
        return scoreDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(scores -> new ArrayList<>(scores));
    }
}
