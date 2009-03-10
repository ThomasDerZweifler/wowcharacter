package de.stm.android.wowcharacter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import android.util.Log;

public class Connection {
	/**
	 * XML von URL lesen
	 * 
	 * @param strURL
	 * @param packed
	 * @return
	 */
	public static StringBuilder getXML(String strURL, String locale, boolean packed) {
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

}
