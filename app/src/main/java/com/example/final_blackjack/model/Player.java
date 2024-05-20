package com.example.final_blackjack.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Player implements Parcelable {

    private String id;

    public String AUTH_TOKEN = "";
    public int playerScore = 1000;

    public String playerName = "";

    public int maxPlayerScore = 1000;

    public int playerPoints = 0;
    public int playerAces = 0;
    public ArrayList<Card> playerHand = new ArrayList<Card>();

    public Player() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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


    protected Player(Parcel in) {
        playerName = in.readString();
        maxPlayerScore = in.readInt();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playerName);
        dest.writeInt(maxPlayerScore);
    }
}
