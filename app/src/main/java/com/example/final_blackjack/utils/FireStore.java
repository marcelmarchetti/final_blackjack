package com.example.final_blackjack.utils;

import android.util.Log;

import com.example.final_blackjack.model.Player;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class FireStore {

    public static void insertPlayer(Player player, FirebaseFirestore dbFirebase) {
        dbFirebase.collection("players").document(player.getId()).set(player);
    }
    public void updatePlayer(Player player, FirebaseFirestore dbFirebase) {
        dbFirebase.collection("players").document(player.getId()).set(player);
    }

    public static List<Player> getTop10Players(FirebaseFirestore dbFirebase) {
        List<Player> topPlayers = new ArrayList<>();
        dbFirebase.collection("players")
                .orderBy("maxPlayerScore", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        task1.getResult().getDocuments().forEach(documentSnapshot -> {
                            Player player = documentSnapshot.toObject(Player.class);
                            topPlayers.add(player);
                            System.out.println("Top 10 players: " + topPlayers);
                        });
                        // Aquí puedes hacer lo que necesites con la lista de los 10 mejores jugadores
                    } else {
                        Log.d("1", "Error getting documents: ", task1.getException());
                    }
                });
        return topPlayers;
    }
}
