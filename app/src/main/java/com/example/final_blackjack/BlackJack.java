package com.example.final_blackjack;

import com.example.final_blackjack.model.Score;
import com.example.final_blackjack.model.Card;
import com.example.final_blackjack.model.Chip;
import com.example.final_blackjack.model.Dealer;
import com.example.final_blackjack.model.Player;
import com.example.final_blackjack.model.db.AppDatabase;
import com.example.final_blackjack.model.db.dao.ScoreDao;

import androidx.annotation.RequiresApi;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


@RequiresApi(api = Build.VERSION_CODES.O)
public class BlackJack extends AppCompatActivity {

    int currentBet = 0;
    ScoreDao scoreDao;

    int maxScore = 1000;
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            db = Room.databaseBuilder(getApplicationContext(),
                    AppDatabase.class, "app-database").build();
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("BlackJack");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        scoreDao = db.scoreDao();

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

        updateScoreUI(scoreTextView);
        updateCurrentBetUI(currentBetTextView);

        loadImageFromAssets(chipsViews[0], chipFifty.imagePath);
        loadImageFromAssets(chipsViews[1], chipHundred.imagePath);
        loadImageFromAssets(chipsViews[2], chipFiveHundred.imagePath);

        hitButton.setEnabled(false);
        stayButton.setEnabled(false);
        resetBetButton.setVisibility(View.VISIBLE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBet > 0) {
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
                    Toast.makeText(BlackJack.this, "Por favor, realiza una apuesta antes de comenzar el juego", Toast.LENGTH_SHORT).show();
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
                placeBet(chipFifty.value);
            }
        });

        chipsViews[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeBet(chipHundred.value);
            }
        });

        chipsViews[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        builder.setMessage("¿Está seguro de que desea salir?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Si el usuario hace clic en "Sí", finalizamos la actividad
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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
                    message = "You Lose!";
                    if(player.playerScore <= 0) {
                        Intent intent = new Intent(BlackJack.this, LostScreen.class);
                        startActivity(intent);
                    }
                } else if (dealer.dealerPoints > 21) {
                    message = "You Win!";
                    player.playerScore += currentBet * 2;
                    if(player.playerScore > maxScore) {
                        maxScore = player.playerScore;
                        Score newScore = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            newScore = new Score(maxScore, LocalDate.now());
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
                    message = "Tie!";
                    player.playerScore += currentBet;
                } else if (player.playerPoints > dealer.dealerPoints) {
                    message = "You Win!";
                    player.playerScore += currentBet * 2;
                    if(player.playerScore > maxScore) {
                        maxScore = player.playerScore;
                        Score newScore = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            newScore = new Score(maxScore, LocalDate.now());
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
                    message = "You Lose!";
                    if(player.playerScore <= 0) {
                        Intent intent = new Intent(BlackJack.this, LostScreen.class);
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

    private void resetCardViews(ImageView[] cardViews) {
        for (ImageView cardView : cardViews) {
            cardView.setImageResource(0);
        }
    }

    private void updateScoreUI(TextView scoreTextView) {
        scoreTextView.setText("Puntos: " + player.playerScore);
    }

    private void updateCurrentBetUI(TextView currentBetTextView) {
        currentBetTextView.setText("Apuesta actual: " + currentBet);
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
            Toast.makeText(this, "No se pudo cargar la imagen: " + filePath, Toast.LENGTH_SHORT).show();
        }
    }
}
