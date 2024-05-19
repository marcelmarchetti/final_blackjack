package com.example.final_blackjack.model.db.dao;

import android.database.sqlite.SQLiteConstraintException;
import android.os.Build;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.final_blackjack.model.Score;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ScoreDao {
    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 1")
    Single<Score> getHighestScore();

    @Insert
    Completable insert(Score score);

    @Update
    Completable update(Score score);

    default Completable insertOrUpdate(Score score) {
        return insert(score).onErrorResumeNext(error -> {
            if (error instanceof SQLiteConstraintException) {
                return update(score);
            }
            return Completable.error(error);
        });
    }

    @Query("SELECT * FROM scores ORDER BY score DESC")
    Single<List<Score>> getAll();
}