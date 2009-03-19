package de.stm.android.wowcharacter.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.db4o.*;
import com.db4o.query.Predicate;

import de.stm.android.wowcharacter.data.WOWCharacter;

/**
 * Speicher der Charactere
 * 
 * @author tfunke
 */
public class Persister {
	private String dbName;
	/** Speicher */
	private ObjectContainer db;
	private Map<String, WOWCharacter> mapCharacters = new HashMap<String, WOWCharacter>();
	private int QUANTOR_EXISTS = 1;
	private int QUANTOR_ALL = 2;

	public Persister(String dbName) {
		this.dbName = dbName;

		// deleteAll();

		load();
	}

	/**
	 * 
	 * @param character
	 * @return
	 */
	public static String getKey( WOWCharacter character ) {
		String region = character.get("REGION").toString();
		String realm = character.get("REALM").toString();
		String name = character.get("NAME").toString();
		String key = region + "." + realm + "." + name;
		return key;
	}

	/**
	 * Laden der Favoriten
	 */
	private void load() {
		try {
			db = Db4o.openFile(dbName);
			
			WOWCharacter proto = new WOWCharacter();// alle Objekte
			ObjectSet<WOWCharacter> result = db.queryByExample(proto);
			while (result.hasNext()) {
				WOWCharacter character = result.next();
				addCharacterToMap(character);
			}
		} catch (Exception e) {
			Log.e(getClass().getName(), "db4o error");
		}
	}

	/**
	 * @param character
	 * @return
	 */
	public boolean addCharacterToMap(WOWCharacter character) {
		String key = getKey(character);
		mapCharacters.put(key, character);
		return true;
	}

	/**
	 * @param character
	 * @return
	 */
	public boolean removeCharacterFromMap(WOWCharacter character) {
		String key = getKey(character);
		mapCharacters.remove(key);
		return true;
	}

	/**
	 * @return
	 */
	public Map<String, WOWCharacter> getMapCharacters() {
		return mapCharacters;
	}

	/**
	 * Alle Eintraege loeschen
	 */
	public void deleteAll() {
		if (db != null) {
			db.close();
		}
		new File(dbName).delete();
		mapCharacters.clear();
		// neue db wieder zur Verfuegung stellen
		db = Db4o.openFile(dbName);
	}

	/**
	 * Speichern eines Eintrages
	 * 
	 * @param value
	 * @return
	 */
	public void add(WOWCharacter character) throws Exception {
		db.store(character);
		db.commit();
		addCharacterToMap(character);
		Log.i(getClass().getName(), "stored " + character);
	}

	/**
	 * Favorit loeschen
	 * 
	 * @param character
	 * @return true, wenn Character(s) geloescht werden konnte(en)
	 */
	public boolean remove(WOWCharacter character) {
		if (db != null) {
			Object region = character.get("REGION");
			Object realm = character.get("REALM");
			Object name = character.get("NAME");
			ObjectSet<WOWCharacter> result = get(new Object[] { "REGION",
					"REALM", "NAME" }, new Object[] { region, realm, name },
					QUANTOR_ALL);
			while (result.hasNext()) {
				db.delete(result.next());
			}
			db.commit();
			if( result.size() > 0 ) {
				removeCharacterFromMap(character);
				return true;
			}
		}
		return false;
	}

	/**
	 * Suche eines Characters
	 * 
	 * @param attributes
	 *            Attribute
	 * @param values
	 *            Attributwerte
	 * @return
	 */
	@SuppressWarnings("serial")
	public ObjectSet<WOWCharacter> get(final Object attribute,
			final Object value) {
		ObjectSet<WOWCharacter> os = null;
		if (db != null) {
			os = db.query(new Predicate<WOWCharacter>() {
				public boolean match(WOWCharacter character) {
					boolean b = false;
					Object o = character.get(attribute);
					if (o != null) {
						b = o.equals(value);
					}
					return b;
				}
			});
		}
		return os;
	}

	/**
	 * Suche eines Characters
	 * 
	 * @param attributes
	 *            Attribute
	 * @param values
	 *            Attributwerte
	 * @param quantor
	 *            Existenz- oder Allquantor
	 * @return
	 */
	@SuppressWarnings("serial")
	public ObjectSet<WOWCharacter> get(final Object[] attributes,
			final Object[] values, final int quantor) {
		ObjectSet<WOWCharacter> os = null;
		if (db != null) {
			os = db.query(new Predicate<WOWCharacter>() {
				public boolean match(WOWCharacter character) {
					boolean b = false;
					if (quantor == QUANTOR_EXISTS) {
						int i = 0;
						for (Object attribute : attributes) {
							Object o = character.get(attribute);
							if (o != null) {
								b = o.equals(values[i]);
								// mindestens eine Uebereinstimmung, dann keine
								// weiteren Tests
								break;
							}
							i++;
						}
					} else if (quantor == QUANTOR_ALL) {
						int i = 0;
						b = true;
						for (Object attribute : attributes) {
							Object o = character.get(attribute);
							if (o != null) {
								if (!o.equals(values[i])) {
									b = false;
									break;
								}
							}
							i++;
						}
					}
					return b;
				}
			});
		}
		return os;
	}

	/**
	 * @param result
	 */
	public void listResult(ObjectSet<WOWCharacter> result) {
		while (result.hasNext()) {
			WOWCharacter character = result.next();
			Log.i(getClass().getName(), character.toString());
		}
	}
}
