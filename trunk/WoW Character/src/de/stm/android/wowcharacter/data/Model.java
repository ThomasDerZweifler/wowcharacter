package de.stm.android.wowcharacter.data;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;
import de.stm.android.wowcharacter.data.Character.Data;
import de.stm.android.wowcharacter.util.*;
import de.stm.android.wowcharacter.util.Armory.R;
import de.stm.android.wowcharacter.util.Armory.R.Region;

/**
 * Modell (singleton), mit Model.getInstance() anzusprechen
 * 
 * @author <a href="mailto:tfunke@icubic.de">Thomas Funke</a>
 */
public class Model {
	/** Name der DB-Datei */
	// private final static String DB4OFILENAME = "/sdcard/wow.db4o";
	private final static String DB4OFILENAME = "/data/data/de.stm.android.wowcharacter/wow.db4o";
	/** Modell */
	private static Model model;
	/** Persister */
	private Persister persister;

	private Model() {
		init();
	}

	/**
	 * Einmalige Instanz zurueckgeben
	 * 
	 * @return
	 */
	public static Model getInstance() {
		if (model == null) {
			model = new Model();
		}
		return model;
	}

	/**
	 * Initialisierungen
	 */
	private void init() {
		loadCharacters();
	}

	/**
	 * @return
	 */
	public Map<String, Character> getMapCharacters() {
		return persister.getMapCharacters();
	}

	/**
	 * Character ("Platzhalter", weitere Infos noch abzurufen) als Favorit speichern
	 * 
	 * @param character
	 * @throws Exception
	 */
	public void addFavorite( Character character ) throws Exception {
		String server = R.URL_US;
		String path = "images/portraits/wow-80/";
		String file = character.get( Data.GENDERID ) + "-" + character.get( Data.RACEID ) + "-"
				+ character.get( Data.CLASSID ) + ".gif";
		if (character.get( Data.REGION ).equals( Region.EU.name() )) {
			server = R.URL_EU;
		}
		int level = (Integer)character.get( Data.LEVEL );
		if (level < 60) {
			path = "images/portraits/wow-default/";
		} else if (level < 70) {
			path = "images/portraits/wow/";
		} else if (level < 80) {
			path = "images/portraits/wow-70/";
		}
		try {
			URL url = new URL( server + path + file );
			Bitmap bm = Connection.getBitmap( url );
			int[] pixels = new int[bm.getWidth() * bm.getHeight()];
			bm.getPixels( pixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight() );
			String key = character.getKey();
			BitmapDb4o bm4o = new BitmapDb4o( key, pixels, bm.getWidth(), bm.getHeight() );
			character.put( Data.BITMAP, bm4o );
			
		} catch (IOException ioe) {
			Log.i( getClass().getName(), "bitmap not loaded" );
		}
		persister.add( character );
	}

	/**
	 * Favorit loeschen
	 * 
	 * @param character
	 * @return true, wenn Character(s) geloescht werden konnte(en)
	 */
	public boolean removeFavorite( Character character ) {
		return persister.remove( character );
	}

	/**
	 *
	 */
	public void deleteAllFavoriteCharacters() {
		persister.deleteAll();
	}

	/**
	 * 
	 */
	private void loadCharacters() {
		persister = new Persister( DB4OFILENAME );
	}

	/**
	 * 
	 * @param character
	 */
	public void getInfos( Character character ) {
		Log.i( getClass().getName(), "getInfos()" + character );
	}

	public void changeCharacter(Character character) throws Exception {
		persister.change(character);
	}
}
