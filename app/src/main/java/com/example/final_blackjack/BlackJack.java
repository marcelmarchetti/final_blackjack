package com.example.final_blackjack;

import com.example.final_blackjack.model.Score;
import com.example.final_blackjack.model.Card;
import com.example.final_blackjack.model.Chip;
import com.example.final_blackjack.model.Dealer;
import com.example.final_blackjack.model.Player;
import com.example.final_blackjack.model.db.AppDatabase;
import com.example.final_blackjack.model.db.dao.ScoreDao;

import android.location.Location;
import android.provider.Settings;
import android.Manifest;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import com.example.final_blackjack.R;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.media.SoundPool;
import android.os.*;
import android.view.*;
import android.widget.*;

import com.example.final_blackjack.utils.FireStore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.schedulers.Schedulers;


@RequiresApi(api = Build.VERSION_CODES.O)
public class BlackJack extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 3;
    private static final int NOTIFICATION_ID = 1;
    private static final int ACTION_APP_NOTIFICATION_SETTINGS = 1001;
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    int currentBet = 0;
    ScoreDao scoreDao;

    int maxScore = 1000;

    double playerLongitude = 0;

    double playerLatitude = 0;
    public static AppDatabase db;
    Chip chipFifty = new Chip(50, "images/chip50.png");
    Chip chipHundred = new Chip(100, "images/chip100.png");
    Chip chipFiveHundred = new Chip(500, "images/chip500.png");
    ArrayList<Card> deck;
    Player player = new Player();
    Random random = new Random();
    Dealer dealer = new Dealer();

    ImageView[] dealerCardViews;
    ImageView[] playerCardViews;

    ImageView[] chipsViews;
    TextView resultTextView;
    Button hitButton;
    Button stayButton;
    Button startButton;
    Button restartButton;
    TextView scoreTextView;
    TextView currentBetTextView;
    Button resetBetButton;
    SoundPool soundPool;
    int soundIdCard;
    int soundIdChip;
    private FirebaseFirestore dbFirebase;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbFirebase = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestLocationPermission();
        setContentView(R.layout.board);
        db = MyApplication.getDatabase();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.blackjack_menutitle));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        scoreDao = db.scoreDao();

        createNotificationChannel();

        soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        soundIdCard = soundPool.load(this, R.raw.card, 1);
        soundIdChip = soundPool.load(this, R.raw.chip_sound, 1);
        player.playerName = getIntent().getStringExtra("playerName");
        dealerCardViews = new ImageView[]{
                findViewById(R.id.dealerCard1View),
                findViewById(R.id.dealerCard2View),
                findViewById(R.id.dealerCard3View),
                findViewById(R.id.dealerCard4View),
                findViewById(R.id.dealerCard5View),
                findViewById(R.id.dealerCard6View),
        };

        playerCardViews = new ImageView[]{
                findViewById(R.id.playerCard1View),
                findViewById(R.id.playerCard2View),
                findViewById(R.id.playerCard3View),
                findViewById(R.id.playerCard4View),
                findViewById(R.id.playerCard5View),
                findViewById(R.id.playerCard6View),
        };
        chipsViews = new ImageView[]{
                findViewById(R.id.chipView1),
                findViewById(R.id.chipView2),
                findViewById(R.id.chipView3),
        };

        resultTextView = findViewById(R.id.resultTextView);
        hitButton = findViewById(R.id.hitButton);
        stayButton = findViewById(R.id.stayButton);
        restartButton = findViewById(R.id.restartButton);
        scoreTextView = findViewById(R.id.scoreTextView);
        currentBetTextView = findViewById(R.id.currentBetTextView);
        resetBetButton = findViewById(R.id.resetBetButton);
        startButton = findViewById(R.id.closeBetButton);
        startButton.setSoundEffectsEnabled(false);
        hitButton.setSoundEffectsEnabled(false);
        updateScoreUI(scoreTextView);
        updateCurrentBetUI(currentBetTextView);

        loadImageFromAssets(chipsViews[0], chipFifty.imagePath);
        loadImageFromAssets(chipsViews[1], chipHundred.imagePath);
        loadImageFromAssets(chipsViews[2], chipFiveHundred.imagePath);
        for (ImageView chipView : chipsViews) {
            chipView.setSoundEffectsEnabled(false);
        }
        hitButton.setEnabled(false);
        stayButton.setEnabled(false);
        resetBetButton.setVisibility(View.VISIBLE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBet > 0) {
                    cardSound();
                    resetCardViews(dealerCardViews);
                    resetCardViews(playerCardViews);
                    hitButton.setEnabled(true);
                    stayButton.setEnabled(true);
                    startButton.setVisibility(View.GONE);
                    resetBetButton.setVisibility(View.GONE);
                    player.playerScore -= currentBet;
                    updateScoreUI(scoreTextView);
                    resultTextView.setText("");
                    startGame();
                } else {
                    Toast.makeText(BlackJack.this, getString(R.string.blackjack_makeabetError), Toast.LENGTH_SHORT).show();
                }
            }
        });

        resetBetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentBet = 0;
                updateCurrentBetUI(currentBetTextView);
            }
        });

        hitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSound();
                Card card = deck.remove(deck.size() - 1);
                player.playerPoints += card.getValue();
                player.playerAces += card.isAce() ? 1 : 0;
                player.playerHand.add(card);
                if (reducePlayerAce() > 21) {
                    hitButton.setEnabled(false);
                    stayButton.setEnabled(false);
                }
                updateGameUI();
            }
        });

        stayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealer.getDealerPoints() < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealer.dealerPoints += card.getValue();
                    dealer.dealerAces += card.isAce() ? 1 : 0;
                    dealer.dealerHand.add(card);
                }
                updateGameUI();
            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);
                startButton.setVisibility(View.VISIBLE);
                resetBetButton.setVisibility(View.VISIBLE);
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);
                for (ImageView chipView : chipsViews) {
                    chipView.setVisibility(View.VISIBLE);
                }
                currentBet = 0;
                updateCurrentBetUI(currentBetTextView);
                resultTextView.setText("");
                resetCardViews(dealerCardViews);
                resetCardViews(playerCardViews);
                restartButton.setVisibility(View.GONE);
            }
        });

        chipsViews[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipSound();
                placeBet(chipFifty.value);
            }
        });

        chipsViews[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipSound();
                placeBet(chipHundred.value);
            }
        });

        chipsViews[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipSound();
                placeBet(chipFiveHundred.value);
            }
        });
    }

    private void placeBet(int amount) {
        if (currentBet + amount <= player.playerScore) {
            currentBet += amount;
            updateCurrentBetUI(currentBetTextView);
        } else {
            currentBet = player.playerScore;
            updateCurrentBetUI(currentBetTextView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                showExitConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.blackjack_exitmessage));
        builder.setPositiveButton(getString(R.string.blackjack_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Si el usuario hace clic en "Sí", finalizamos la actividad
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.blackjack_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Si el usuario hace clic en "Cancelar", simplemente cerramos el diálogo
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void startGame() {
        buildDeck();
        shuffleDeck();

        dealer.dealerHand = new ArrayList<>();
        dealer.dealerPoints = 0;
        dealer.dealerAces = 0;

        dealer.hiddenCard = deck.remove(deck.size() - 1);
        dealer.dealerPoints += dealer.hiddenCard.getValue();
        dealer.dealerAces += dealer.hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealer.dealerPoints += card.getValue();
        dealer.dealerAces += card.isAce() ? 1 : 0;
        dealer.dealerHand.add(card);

        player.playerHand = new ArrayList<>();
        player.playerPoints = 0;
        player.playerAces = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            player.playerPoints += card.getValue();
            player.playerAces += card.isAce() ? 1 : 0;
            player.playerHand.add(card);
        }

        updateGameUI();
    }

    public void buildDeck() {
        deck = new ArrayList<>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                Card card = new Card(value, type);
                deck.add(card);
            }
        }
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }
    }

    public int reducePlayerAce() {
        while (player.playerPoints > 21 && player.playerAces > 0) {
            player.playerPoints -= 10;
            player.playerAces--;
        }
        return player.playerPoints;
    }

    public int reduceDealerAce() {
        while (dealer.dealerPoints > 21 && dealer.dealerAces > 0) {
            dealer.dealerPoints -= 10;
            dealer.dealerAces--;
        }
        return dealer.dealerPoints;
    }

    public void updateGameUI() {
        try {
            // Draw hidden card
            Bitmap hiddenCardImg = BitmapFactory.decodeStream(getAssets().open("images/BACK.png"));
            if (!stayButton.isEnabled()) {
                hiddenCardImg = BitmapFactory.decodeStream(getAssets().open(dealer.hiddenCard.getImagePath()));
            }
            dealerCardViews[0].setImageBitmap(hiddenCardImg);

            // Draw dealer's hand
            for (int i = 0; i < dealer.dealerHand.size(); i++) {
                Card card = dealer.dealerHand.get(i);
                Bitmap cardImg = BitmapFactory.decodeStream(getAssets().open(card.getImagePath()));
                dealerCardViews[i + 1].setImageBitmap(cardImg);
            }

            // Draw player's hand
            for (int i = 0; i < player.playerHand.size(); i++) {
                Card card = player.playerHand.get(i);
                Bitmap cardImg = BitmapFactory.decodeStream(getAssets().open(card.getImagePath()));
                playerCardViews[i].setImageBitmap(cardImg);
            }

            if (!stayButton.isEnabled() || player.playerPoints > 21) {
                dealer.dealerPoints = reduceDealerAce();
                player.playerPoints = reducePlayerAce();

                String message = "";
                if (player.playerPoints > 21) {
                    message = getString(R.string.blackjack_loose);
                    if (player.playerScore <= 0) {
                        Intent intent = new Intent(BlackJack.this, LostScreen.class);
                        intent.putExtra("maxScore", maxScore);
                        if (hasWriteCalendarPermission()) {
                            agregarEventoAlCalendario("BlackJack", "Has perdido con una puntuacion maxima de: " + String.valueOf(maxScore), System.currentTimeMillis(), System.currentTimeMillis() + 3600000);
                        } else {
                            requestWriteCalendarPermission();
                            agregarEventoAlCalendario("BlackJack", "Has perdido con una puntuacion maxima de: " + String.valueOf(maxScore), System.currentTimeMillis(), System.currentTimeMillis() + 3600000);
                        }
                        startActivity(intent);
                    }
                } else if (dealer.dealerPoints > 21) {
                    message = getString(R.string.blackjack_win);
                    getLastLocation();
                    showNotification(maxScore);
                    player.playerScore += currentBet * 2;
                    if (player.playerScore > maxScore) {
                        maxScore = player.playerScore;
                        Score newScore = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            newScore = new Score(maxScore, LocalDate.now());
                            newScore.longitude = playerLongitude;
                            newScore.latitude = playerLatitude;
                        }
                        scoreDao.insertOrUpdate(newScore)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    // La puntuación se ha insertado o actualizado correctamente
                                }, throwable -> {
                                    // Manejar cualquier error que pueda ocurrir durante la inserción o actualización
                                });
                    }
                    scoreDao.getAll()
                            .subscribeOn(Schedulers.io()) // Ejecutar en un hilo de background
                            .observeOn(AndroidSchedulers.mainThread()) // Observar en el hilo principal para actualizar la IU
                            .subscribe(scores -> {
                                for (Score score : scores) {
                                    System.out.println(score.toString());
                                }
                            }, throwable -> {
                                // Manejar cualquier error que ocurra durante la consulta
                                throwable.printStackTrace();
                            });
                } else if (player.playerPoints == dealer.dealerPoints) {
                    message = getString(R.string.blackjack_tie);
                    player.playerScore += currentBet;
                } else if (player.playerPoints > dealer.dealerPoints) {
                    message = getString(R.string.blackjack_win);
                    showNotification(maxScore);
                    getLastLocation();
                    player.playerScore += currentBet * 2;
                    if (player.playerScore > maxScore) {
                        maxScore = player.playerScore;
                        Score newScore = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            newScore = new Score(maxScore, LocalDate.now());
                            newScore.longitude = playerLongitude;
                            newScore.latitude = playerLatitude;
                        }
                        scoreDao.insertOrUpdate(newScore)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    // La puntuación se ha insertado o actualizado correctamente
                                }, throwable -> {
                                    // Manejar cualquier error que pueda ocurrir durante la inserción o actualización
                                });
                    }
                    scoreDao.getAll()
                            .subscribeOn(Schedulers.io()) // Ejecutar en un hilo de background
                            .observeOn(AndroidSchedulers.mainThread()) // Observar en el hilo principal para actualizar la IU
                            .subscribe(scores -> {
                                for (Score score : scores) {
                                    System.out.println(score.toString());
                                }
                            }, throwable -> {
                                // Manejar cualquier error que ocurra durante la consulta
                                throwable.printStackTrace();
                            });
                } else if (player.playerPoints < dealer.dealerPoints) {
                    message = getString(R.string.blackjack_loose);
                    if (player.playerScore <= 0) {
                        Intent intent = new Intent(BlackJack.this, LostScreen.class);
                        intent.putExtra("maxScore", maxScore);
                        if (hasWriteCalendarPermission()) {
                            agregarEventoAlCalendario("BlackJack", "Has perdido con una puntuacion maxima de: " + String.valueOf(maxScore), System.currentTimeMillis(), System.currentTimeMillis() + 3600000);
                        } else {
                            requestWriteCalendarPermission();
                            agregarEventoAlCalendario("BlackJack", "Has perdido con una puntuacion maxima de: " + String.valueOf(maxScore), System.currentTimeMillis(), System.currentTimeMillis() + 3600000);
                        }
                        startActivity(intent);
                    }
                }

                resultTextView.setText(message);
                restartButton.setVisibility(View.VISIBLE);
                updateScoreUI(scoreTextView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasWriteCalendarPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestWriteCalendarPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, REQUEST_CODE_PERMISSIONS);
    }

    private void resetCardViews(ImageView[] cardViews) {
        for (ImageView cardView : cardViews) {
            cardView.setImageResource(0);
        }
    }

    private void updateScoreUI(TextView scoreTextView) {
        scoreTextView.setText(getString(R.string.blackjack_points) + player.playerScore);
    }

    private void updateCurrentBetUI(TextView currentBetTextView) {
        currentBetTextView.setText(getString(R.string.blackjack_bet) + currentBet);
        currentBetTextView.setVisibility(View.VISIBLE);
    }

    private void loadImageFromAssets(ImageView imageView, String filePath) {
        try {
            InputStream inputStream = getAssets().open(filePath);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.blackjack_errorloadingimage) + filePath, Toast.LENGTH_SHORT).show();
        }
    }

    // Método para reproducir el primer efecto de sonido
    private void cardSound() {
        // Reproducir el efecto de sonido 1
        soundPool.play(soundIdCard, 3, 3, 1, 0, 1);
    }

    // Método para reproducir el segundo efecto de sonido
    private void chipSound() {
        // Reproducir el efecto de sonido 2
        soundPool.play(soundIdChip, 1, 1, 1, 0, 1);
    }

    public void agregarEventoAlCalendario(String titulo, String descripcion, long inicio, long fin) {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
        intent.putExtra("title", "A Test Event from android app");
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, realiza la acción que requiere permisos
                agregarEventoAlCalendario("BlackJack", "Has perdido con una puntuacion maxima de: " + String.valueOf(maxScore), System.currentTimeMillis(), System.currentTimeMillis() + 3600000);
            } else {
                // Permiso denegado, muestra un mensaje o toma una acción alternativa
                Toast.makeText(this, "Se necesitan permisos de calendario para agregar eventos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(int maxScore) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.back)
                .setContentTitle(getString(R.string.blackjack_newmaxscore))
                .setContentText(String.valueOf(maxScore))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (!notificationManager.areNotificationsEnabled()) {
            // Solicitar permiso para mostrar notificaciones
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
            startActivity(intent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());



    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // Permiso ya concedido
            getLastLocation();
        }
    }


    public void onRequestPermissionsLocationResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                getLastLocation();
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations, this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                playerLatitude = location.getLatitude();
                                playerLongitude = location.getLongitude();

                            }
                        }
                    });
        }
    }

}
