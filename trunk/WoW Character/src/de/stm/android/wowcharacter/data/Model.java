package de.stm.android.wowcharacter.data;

import java.util.Map;

import android.graphics.drawable.Drawable;
import android.util.Log;
import de.stm.android.wowcharacter.util.Persister;
import de.stm.android.wowcharacter.util.Resource;

/**
 * Modell (singleton), mit Model.getInstance() anzusprechen
 * 
 * @author tfunke
 */
public class Model {
	/** Name der DB-Datei */
	private final static String DB4OFILENAME = "/sdcard/wow.db4o";
	/** Modell */
	private static Model model;
	public Drawable rowBackground;
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
		loadImages();
	}

	private void loadImages() {
		rowBackground = Resource
				.getDrawable( "http://eu.wowarmory.com/images/portraits/wow-default/0-1-6.gif" );
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
	public void addFavorite( WOWCharacter character ) {
		persister.add( character );
	}

	/**
	 * 
	 * @param character
	 */
	public void removeFavorite( WOWCharacter character ) {
		persister.remove( character );		
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

	public void getInfos( WOWCharacter character ) {
		Log.i( getClass().getName(), "getInfos()" + character );
	}

}
