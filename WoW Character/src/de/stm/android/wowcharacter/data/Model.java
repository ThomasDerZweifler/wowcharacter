package de.stm.android.wowcharacter.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	public void addFavorite( WOWCharacter character ) throws Exception {
//		String url = "http://eu.wowarmory.com/character-sheet.xml?" + character.get("URL");
//		try {
//			StringBuilder sb = Connection.getXML(url, character.get("REGION").toString(),  true);
//		} catch( Exception e ) {
//			
//		}
		URL url;
		InputStream is = null;
		url = new URL("http://eu.wowarmory.com/images/portraits/wow-default/0-1-6.gif");
		Object content = url.getContent();
		is = (InputStream) content;
		Bitmap bm = BitmapFactory.decodeStream(is);
		character.put( "ICON", bm );
		is.close();
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

	public void getInfos( WOWCharacter character ) {
		Log.i( getClass().getName(), "getInfos()" + character );
	}

}
