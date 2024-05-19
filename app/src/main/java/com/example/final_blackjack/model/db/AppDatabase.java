package com.example.final_blackjack.model.db;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Database;
import androidx.room.*;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.example.final_blackjack.converters.DateConverter;
import com.example.final_blackjack.model.Score;
import com.example.final_blackjack.model.db.dao.ScoreDao;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@RequiresApi(api = Build.VERSION_CODES.O)
@Database(entities = {Score.class}, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ScoreDao scoreDao();
}

 class ScoreRepository {
    private ScoreDao scoreDao;

    public ScoreRepository(Context context) {
        AppDatabase db = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            db = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "app-database").build();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            scoreDao = db.scoreDao();
        }
    }

    public Single<Score> getHighestScore() {
        return scoreDao.getHighestScore();
    }

    public Completable insertScore(Score score) {
        return scoreDao.insert(score);
    }
}

class ScoreViewModel extends AndroidViewModel {
    private ScoreRepository repository;

    public ScoreViewModel(@NonNull Application application) {
        super(application);
        repository = new ScoreRepository(application);
    }

    public Single<Score> getHighestScore() {
        return repository.getHighestScore();
    }

    public Completable insertScore(Score score) {
        return repository.insertScore(score);
    }
}
