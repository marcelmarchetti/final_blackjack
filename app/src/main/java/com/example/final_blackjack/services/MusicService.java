package com.example.final_blackjack.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.example.final_blackjack.R;

import java.io.IOException;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    public String selectedMusicUri;

    private AudioManager audioManager;
    private BroadcastReceiver noisyAudioStreamReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        // Inicializar el MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true);

        // Ajustar el volumen
        float volume = 2.0f; // volumen máximo
        mediaPlayer.setVolume(volume, volume);

        // Configurar AudioManager
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Configurar el BroadcastReceiver para interrupciones de audio
        noisyAudioStreamReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction()) && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        };

        registerReceiver(noisyAudioStreamReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer != null && intent != null && intent.hasExtra("selected_audio_uri")) {
            selectedMusicUri = intent.getStringExtra("selected_audio_uri");
        }
        startPlayingMusic();
        return START_STICKY;
    }

    public void startPlayingMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        if (selectedMusicUri != null && !selectedMusicUri.isEmpty()) {
            try {
                mediaPlayer.setDataSource(selectedMusicUri);
                mediaPlayer.prepare();
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Reproducir la música por defecto si no hay música seleccionada por el usuario
            mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // Detener la música cuando la aplicación está en segundo plano
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(noisyAudioStreamReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
