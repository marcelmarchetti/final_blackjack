package com.example.final_blackjack.model;

import java.util.ArrayList;

public class Player {
    public int playerScore = 1000;

    public int playerPoints = 0;
    public int playerAces = 0;
    public ArrayList<Card> playerHand = new ArrayList<Card>();

    public Player() {}

    public int getPlayerPoints() {
        return playerPoints;
    }

    public void setPlayerPoints(int playerPoints) {
        this.playerPoints = playerPoints;
    }

    public int getPlayerAces() {
        return playerAces;
    }

    public void setPlayerAces(int playerAces) {
        this.playerAces = playerAces;
    }

    public ArrayList<Card> getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(ArrayList<Card> playerHand) {
        this.playerHand = playerHand;
    }

    public void addPlayerHand(Card card) {
        playerHand.add(card);
    }
}
