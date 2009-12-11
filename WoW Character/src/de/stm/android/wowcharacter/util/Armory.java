package de.stm.android.wowcharacter.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

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
	}
	
	private static Locale locale = Locale.getDefault();
	
	/**
	 * 
	 * @param character
	 * @param region
	 * @return
	 */
	public StringBuilder search(String character, R.Region region) {
		String server = R.URL_EU;
		if (region == R.Region.US) {
			server = R.URL_US;
		}

		try {
			character = URLEncoder.encode(character, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// should never happen
		}

		String url = server + R.SEARCHPAGE + character + R.SEARCHTYPE_CHAR;
		
		StringBuilder sb = Connection.getXML(url, locale, true);
		
		return sb;
	}
	
	public StringBuilder charactersheet(String character, String server, R.Region region) {
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
	
	public static StringBuilder charactersheet(String urlquery, R.Region region) {
		String server = R.URL_EU;
		if (region == R.Region.US) {
			server = R.URL_US;
		}
		
		String url = server + R.CHARACTERSHEETPAGE + urlquery;
		
		StringBuilder sb = Connection.getXML(url, locale, true);
		
		return sb;
	}
	
	public static StringBuilder iteminfo(int id, R.Region region) {
		String server = R.URL_EU;
		if (region == R.Region.US) {
			server = R.URL_US;
		}
		
		String url = server + R.ITEMINFOPAGE + Integer.toString(id);
		
		StringBuilder sb = Connection.getXML(url, locale, true);
		
		return sb;
	}
}
