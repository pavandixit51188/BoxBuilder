/*
 * Kaesekaestchen
 * A simple Dots'n'Boxes Game for Android
 *
 * Copyright (C) 2011 - 2012 Stefan Oltmann
 *
 * Contact : dotsandboxes@stefan-oltmann.de
 * Homepage: http://www.stefan-oltmann.de/
 *
 * This file is part of Kaesekaestchen.
 *
 * Kaesekaestchen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kaesekaestchen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Kaesekaestchen. If not, see <http://www.gnu.org/licenses/>.
 */
package edu.UTD.ComputerScience;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import edu.UTD.ComputerScience.model.GameType;

/**
 *This Activity is displayed when starting the app .
 *  Here you can select who the two players.
 */
public class HomeActivity extends Activity implements OnClickListener {

    /**
     * If an app store settings ,
     * then must be stored in a "shared preferences " map under a specific key .
     * In this case, we store the game settings .
     */
    public static final String GAME_SETTINGS_KEY = "game_settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        Button playButton = (Button) findViewById(R.id.play);
        playButton.setOnClickListener(this);

        /* Rebuild of settings for the view */
        SharedPreferences settings = getSharedPreferences(GAME_SETTINGS_KEY, MODE_PRIVATE);
        ((Spinner) findViewById(R.id.player_typ_1_spinner)).setSelection(settings.getInt("gameType1", 0));
        ((Spinner) findViewById(R.id.player_typ_2_spinner)).setSelection(settings.getInt("gameType2", 2));
        ((Spinner) findViewById(R.id.field_size_x)).setSelection(settings.getInt("fieldSizeX", 3));
        ((Spinner) findViewById(R.id.field_size_y)).setSelection(settings.getInt("fieldSizeY", 3));
    }

    /**
     * Diese Methode muss überschrieben werden, wenn ein Menü angezeigt werden
     * soll. Die App benutzt dieses um ein Beenden-Menü anzubieten.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    /**
     * Wurde in der Menüleiste eine Option gewählt, wird diese Methode
     * aufgerufen.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.app_exit)
            finish();

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {

        GameType gameType1 = GameType.parse((String) ((Spinner) findViewById(R.id.player_typ_1_spinner)).getSelectedItem());
        GameType gameType2 = GameType.parse((String) ((Spinner) findViewById(R.id.player_typ_2_spinner)).getSelectedItem());

        int fieldSizeX = Integer.parseInt((String) ((Spinner) findViewById(R.id.field_size_x)).getSelectedItem());
        int fieldSizeY = Integer.parseInt((String) ((Spinner) findViewById(R.id.field_size_y)).getSelectedItem());

        /* Werte in Settings speichern */
        SharedPreferences settings = getSharedPreferences(GAME_SETTINGS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("gameType1", ((Spinner) findViewById(R.id.player_typ_1_spinner)).getSelectedItemPosition());
        editor.putInt("gameType2", ((Spinner) findViewById(R.id.player_typ_2_spinner)).getSelectedItemPosition());
        editor.putInt("fieldSizeX", ((Spinner) findViewById(R.id.field_size_x)).getSelectedItemPosition());
        editor.putInt("fieldSizeY", ((Spinner) findViewById(R.id.field_size_y)).getSelectedItemPosition());
        editor.commit();

        /* Intent bauen */
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameType1", gameType1);
        intent.putExtra("gameType2", gameType2);
        intent.putExtra("fieldSizeX", fieldSizeX);
        intent.putExtra("fieldSizeY", fieldSizeY);

        startActivity(intent);
    }

}
