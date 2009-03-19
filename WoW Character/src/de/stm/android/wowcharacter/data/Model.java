package de.stm.android.wowcharacter.data;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;
import de.stm.android.wowcharacter.util.*;
import de.stm.android.wowcharacter.util.Armory.R;
import de.stm.android.wowcharacter.util.Armory.R.Region;

/**
 * Modell (singleton), mit Model.getInstance() anzusprechen
 * 
 * @version $Revision: $Date: $
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
	public Map<String, WOWCharacter> getMapCharacters() {
		return persister.getMapCharacters();
	}

	/**
	 * Character ("Platzhalter", weitere Infos noch abzurufen) als Favorit speichern
	 * 
	 * @param character
	 */
	public void addFavorite( WOWCharacter character ) throws Exception {
		String server = R.URL_US;
		String path = "images/portraits/wow-80/";
		String file = character.get( "GENDERID" ) + "-" + character.get( "RACEID" ) + "-"
				+ character.get( "CLASSID" ) + ".gif";
		if (character.get( "REGION" ).equals( Region.EU.name() )) {
			server = R.URL_EU;
		}
		int level = (Integer)character.get( "LEVEL" );
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
			String key = Persister.getKey( character );
			BitmapDb4o bm4o = new BitmapDb4o( key, pixels, bm.getWidth(), bm.getHeight() );
			character.put( "BITMAP", bm4o );
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
	public boolean removeFavorite( WOWCharacter character ) {
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
	public void getInfos( WOWCharacter character ) {
		Log.i( getClass().getName(), "getInfos()" + character );
	}
}
