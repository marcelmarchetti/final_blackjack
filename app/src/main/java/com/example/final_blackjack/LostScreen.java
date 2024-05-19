package com.example.final_blackjack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.NoCopySpan;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LostScreen extends AppCompatActivity {
    Button yesButton;
    Button noButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostgame);

        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        yesButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BlackJack.class);
            startActivity(intent);
        });

        noButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}
