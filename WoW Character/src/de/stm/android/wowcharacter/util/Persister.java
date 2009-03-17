package de.stm.android.wowcharacter.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.os.Environment;
import android.util.Log;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.*;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.ext.Db4oException;
import com.db4o.ext.ObjectNotStorableException;
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
		// testDB4o();
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
	
	public class BitmapTranslator implements ObjectConstructor {

		public Object onInstantiate(ObjectContainer container,
				Object storedObject) {
			try {
				return storedObject;
			} catch (RuntimeException e) {
				throw new Db4oException(e);
			}
		}

		public void onActivate(ObjectContainer container,
				Object applicationObject, Object storedObject) {
			Log.i("onActivate()",applicationObject + "__" + storedObject );
		}

		public Object onStore(ObjectContainer container,
				Object applicationObject) {
			try {
				return applicationObject;
			} catch (RuntimeException e) {
				throw new ObjectNotStorableException(e.getMessage());
			}
		}

		public Class storedClass() {
			return String.class;
		}

	}

	private void load() {
		File root = Environment.getExternalStorageDirectory();
		if (root.canWrite()) {
			try {
				db = Db4o.openFile(dbName);
				
//				Configuration configuration = db.ext().configure();
//				configuration.objectClass( "WOWCharacter" ).cascadeOnUpdate( true );
//				configuration.objectClass( "WOWCharacter" ).cascadeOnDelete( true );
//				ObjectClass oc = configuration.objectClass( "WOWCharacter" );
//				oc.minimumActivationDepth( 20 );
				
//				Configuration conf = db.ext().configure();
//				conf.objectClass(WOWCharacter.class).updateDepth(2);
//				conf.activationDepth(5);
//				conf.objectClass(Bitmap.class).translate(
//						new BitmapTranslator());
//				conf.objectClass(WOWCharacter.class).cascadeOnUpdate(true);
//				conf.objectClass(WOWCharacter.class).cascadeOnActivate(true);
				WOWCharacter proto = new WOWCharacter();// alle Objekte
				ObjectSet<WOWCharacter> result = db.queryByExample(proto);
				while (result.hasNext()) {
					WOWCharacter character = result.next();
					addCharacterToMap(character);
				}
			} catch (Exception e) {
				Log.e(getClass().getName(), "keine SD-Card ansprechbar");
			}
		} else {
			Log.i(getClass().getName(), "SD-Card ist schreibgeschuetzt!");
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
	 * 
	 */
	public void testDB4o() {
		try {
			db = Db4o.openFile(dbName);
			deleteAll();
			Log.i("db4o", "db4o version: " + Db4o.version());
			WOWCharacter c = new WOWCharacter();
			c.put("NAME", "Stefan");
			c.put("REALM", "Lothar");
			c.put("REGION", "EU");
			c.put("CLASS", "Class_Stefan");
			c.put("LEVEL", new Integer(80));
			c.put("XML", "XML_Stefan");
			db.store(c);
			c = new WOWCharacter();
			c.put("NAME", "Thomas");
			c.put("REALM", "Server1");
			c.put("REGION", "EU");
			c.put("CLASS", "Class_Thomas");
			c.put("LEVEL", new Integer(20));
			c.put("XML", "XML_Thomas");
			db.store(c);
			c = new WOWCharacter();
			c.put("NAME", "Goran");
			c.put("REALM", "Lothar");
			c.put("REGION", "US");
			c.put("CLASS", "Class_Goran");
			c.put("LEVEL", new Integer(5));
			c.put("XML", "XML_Goran");
			db.store(c);
			c = new WOWCharacter();
			c.put("NAME", "Anton");
			c.put("REALM", "Server3");
			c.put("REGION", "US");
			c.put("CLASS", "Class_Wincent");
			c.put("LEVEL", new Integer(40));
			c.put("XML", "XML_Wincent");
			db.store(c);
			Log.i("db4o", "...stored characters: ");
			WOWCharacter proto = new WOWCharacter();// alle Objekte
			ObjectSet<WOWCharacter> result = db.queryByExample(proto);
			listResult(result);
			Log.i("db4o", "...get object(s) (Name = Thomas): ");
			result = get("NAME", "Thomas");
			listResult(result);
			// TODO Sort-Direction
		} catch (DatabaseClosedException e1) {
			Log.e("db4o", e1.toString());
		} catch (DatabaseReadOnlyException e1) {
			Log.e("db4o", e1.toString());
		} finally {
			db.close();
		}
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
