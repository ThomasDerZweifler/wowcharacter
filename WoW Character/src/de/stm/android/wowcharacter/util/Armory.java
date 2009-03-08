package de.stm.android.wowcharacter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import android.util.Log;

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
	 * Gibt die aktuelle Sprache zur�ck
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
	 * XML von URL lesen
	 * 
	 * @param strURL
	 * @param packed
	 * @return
	 */
	private StringBuilder getXML(String strURL, boolean packed) {
		if (!strURL.startsWith("http://")) {
			strURL = "http://" + strURL;
		}

		URLConnection urlConn = null;
		try {
			URL url = new URL(strURL);
			// Wichtig, Zugriff erlauben durch: <uses-permission
			// android:name="android.permission.INTERNET" /> (in manifest)
			urlConn = url.openConnection();
			urlConn.addRequestProperty("User-Agent", "Firefox/3.0");
			urlConn.setRequestProperty("Connection", "close");
			urlConn.setConnectTimeout(1000 * 10 * 60); // timeout after 10
														// minutes
			// urlConn
			// .addRequestProperty(
			// "User-Agent",
			// "Mozilla/5.0 (Windows; U; Windows NT 6.0; rv:1.9.1b3pre)
			// Gecko/20090218 Firefox/3.0 SeaMonkey/2.0a3pre" );
			// urlConn.addRequestProperty( "Accept",
			// "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
			// );
			urlConn.addRequestProperty( "Accept-Charset", "utf-8" );
			// urlConn.addRequestProperty( "Accept-Charset",
			// "ISO-8859-1,ISO-8859-2" );

			urlConn.addRequestProperty("Accept-Language", locale.toString().replace("_", "-"));
			// urlConn.addRequestProperty( "Accept-Language",
			// "en-us;q=0.5,en;q=0.3" );

			urlConn.addRequestProperty("Accept-Encoding", packed ? "gzip,deflate" : "identity");
		} catch (IOException ioe) {
			Log.e("Armory", "Could not connect to server!");
			return null;
		}
		InputStream is = null;
		StringBuilder sb = null;
		try {
			is = urlConn.getInputStream();
			String encoding = urlConn.getContentEncoding();
			if (encoding != null) {
				if (encoding.equalsIgnoreCase("gzip")) {
					is = new GZIPInputStream(is);
				} else if (encoding.equalsIgnoreCase("deflate")) {
					is = new InflaterInputStream(is, new Inflater(true));
				}
			}
			BufferedReader bis = new BufferedReader(new InputStreamReader(is));
			
			sb = new StringBuilder();
			
			String s;
			while ( ( s = bis.readLine() ) != null ) {
				sb.append(s);
			}

			bis.reset();//wichtig, da sonst keine neue Anfrage sofort gestellt werden kann!?

			bis.close();
		} catch (Exception e) {
			/** */
		}
		
		return sb;
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
		
		StringBuilder sb = getXML(url, true);
		
		return sb;
	}
}