package de.stm.android.wowcharacter.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.drawable.Drawable;

public class Resource {

	/**
	 * Drawable laden und zurueckgeben (TODO Laden und in Map speichern)
	 * 
	 * @param address
	 *            (Webadresse)
	 * @return
	 */
	private static Drawable getDrawable(String address) {
		// Aufruf und Nutzung:
		// Resource.getDrawable("http://eu.wowarmory.com/images/portraits/wow-default/0-1-6.gif");
		// ImageView imgView = new ImageView(this);
		// imgView = (ImageView)findViewById(R.id.imageView);
		// imgView.setImageDrawable(d);

		InputStream is = null;
		try {
			URL url = new URL(address);
			Object content = url.getContent();
			is = (InputStream) content;
			Drawable d = Drawable.createFromStream(is, "src");
			is.close();
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
