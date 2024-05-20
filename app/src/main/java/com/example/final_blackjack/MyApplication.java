package com.example.final_blackjack;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.room.Room;

import com.example.final_blackjack.model.db.AppDatabase;
import com.example.final_blackjack.services.MusicService;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    private int activityReferences = 0;
    private MusicService musicService = new MusicService();
    private boolean isActivityChangingConfigurations = false;

    private static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "database-name")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(Activity activity) {
                if (++activityReferences == 1 && !isActivityChangingConfigurations) {
                    // App enters foreground
                    Intent musicServiceIntent = new Intent(activity, MusicService.class);
                    activity.startService(musicServiceIntent);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (++activityReferences == 1 && !isActivityChangingConfigurations) {
                    // App enters foreground
                    Intent musicServiceIntent = new Intent(activity, MusicService.class);
                    activity.startService(musicServiceIntent);
                }
            }


            @Override
            public void onActivityPaused(Activity activity) {}

            @Override
            public void onActivityStopped(Activity activity) {
                isActivityChangingConfigurations = activity.isChangingConfigurations();
                if (--activityReferences == 0 && !isActivityChangingConfigurations) {
                    // App enters background
                    Intent musicServiceIntent = new Intent(activity, MusicService.class);
                    stopService(musicServiceIntent);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override
            public void onActivityDestroyed(Activity activity) {}
        });
    }

    public void pauseMusic(Activity activity) {
        Intent musicServiceIntent = new Intent(activity, MusicService.class);
        stopService(musicServiceIntent);
    }

    public void resumeMusic(Activity activity) {
            // App enters foreground
            Intent musicServiceIntent = new Intent(activity, MusicService.class);
            activity.startService(musicServiceIntent);
    }
    public static AppDatabase getDatabase() {
        return db;
    }
}
