package de.stefan_oltmann.kaesekaestchen.model;

import android.graphics.Bitmap;

public class Player {

    private String name;
    private Bitmap symbol;
    private int color;
    private GameType gameType;

    public Player(String name, Bitmap symbol, int color, GameType gameType) {
        this.name = name;
        this.symbol = symbol;
        this.color = color;
        this.gameType = gameType;
    }

    public String getName() {
        return name;
    }

    public Bitmap getSymbol() {
        return symbol;
    }

    public int getColor() {
        return color;
    }

    public GameType getGameType() {
        return gameType;
    }

    public boolean isComputerOpponent() {
        return gameType.isComputerOpponent();
    }

    @Override
    public String toString() {
        return "Player [name=" + name + ", color=" + color + "]";
    }

}
