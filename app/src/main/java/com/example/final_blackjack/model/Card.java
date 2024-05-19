package com.example.final_blackjack.model;

public class Card {
    String value;
    String type;

    public Card(String value, String type) {
        this.value = value;
        this.type = type;
    }

    //Fem el parse de la carta per a que ens retorni el valor de la carta
    public int getValue() {
        if("AJQK".contains(value))
            if(value.equals("A"))
                return 11;
            else
                return 10;
        return Integer.parseInt(value);
    }

    public boolean isAce() {
        return value.equals("A");
    }

    public String getImagePath() {
        return "images/"+value + "-" + type + ".png";
    }
    public String getType() {
        return type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return value + "-" + type;
    }



}
