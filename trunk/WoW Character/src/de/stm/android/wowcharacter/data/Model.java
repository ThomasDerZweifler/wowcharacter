package de.stm.android.wowcharacter.data;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import de.stm.android.wowcharacter.util.*;
import de.stm.android.wowcharacter.util.Armory.R;
import de.stm.android.wowcharacter.util.Armory.R.Region;

/**
 * Modell (singleton), mit Model.getInstance() anzusprechen
 * 
 * @author tfunke
 */
public class Model {
	/** Name der DB-Datei */
//	private final static String DB4OFILENAME = "/sdcard/wow.db4o";
	private final static String DB4OFILENAME = "/data/data/de.stm.android.wowcharacter/wow.db4o";
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
	public void addFavorite( WOWCharacter character, Activity activity ) throws Exception {
//		String url = "http://eu.wowarmory.com/character-sheet.xml?" + character.get("URL");
//		try {
//			StringBuilder sb = Connection.getXML(url, character.get("REGION").toString(),  true);
//		} catch( Exception e ) {
//			
//		}
		URL url;
		InputStream is = null;
		
		String server = R.URL_US;
		String path = "images/portraits/wow-80/";
		String file = character.get("GENDERID") + "-" + character.get("RACEID") + "-" + character.get("CLASSID") + ".gif";
		
		if (character.get("REGION").equals(Region.EU.name()) ) {
			server = R.URL_EU;
		}
		
		int level = (Integer)character.get("LEVEL");
		if (level < 60) {
			path = "images/portraits/wow-default/";
		} else if (level < 70) {
			path = "images/portraits/wow/";
		} else if (level < 80) {
			path = "images/portraits/wow-70/";
		}
		
		url = new URL( server + path + file );
		Object content = url.getContent();
		is = (InputStream) content;
		Bitmap bm = BitmapFactory.decodeStream(is);
		is.close();
		String key = Persister.getKey(character);
//		String keyIcon = key + ".ICON";
				
//		String uri = android.provider.MediaStore.Images.Media.insertImage(activity.getContentResolver(), bm, keyIcon, "icon for " + key);
//		character.put( "ICON", uri );

		int[] pixels = new int[bm.getWidth()*bm.getHeight()];
		bm.getPixels( pixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight() );

		BitmapDb4o bm4o = new BitmapDb4o( key, pixels, bm.getWidth(), bm.getHeight() );

		character.put( "BITMAP", bm4o );

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
