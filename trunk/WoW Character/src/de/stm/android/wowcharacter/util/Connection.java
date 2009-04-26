package de.stm.android.wowcharacter.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.zip.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 
 * Laden von Inhalten aus dem Internet
 * 
 * @author <a href="mailto:tfunke@icubic.de">Thomas Funke</a>
 *
 */
public class Connection {
	/**
	 * XML von URL lesen
	 * 
	 * @param strURL
	 * @param packed
	 * @return
	 */
	public static StringBuilder getXML(String strURL, Locale locale, boolean packed) {
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
			urlConn.addRequestProperty( "Accept-Charset", "utf-8" );
			urlConn.addRequestProperty("Accept-Language", locale.toString().replace("_", "-"));
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
	 * Bild von URL lesen
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Bitmap getBitmap( URL url ) throws IOException {
		Object content = url.getContent();
		InputStream is = (InputStream) content;
		Bitmap bm = BitmapFactory.decodeStream(is);
		is.close();
		return bm;
	}
}
