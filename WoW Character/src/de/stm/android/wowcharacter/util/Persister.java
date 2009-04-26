package de.stm.android.wowcharacter.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.query.Predicate;

import de.stm.android.wowcharacter.data.Character;
import de.stm.android.wowcharacter.data.Character.Data;

/**
 * 
 * Speicher der Charactere
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>,
 *
 */
public class Persister {
	private String dbName;
	/** Speicher */
	private ObjectContainer db;
	private Map<String, Character> mapCharacters = new HashMap<String, Character>();
	private int QUANTOR_EXISTS = 1;
	private int QUANTOR_ALL = 2;

	public Persister(String dbName) {
		this.dbName = dbName;

		// deleteAll();

		load();
	}

	/**
	 * Laden der Favoriten
	 */
	private void load() {
		try {
			db = Db4o.openFile(dbName);
			Configuration config = db.ext().configure();      
		    config.objectClass(Character.class).cascadeOnUpdate(true);//wichtig (!), damit HashMap in Test.class persistiert wird!
			
			Character proto = new Character();// alle Objekte
			ObjectSet<Character> result = db.queryByExample(proto);
			while (result.hasNext()) {
				Character character = result.next();
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
	public boolean addCharacterToMap(Character character) {
		String key = character.getKey();
		mapCharacters.put(key, character);
		return true;
	}

	/**
	 * @param character
	 * @return
	 */
	public boolean removeCharacterFromMap(Character character) {
		String key = character.getKey();
		mapCharacters.remove(key);
		return true;
	}

	/**
	 * @return
	 */
	public Map<String, Character> getMapCharacters() {
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
	 * Speichern eines Eintrages (Ueberschreiben eines vorhandenen Eintrags)
	 * 
	 * @param value
	 * @return
	 */
	public void add(Character character) throws Exception {
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
	public boolean remove(Character character) {
		if (db != null) {
			Object region = character.get( Data.REGION );
			Object realm = character.get( Data.REALM );
			Object name = character.get( Data.NAME);
			ObjectSet<Character> result = get(new Object[] { Data.REGION.name(),
					Data.REALM.name(), Data.NAME.name() }, new Object[] { region, realm, name },
					QUANTOR_ALL);
			while (result.hasNext()) {
				db.delete(result.next());
			}
			db.commit();
			if( result.size() > 0 ) {
				removeCharacterFromMap(character);
				Log.i(getClass().getName(), "deleted " + character);
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
	public ObjectSet<Character> get(final Object attribute,
			final Object value) {
		ObjectSet<Character> os = null;
		if (db != null) {
			os = db.query(new Predicate<Character>() {
				public boolean match(Character character) {
					boolean b = false;
					Object o = character.get(Data.valueOf((String) attribute));
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
	public ObjectSet<Character> get(final Object[] attributes,
			final Object[] values, final int quantor) {
		ObjectSet<Character> os = null;
		if (db != null) {
			os = db.query(new Predicate<Character>() {
				public boolean match(Character character) {
					boolean b = false;
					if (quantor == QUANTOR_EXISTS) {
						int i = 0;
						for (Object attribute : attributes) {
							Object o = character.get(Data.valueOf((String)attribute));
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
							Object o = character.get(Data.valueOf((String)attribute));
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
	public void listResult(ObjectSet<Character> result) {
		while (result.hasNext()) {
			Character character = result.next();
			Log.i(getClass().getName(), character.toString());
		}
	}

	/**
	 * 
	 * @param character
	 * @throws Exception
	 */
	public void change(Character character) throws Exception {
		db.store(character);
		db.commit();
	}

}
