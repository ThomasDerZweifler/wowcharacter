package de.stm.android.wowcharacter.util;

import java.util.Locale;

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
	}
	
	private Locale locale;
	
	public Armory() {
		locale = Locale.getDefault();
	}
	
	/**
	 * Gibt die aktuelle Sprache zurueck
	 * 
	 * @return 
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Setzt die Sprache in der Armory antworten soll
	 * 
	 * @param locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * 
	 * @param character
	 * @param region
	 * @return
	 */
	public StringBuilder search(String character, R.Region region) {
		String r = R.URL_EU;
		if (region == R.Region.US) {
			r = R.URL_US;
		}

		String url = r + R.SEARCHPAGE + character + R.SEARCHTYPE_CHAR;
		
		StringBuilder sb = Connection.getXML(url, r, true);
		
		return sb;
	}
}
