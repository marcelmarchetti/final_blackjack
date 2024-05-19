package com.example.final_blackjack.model;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)
@Entity(tableName = "scores")
public class Score {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public int score;

    public LocalDate date;

    public double latitude;
    public double longitude;

    public Score(int score, LocalDate date) {
        this.score = score;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", score=" + score +
                ", date=" + date +
                '}';
    }
}