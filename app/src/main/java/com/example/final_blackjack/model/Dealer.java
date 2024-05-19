package com.example.final_blackjack.model;

import java.util.ArrayList;

public class Dealer {
    public Card hiddenCard;
    public ArrayList<Card> dealerHand = new ArrayList<Card>();
    public int dealerPoints = 0;
    public int dealerAces = 0;
    public Dealer() {}

    public Card getHiddenCard() {
        return hiddenCard;
    }

    public ArrayList<Card> getDealerHand() {
        return dealerHand;
    }

    public int getDealerPoints() {
        return dealerPoints;
    }

    public int getDealerAces() {
        return dealerAces;
    }

    public void setHiddenCard(Card hiddenCard) {
        this.hiddenCard = hiddenCard;
    }

    public void setDealerHand(ArrayList<Card> dealerHand) {
        this.dealerHand = dealerHand;
    }

    public void setDealerPoints(int dealerPoints) {
        this.dealerPoints = dealerPoints;
    }

    public void setDealerAces(int dealerAces) {
        this.dealerAces = dealerAces;
    }

    public void addDealerHand(Card card) {
        dealerHand.add(card);
    }
}
