package com.example.final_blackjack.model;

public class Chip {
    public int value;
    public String imagePath;

    public int width = 100;
    public int height = 100;
    public Chip(int value, String imagePath) {
        this.value = value;
        this.imagePath = imagePath;
    }
}
