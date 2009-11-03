package de.stm.android.wowcharacter.data;

import android.content.ContentValues;

/**
 * Characterdetails (REGION+REALM+NAME kann als Schluessel verwendet werden)
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
public class Character implements Comparable<Character> {
	/** key=Data.xxx.name(), value (man kann leider nicht von erben, deshalb als inner object) */
	private ContentValues contentValues = new ContentValues();
	public static enum Data {
		NAME, REALM, FACTIONID, LEVEL, GENDERID, RACE, RACEID, CLASS, CLASSID, GUILD, URL, REGION, BITMAP, XML
	}

	/**
	 * Schluessel und Wert speichern, Wert eines vorhandenen Schluessels ueberschreiben
	 * 
	 * @param key
	 * @param value
	 */
	public void put( Object k, Object value ) {
		put( k, value, true );
	}

	/**
	 * Schluessel und Wert speichern, Wert eines vorhandenen Schluessels (nicht)ueberschreiben
	 * 
	 * @param key
	 * @param value
	 * @param overwrite
	 *            Wert eines vorhandenen Schluessels (nicht)ueberschreiben, wenn force = (false)true
	 */
	public void put( Object k, Object value, boolean overwrite ) {
		String key = k.toString();
		boolean keyExists = contentValues.containsKey( key );
		if (value instanceof byte[]) {
			if (!keyExists || overwrite) {
				contentValues.put( key, (byte[])value );
			}
		} else if (value instanceof String) {
			if (!keyExists || overwrite) {
				contentValues.put( key, (String)value );
			}
		} else if (value instanceof Boolean) {
			if (!keyExists || overwrite) {
				contentValues.put( key, (Boolean)value );
			}
		}
	}

	/**
	 * @param key
	 * @return
	 */
	public Object get( Object key ) {
		return contentValues.get( key.toString() );
	}

	@Override
	public String toString() {
		return get( Data.NAME ) + " @ " + get( Data.REALM );
	}

	public ContentValues getContentValues() {
		return contentValues;
	}

	public int compareTo( Character other ) {
		Object o = get( Data.REALM );
		if (o != null) {
			return o.toString().compareTo( other.get( Data.REALM ).toString() );
		}
		return 0;
	}

	/**
	 * @return
	 */
	public String getKey() {
		String region = contentValues.getAsString( Data.REGION.name() );
		String realm = contentValues.getAsString( Data.REALM.name() );
		String name = contentValues.getAsString( Data.NAME.name() );
		String key = region + "." + realm + "." + name;
		return key;
	}
}
