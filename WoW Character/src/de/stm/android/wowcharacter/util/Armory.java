package de.stm.android.wowcharacter.util;

import java.io.*;
import java.net.*;
import java.util.Locale;

import android.graphics.Bitmap;
import de.stm.android.wowcharacter.data.Character;
import de.stm.android.wowcharacter.data.Character.Data;

/**
 * 
 * Abruf der XML-Daten aus dem Armory
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>,
 * <a href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 *
 */
public class Armory {
	public static class R {
		public static enum Region {
			EU,
			US
		};
		
		final static public String URL_EU = "http://eu.wowarmory.com/";
		final static public String URL_US = "http://www.wowarmory.com/";
		
		final static public String SEARCHPAGE = "search.xml?searchQuery=";
		
		final static public String SEARCHTYPE_ALL = "&searchType=all";
		final static public String SEARCHTYPE_CHAR = "&searchType=characters";

		final static public String CHARACTERSHEETPAGE = "character-sheet.xml?";	

		final static public String ITEMINFOPAGE = "item-info.xml?i=";
		
		final static public String ITEMICON = "wow-icons/_images/51x51/";
		
		final static public String CHARICON = "images/portraits/wow-default/";
		final static public String CHARICON_60 = "images/portraits/wow/";
		final static public String CHARICON_70 = "images/portraits/wow-70/";
		final static public String CHARICON_80 = "images/portraits/wow-80/";
	}
	
	private static Locale locale = Locale.getDefault();

	/**
	 * Gibt die URL des Servers zurueck
	 * @param region
	 * @return
	 */
	private static String getArmoryServerURL(String region) {
		String server = R.URL_EU;
		if (region.equals(R.Region.US.name())) {
			server = R.URL_US;
		}
		return server;
	}

	/**
	 * Suche nach Charaktern
	 * @param character
	 * @param region
	 * @return
	 */
	public static StringBuilder search(String character, R.Region region) {
		String server = getArmoryServerURL(region.name());

		try {
			character = URLEncoder.encode(character, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// should never happen
		}

		String url = server + R.SEARCHPAGE + character + R.SEARCHTYPE_CHAR;
		
		StringBuilder sb = Connection.getXML(url, locale, true);
		
		return sb;
	}
	
	/**
	 * Umwandeln und Erzeugen der URL Query
	 * @param character
	 * @param server
	 * @param region
	 * @return
	 */
	public static StringBuilder charactersheet(String character, String server, R.Region region) {
		try {
			character = URLEncoder.encode(character, "UTF-8");
			server = URLEncoder.encode(server, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// should never happen
		}

		// Sample: r=Lothar&amp;n=Etienne
		String urlquery = "r=" + server + "&amp;n=" + character;
		
		return charactersheet(urlquery, region);
	}
	
	/**
	 * Charcter abrufen
	 * @param urlquery
	 * @param region
	 * @return
	 */
	public static StringBuilder charactersheet(String urlquery, R.Region region) {
		String server = getArmoryServerURL(region.name());
		
		String url = server + R.CHARACTERSHEETPAGE + urlquery;
		
		StringBuilder sb = Connection.getXML(url, locale, true);
		
		return sb;
	}
	
	/**
	 * Item abrufen
	 * @param id
	 * @param region
	 * @return
	 */
	public static StringBuilder iteminfo(int id, R.Region region) {
		String server = getArmoryServerURL(region.name());
		
		String url = server + R.ITEMINFOPAGE + Integer.toString(id);
		
		StringBuilder sb = Connection.getXML(url, locale, true);
		
		return sb;
	}

	/**
	 * Itemicon abrufen
	 * @param iconname
	 * @param region
	 * @return
	 */
	public static Bitmap getItemIcon(String iconname, String region) {
		String server = getArmoryServerURL(region);	
		String iconlocation = server + R.ITEMICON + iconname + ".jpg";
		try {
			return Connection.getBitmap( new URL( iconlocation ) );
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static byte[] getCharIcon(Character character) {
		Bitmap bm = null;
		
		String server = getArmoryServerURL(character.get( Data.REGION ).toString());
		
		String path = R.CHARICON_80;
		String file = character.get( Data.GENDERID ) + "-" + character.get( Data.RACEID ) + "-"
				+ character.get( Data.CLASSID ) + ".gif";
		int level = Integer.parseInt( character.get( Data.LEVEL ).toString() );
		if (level < 60) {
			path = R.CHARICON;
		} else if (level < 70) {
			path = R.CHARICON_60 ;
		} else if (level < 80) {
			path = R.CHARICON_70;
		}
		try {
			bm = Connection.getBitmap( new URL( server + path + file ) );
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}		

		if(bm != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
			return out.toByteArray();	
		}
		
		return null;
	}

}
