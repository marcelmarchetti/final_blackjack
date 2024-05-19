package com.example.final_blackjack;

// En tu SettingsActivity.java

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Settings extends AppCompatActivity {

    private static final int PICK_AUDIO_REQUEST_CODE = 1;

    private Button selectAudioButton;

    private Switch pauseMusicSwitch;
    private Button spanishButton;
    private Button englishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        selectAudioButton = findViewById(R.id.selectMusicButton);
        pauseMusicSwitch = findViewById(R.id.pauseMusicSwitch);
        spanishButton = findViewById(R.id.spanishButton);
        englishButton = findViewById(R.id.englishButton);

        // Configurar Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.settings_menutitle));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        selectAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        pauseMusicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Si el switch está activado, pausar la música
                ((MyApplication) getApplication()).pauseMusic(this);
            } else {
                // Si el switch está desactivado, reanudar la música
                ((MyApplication) getApplication()).resumeMusic(this);
            }
        });

        spanishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocaleToSpanish(v);
            }
        });

        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocaleToEnglish(v);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar los clics en los elementos de la barra de acción/ActionBar
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Manejar el clic en el botón de retroceso (flecha hacia atrás)
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Select Audio File"), PICK_AUDIO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri audioUri = data.getData();
                // Guardar la ruta del archivo URI en SharedPreferences o en otra forma de almacenamiento de datos
                // Por ejemplo:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("selected_audio_uri", audioUri.toString());
                editor.apply();
            }
        }
    }

    // Método para cambiar la localización a inglés
    public void setLocaleToEnglish(View view) {
        Locale locale = new Locale("en");
        setLocale(locale);
    }

    // Método para cambiar la localización a español
    public void setLocaleToSpanish(View view) {
        Locale locale = new Locale("es");
        setLocale(locale);
    }

    // Método para cambiar la localización de la aplicación
    private void setLocale(Locale locale) {
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Aquí podrías mostrar un mensaje indicando que se ha cambiado el idioma
        String message;
        if (locale.getLanguage().equals("en")) {
            message = "Language changed to English";
        } else {
            message = "Idioma cambiado a español";
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Puedes reiniciar la actividad para aplicar los cambios de localización a todas las vistas
        recreate();
    }
}
