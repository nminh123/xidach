package com.nminh123.xidach;

public class Card
{
    private String value;
    private String type;
    public Card(String value, String type)
    {
        this.value = value;
        this.type = type;
    }

    public String getType(){return type;}

    public int getValue() {
        if ("AJQK".contains(value)) { //A J Q K
            if (value == "A") {
                return 11;
            }
            return 10;
        }
        return Integer.parseInt(value); //2-10
    }

    public boolean isAce() {
        return value == "A";
    }

    public String getImagePath() {
        return "cards/" + toString() + ".png";
    }

    @Override
    public String toString()
    {
        return value + "-" + type;
    }
}