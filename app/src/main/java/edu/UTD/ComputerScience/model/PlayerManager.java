
package edu.UTD.ComputerScience.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PlayerManager {

    /** List all Player. */
    private List<Player> playerList = new ArrayList<Player>();

    /** The player who is currently playing */
    private Player currentPlayer;

    public PlayerManager() {
    }

    public void addPlayer(Player player) {
        playerList.add(player);
    }

    public List<Player> getPlayer() {
        return Collections.unmodifiableList(playerList);
    }

    public Player getCurrentPlayer() {
        if (currentPlayer == null)
            throw new RuntimeException("Before answering the player must check at least once have been called!");
        return currentPlayer;
    }

    public void selectNextPlayerLength() {

        int indexActingPlayer = playerList.indexOf(currentPlayer);

        int indexNextPlayer = indexActingPlayer + 1;
        if (indexNextPlayer > playerList.size() - 1)
            indexNextPlayer = 0;

        currentPlayer = playerList.get(indexNextPlayer);
    }

}
