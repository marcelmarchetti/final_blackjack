package com.example.final_blackjack;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class Information extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_activity);

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient()); // Esto asegura que la página se abra en el WebView y no en el navegador
        webView.getSettings().setJavaScriptEnabled(true); // Habilitar JavaScript si es necesario
        webView.loadUrl("https://www.crehana.com/blog/estilo-vida/guia-reglas-blackjack/"); // Reemplaza con la URL que quieras cargar
        // Configurar Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.information_menutitle));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Manejar el clic del botón de "atrás" en la ActionBar
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Llamar al método onBackPressed para manejar la navegación hacia atrás
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
