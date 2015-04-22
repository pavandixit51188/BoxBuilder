package edu.UTD.ComputerScience.model;

public enum GameType {

    HUMAN,
    COMPUTER_EASY,
    COMPUTER_MEDIUM,
    COMPUTER_HARD;


    public static GameType parse(String string) {

        if (string == null)
            return null;

        if (string.equals("Human"))
            return HUMAN;

        if (string.equals("Easy"))
            return COMPUTER_EASY;

        if (string.equals("Medium"))
            return COMPUTER_MEDIUM;

        if (string.equals("Hard"))
            return COMPUTER_HARD;

        throw new IllegalArgumentException("Invalid GameType: " + string);
    }

    public boolean isComputerOpponent() {
        return this == GameType.COMPUTER_EASY
                || this == GameType.COMPUTER_MEDIUM
                || this == GameType.COMPUTER_HARD;
    }

}
