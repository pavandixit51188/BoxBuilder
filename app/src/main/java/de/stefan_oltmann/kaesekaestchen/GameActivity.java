
package de.stefan_oltmann.kaesekaestchen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import de.stefan_oltmann.kaesekaestchen.model.Box;
import de.stefan_oltmann.kaesekaestchen.model.Pitch;
import de.stefan_oltmann.kaesekaestchen.model.Player;
import de.stefan_oltmann.kaesekaestchen.model.PlayerManager;
import de.stefan_oltmann.kaesekaestchen.model.GameType;
import de.stefan_oltmann.kaesekaestchen.model.Stroke;

/**
 * The main Activity that manages the pitch and controls the Gameloop .
 */
public class GameActivity extends Activity {

    private PitchView pitchView;
    private Pitch pitch;
    private PlayerManager playerManager;

    private final Handler    mHandler = new Handler();

    /** This variable controls the game loop thread. */
    private volatile boolean running  = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Bundle intentExtras = getIntent().getExtras();

        GameType gameType1 = (GameType) intentExtras.get("gameType1");
        GameType gameType2 = (GameType) intentExtras.get("gameType2");

        int fieldSizeX = intentExtras.getInt("fieldSizeX");
        int fieldSizeY = intentExtras.getInt("fieldSizeY");

        pitch = Pitch.generate(fieldSizeX, fieldSizeY);
        playerManager = new PlayerManager();

        pitchView = (PitchView) findViewById(R.id.spielfeldView);
        pitchView.init(pitch);

        playerManager.addPlayer(
                new Player(getResources().getString(R.string.player_1_name),
                        BitmapFactory.decodeResource(getResources(), R.drawable.player_symbol_first),
                        getResources().getColor(R.color.player_1_color), gameType1));
        playerManager.addPlayer(
                new Player(getResources().getString(R.string.player_2_name),
                        BitmapFactory.decodeResource(getResources(), R.drawable.player_symbol_second),
                        getResources().getColor(R.color.player_2_color), gameType2));

        startGameLoop();
    }

    @Override
    protected void onStop() {
        running = false;
        super.onStop();
    }

    public void startGameLoop() {
        Thread thread = new Thread(new GameLoopRunnable());
        thread.start();
        running = true;
    }

    private class GameLoopRunnable implements Runnable {

        public void run() {

            /* The first player selection */
            playerManager.selectNextPlayerLength();

            while (!isGameOver()) {

                final Player player = playerManager.getCurrentPlayer();

                /*
                 * Display which player 's turn , and how many of this item has been .
                 */
                mHandler.post(new Runnable() {
                    public void run() {

                        ImageView imageView = (ImageView) findViewById(R.id.currentPlayerSymbol);
                        imageView.setImageBitmap(player.getSymbol());

                        TextView textView = (TextView) findViewById(R.id.displayPoints);
                        textView.setText(String.valueOf(calculateScore(player)));
                    }
                });

                Stroke input = null;

                if (!player.isComputerOpponent()) {

                    pitchView.resetLastEntry();

                    /*
                     * The user now has to make its input.
                     * This Gameloop-thread will now wait for it.
                     * For this is here the wait () / notify () - Java technology used.
                     * as long as no new entry has been made in this thread sleeps now .
                     */
                    while ((input = pitchView.getLastEntry()) == null) {
                        try {
                            synchronized (pitchView) {
                                pitchView.wait();
                            }
                        } catch (InterruptedException ignore) {
                            /*
                             * This case can be ignored.
                             * If the thread suddenly wake up again, he will if no entry is made
                             * ​​directly put to sleep by the surrounding while loop.
                             */
                        }
                    }

                } else {

                    try { /* The user should see the input of the computer. */
                        Thread.sleep(500);
                    } catch (InterruptedException ignore) {
                    }

                    input = computerGegnerZug(player.getGameType());
                }

                updateStroke(input);

                /*
                 * If the activity is ended, also stop this thread. Without this line,
                 * the activity would never end and the thread continues to run until
                 * Android kills them. But we do not want to stand out negatively course .
                 */
                if (!running)
                    return;
            }

            /*
             * If all boxes are filled , the game is over and the "Game Score" can be displayed.
             */
            if (isGameOver()) {

                mHandler.post(new Runnable() {

                    public void run() {

                        Player gewinner = determineWinner();

                        /* Need To fix Hardcoded pictures */
                        int pictureId = 0;
                        if (gewinner.getName().equals(getResources().getString(R.string.player_1_name)))
                            pictureId = R.drawable.pokal_kaese;
                        else
                            pictureId = R.drawable.pokal_maus;

                        AlertDialog alertDialog = new AlertDialog.Builder(GameActivity.this)
                                .setTitle(getResources().getText(R.string.game_score))
                                .setIcon(getResources().getDrawable(pictureId))
                                .setMessage(getGameOverDialogMessage())
                                .setCancelable(false)
                                .setPositiveButton(getResources().getText(R.string.play_again),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                startActivity(getIntent());
                                            }
                                        })
                                .setNegativeButton(getResources().getText(R.string.main_menu),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.dismiss();
                                                GameActivity.this.finish();
                                            }
                                        })
                                .create();

                        alertDialog.show();
                    }
                });
            }
        }

    }

    private String getGameOverDialogMessage() {

        Player winner = determineWinner();

        StringBuilder sb = new StringBuilder();

        sb.append(getResources().getString(R.string.winner) + ": " + winner.getName() + "\n\n");

        for (Player player : playerManager.getPlayer())
            sb.append(player.getName() + ":\t\t" + calculateScore(player) + "\n");

        return sb.toString();
    }

    private Stroke computerGegnerZug(GameType gameType) {

        Stroke strich = waehleLetztenOffenenStrichFuerKaestchen();

        if (strich != null)
            return strich;

        Stroke zufallsStrich = selectRandomStroke();

        /*
         * Die einfache KI wählt einfach irgendeinen Strich, die mittlere KI
         * passt wenigstens auf, dass kein Strich gewählt wird, der beim Zug des
         * Gegners ein Kästchen schließen könnte und diesem damit einen Punkt
         * schenkt.
         */
        if (gameType == GameType.COMPUTER_MEDIUM) {

            int loopCounter = 0;

            while (zufallsStrich.isSurroundingBoxClosed()) {

                zufallsStrich = selectRandomStroke();

                /*
                 * Dies wird maximal 30 Mal versucht. Konnte dann immer noch
                 * keine gefunden werden, gibt es entweder keine mehr oder der
                 * Gegner darf auch mal Glück haben.
                 */
                if (++loopCounter >= 30)
                    break;
            }
        }

        return zufallsStrich;
    }

    private Stroke waehleLetztenOffenenStrichFuerKaestchen() {

        for (Box box : pitch.getOpenBoxList())
            if (box.getStrokeWithoutOwner().size() == 1)
                return box.getStrokeWithoutOwner().get(0);

        return null;
    }

    private Stroke selectRandomStroke() {

        List<Stroke> strokesWithoutOwner = new ArrayList<Stroke>(pitch.getStrokesWithoutOwner());
        Stroke randomStroke = strokesWithoutOwner.get(new Random().nextInt(strokesWithoutOwner.size()));

        return randomStroke;
    }

    private void updateStroke(Stroke stroke) {

        if (stroke.getOwner() != null)
            return;

        Player currentPlayer = playerManager.getCurrentPlayer();

        boolean isClosedBox = pitch.selectStroke(stroke, currentPlayer);

        /*
         * If a box has been closed, one player is off again.
         * Could he not close either , the other player turn again
         */
        if (!isClosedBox)
            playerManager.selectNextPlayerLength();

        pitchView.updateDisplay();
    }

    public boolean isGameOver() {
        return pitch.isAllBoxesHaveOwner();
    }

    public Player determineWinner() {

        Player winner = null;
        int maxPointCount = 0;

        for (Player player : playerManager.getPlayer()) {

            int score = calculateScore(player);

            if (score > maxPointCount) {
                winner = player;
                maxPointCount = score;
            }
        }

        return winner;
    }

    public int calculateScore(Player player) {

        int score = 0;

        for (Box box : pitch.getBoxList())
            if (box.getOwner() == player)
                score++;

        return score;
    }

}