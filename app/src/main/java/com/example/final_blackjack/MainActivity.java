package com.example.final_blackjack;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;
import com.example.final_blackjack.utils.FireStore;
import com.example.final_blackjack.model.Player;
import com.example.final_blackjack.services.MusicService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private List<Player> top10Players = new ArrayList<>();
    private static final String TAG = "GoogleActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore dbFirebase;
    public Player player = new Player();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        dbFirebase = FirebaseFirestore.getInstance();
        top10Players = FireStore.getTop10Players(dbFirebase);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.mainmenu_title));
        }
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Asegúrate de que este ID está configurado en tu strings.xml
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // El usuario ha iniciado sesión, actualiza la UI según sea necesario.
            // Por ejemplo, puedes navegar a una nueva actividad o mostrar la información del usuario.
            Log.d(TAG, "User: " + user.getDisplayName());
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            player.AUTH_TOKEN = token;
                            System.out.println("Token: " + token);
                            player.playerName = user.getDisplayName();
                            dbFirebase.collection("players").document(user.getUid()).set(player);
                        } else {
                            // Maneja el error al obtener el token
                            Log.d(TAG, "Error al obtener el token");
                        }
                    });
        } else {
            // El usuario no ha iniciado sesión, mantén la UI actual.
            Log.d(TAG, "No user is signed in.");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_start_game) {
            startGame();
            return true;
        } else if (itemId == R.id.action_options) {
            options();
            return true;
        } else if (itemId == R.id.action_information) {
            return information();
        } else if (itemId == R.id.action_top_scores) {
            return topScores();
        }else if (itemId == R.id.action_history) {
            showHistory();
            return true;
        } else if (itemId == R.id.action_exit) {
            // Acción para salir de la aplicación
            exitApp();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean information() {
        // Acción para abrir la información
        Intent intent = new Intent(this, Information.class);
        startActivity(intent);
        return true;
    }

    private boolean topScores() {
        // Acción para abrir la información
        Intent intent = new Intent(this, TopScores.class);
        intent.putExtra("top10Players", (ArrayList<Player>) top10Players);
        startActivity(intent);
        return true;
    }

    void startGame() {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            intent = new Intent(this, BlackJack.class);
            intent.putExtra("player_name", player.playerName);
        }
        startActivity(intent);
    }

    void options() {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            intent = new Intent(this, Settings.class);
        }
        startActivity(intent);
    }

    void showHistory() {
        Intent intent = new Intent(this, MatchHistory.class);
        startActivity(intent);
    }

    void exitApp() {
        Intent musicService = new Intent(this, MusicService.class);
        stopService(musicService);
        finish();
    }
}
