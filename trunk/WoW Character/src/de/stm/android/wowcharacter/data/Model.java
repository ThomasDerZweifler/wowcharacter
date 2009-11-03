package de.stm.android.wowcharacter.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;
import android.util.Log;
import de.stm.android.wowcharacter.data.Character.Data;
import de.stm.android.wowcharacter.util.Connection;
import de.stm.android.wowcharacter.util.Armory.R;
import de.stm.android.wowcharacter.util.Armory.R.Region;

/**
 * Modell (singleton), mit Model.getInstance() anzusprechen
 * 
 * @author <a href="mailto:tfunke@icubic.de">Thomas Funke</a>
 */
public class Model implements ICharactersProvider {
	/** Modell */
	private static Model model;

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
	 * Character ("Platzhalter", weitere Infos noch abzurufen) als Favorit speichern
	 * 
	 * @param character
	 * @throws Exception
	 */
	public void xxx_addFavorite( Character character ) throws Exception {
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
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			character.put(Data.BITMAP, out.toByteArray());		
		} catch (IOException ioe) {
			Log.i( getClass().getName(), "bitmap not loaded" );
		}
	}

	public byte[] getImage(Character character) throws IOException {
		String server = R.URL_US;
		String path = "images/portraits/wow-80/";
		String file = character.get( Data.GENDERID ) + "-" + character.get( Data.RACEID ) + "-"
				+ character.get( Data.CLASSID ) + ".gif";
		if (character.get( Data.REGION ).equals( Region.EU.name() )) {
			server = R.URL_EU;
		}
		int level = Integer.parseInt( character.get( Data.LEVEL ).toString() );
		if (level < 60) {
			path = "images/portraits/wow-default/";
		} else if (level < 70) {
			path = "images/portraits/wow/";
		} else if (level < 80) {
			path = "images/portraits/wow-70/";
		}
		URL url = new URL( server + path + file );
		Bitmap bm = Connection.getBitmap( url );
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
		return out.toByteArray();
	}
	
	/**
	 * Infos zum Character ausgeben
	 * @param character
	 */
	public void getInfos( Character character ) {
		Log.i( getClass().getName(), "getInfos()" + character );
	}
}
