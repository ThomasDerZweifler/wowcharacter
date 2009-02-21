package de.stm.android.wow.character.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import android.util.Log;

public class Armory implements Const {
	/**
	 * XML von URL lesen
	 * 
	 * @param strURL
	 * @param packed
	 * @return
	 */
	private StringBuilder getXML( String strURL, boolean packed ) {
		if (!strURL.startsWith( "http://" )) {
			strURL = "http://" + strURL;
		}
		URLConnection urlConn = null;
		try {
			URL url = new URL( strURL );
			// Wichtig, Zugriff erlauben durch: <uses-permission
			// android:name="android.permission.INTERNET" /> (in manifest)
			urlConn = url.openConnection();
			urlConn.addRequestProperty( "User-Agent", "Firefox/3.0" );
//			urlConn
//					.addRequestProperty(
//							"User-Agent",
//							"Mozilla/5.0 (Windows; U; Windows NT 6.0; rv:1.9.1b3pre) Gecko/20090218 Firefox/3.0 SeaMonkey/2.0a3pre" );
//			urlConn.addRequestProperty( "Accept",
//					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
//			urlConn.addRequestProperty( "Accept-Charset", "ISO-8859-15,utf-8;q=0.7,*;q=0.7" );
//			urlConn.addRequestProperty( "Accept-Charset", "ISO-8859-1,ISO-8859-2" );

			urlConn.addRequestProperty( "Accept-Language", "de-de,de;q=0.8" );
//			urlConn.addRequestProperty( "Accept-Language", "en-us;q=0.5,en;q=0.3" );

			urlConn.addRequestProperty( "Accept-Encoding", packed ? "gzip,deflate" : "identity" );
		} catch (IOException ioe) {
			Log.e( "Search", "Could not connect to server!" );
			return null;
		}
		InputStream is = null;
		InputStreamReader isr = null;
		StringBuilder sb = null;
		try {
			is = urlConn.getInputStream();
			String encoding = urlConn.getContentEncoding();
			if (encoding != null ) {
				if( encoding.equalsIgnoreCase("gzip") ) {
					is = new GZIPInputStream( is );					
				} else if( encoding.equalsIgnoreCase("deflate") ) {
					is = new InflaterInputStream( is, new Inflater( true ) );
				}
			}
			isr = new InputStreamReader( is );
			sb = new StringBuilder();
			char chars[] = new char[1024];
			int len = 0;
			while ((len = isr.read( chars, 0, chars.length )) >= 0) {
				sb.append( chars, 0, len );
			}
		} catch (IOException ioe) {
			Log.e( "Search", "Invalid format!!" );
		} finally {
			try {
				isr.close();
			} catch (Exception e) {
				Log.e( "Search", "finally" + e.getMessage() );
			}
		}
		return sb;
	}

	public StringBuilder search(String character, String region) {
		String url = URL_EU + SEARCHPAGE + character + SEARCHTYPE_ALL;
		return getXML(url, true);
	}

	
}
