package de.stm.android.wowcharacter.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.util.Log;
import de.stm.android.wowcharacter.util.Armory;

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
	 * 
	 * @param character
	 * @return
	 * @throws IOException
	 */
	public byte[] getImage(Character character) throws IOException {
		Bitmap bm = Armory.getCharIcon(character);
		if(bm != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			return out.toByteArray();	
		}
		return null;
	}

	/**
	 * Infos zum Character ausgeben
	 * @param character
	 */
	public void getInfos( Character character ) {
		Log.i( getClass().getName(), "getInfos()" + character );
	}
}
