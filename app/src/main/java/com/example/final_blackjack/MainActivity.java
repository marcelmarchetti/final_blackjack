package com.example.final_blackjack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Menú Principal");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_start_game) {
            // Acción para iniciar el juego
            startGame();
            return true;
        } else if (itemId == R.id.action_options) {
            // Acción para abrir las opciones
            options();
            return true;
        } else if (itemId == R.id.action_history) {
            // Acción para mostrar el historial
            showHistory();
            return true;
        } else if (itemId == R.id.action_exit) {
            // Acción para salir de la aplicación
            exitApp();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    void startGame() {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            intent = new Intent(this, BlackJack.class);
        }
        startActivity(intent);
    }

    void options() {
        // Código para abrir la configuración
    }

    void showHistory() {
        Intent intent = new Intent(this, MatchHistory.class);
        startActivity(intent);
    }

    void exitApp() {
        finish();
    }
}
