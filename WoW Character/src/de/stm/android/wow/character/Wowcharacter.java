package de.stm.android.wow.character;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Eintrittsklasse
 * 
 * @author tfunke
 *
 */
public class Wowcharacter extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//zum Splash uebergehen
		Intent intent = new Intent(this,
				de.stm.android.wow.character.Splash.class);
		startActivity(intent);
		//diese Aktivitaet vom "History-Stack" nehmen
		finish();
	}
}