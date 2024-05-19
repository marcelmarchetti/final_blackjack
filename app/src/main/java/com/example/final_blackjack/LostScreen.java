package com.example.final_blackjack;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.NoCopySpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.reactivex.rxjava3.annotations.NonNull;

public class LostScreen extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 123;
    private static final int REQUEST_CODE_CAPTURE_SCREENSHOT = 102;
    Button yesButton;
    Button noButton;
    TextView scoreTextView;
    Button screenshotButton;
    int maxScore;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostgame);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.lose_menutitle));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        maxScore = getIntent().getIntExtra("maxScore", 1000);
        if(maxScore <= 1000){ maxScore = 1000;}
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);
        screenshotButton = findViewById(R.id.screenshotButton);
        scoreTextView = findViewById(R.id.maxScoreTextView);
        String temp = String.valueOf(maxScore);
        scoreTextView.setText(getString(R.string.lose_maxpuntuationtext) + temp);
        yesButton.setOnClickListener(v -> {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                intent = new Intent(this, BlackJack.class);
            }
            startActivity(intent);
        });

        noButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        screenshotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasWriteStoragePermission()) {
                    System.out.println("Has permission");
                    captureScreenshot();
                } else {
                    System.out.println("No permission");
                    requestWriteStoragePermission();
                }
            }
        });
    }
    private boolean hasWriteStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    // Método para solicitar permisos de escritura externa
    private void requestWriteStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_PERMISSIONS);
    }

    private void captureScreenshot() {
        // Capture the screen
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap screenshotBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        // Save the screenshot to a file
        saveScreenshotToFile(screenshotBitmap);
        Toast.makeText(this, getString(R.string.lose_screenshot), Toast.LENGTH_SHORT).show();
    }

    private void saveScreenshotToFile(Bitmap screenshotBitmap) {
        // Create a file to save the screenshot
        File screenshotsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String fileName = "screenshot_" + System.currentTimeMillis() + ".png";
        File screenshotFile = new File(screenshotsDir, fileName);

        // Save the screenshot to the file
        try {
            OutputStream outputStream = new FileOutputStream(screenshotFile);
            screenshotBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add the screenshot to the gallery so the user can view it
        MediaStore.Images.Media.insertImage(getContentResolver(), screenshotBitmap,
                "Screenshot " + System.currentTimeMillis(), "Screenshot");

        // Inform the user that the screenshot has been saved
        Uri screenshotUri = Uri.fromFile(screenshotFile);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, screenshotUri);
        sendBroadcast(mediaScanIntent);
    }

    // Método para manejar el resultado de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado, realizar la acción deseada (captura de pantalla, en este caso)
                System.out.println("Permission granted");
                captureScreenshot();
            } else {
                // Permiso denegado, mostrar un mensaje al usuario o tomar otra acción adecuada
                System.out.println("Permission denied");
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
