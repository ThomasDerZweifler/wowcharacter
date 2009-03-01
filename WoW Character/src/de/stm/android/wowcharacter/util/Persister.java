package de.stm.android.wowcharacter.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.query.Predicate;

import de.stm.android.wowcharacter.data.WOWCharacter;

/**
 * Speicher der Charactere
 * 
 * @author tfunke
 * 
 */
public class Persister {
	private String dbName;
	/** Speicher */
	private ObjectContainer db;

	private Map<String, WOWCharacter> mapCharacters = new HashMap<String,WOWCharacter>();

	public Persister(String dbName) {
		this.dbName = dbName;
		load();
//		testDB4o();
	}

	private void load() {
		db = Db4o.openFile(dbName);
		WOWCharacter proto = new WOWCharacter();// alle Objekte
		ObjectSet<WOWCharacter> result = db.queryByExample(proto);
		Iterator<WOWCharacter> r = result.iterator();
		while (r.hasNext()) {
			WOWCharacter character = (WOWCharacter) r.next();
			String region = character.get("REGION").toString();
			String server = character.get("SERVER").toString();
			String name = character.get("NAME").toString();
			String key = region + "." + server + "." + name;
			mapCharacters.put(key, character);
		}
	}

	public Map<String, WOWCharacter> getMapCharacters() {
		return mapCharacters;
	}

	/**
	 * 
	 */
	public void testDB4o() {

		db = Db4o.openFile(dbName);

		deleteAll();

		Log.i("db4o", "db4o version: " + Db4o.version());
		try {

			WOWCharacter c = new WOWCharacter();
			c.put("NAME", "Stefan");
			c.put("SERVER", "Lothar");
			c.put("REGION", "EU");
			c.put("CLASS", "Class_Stefan");
			c.put("LEVEL", new Integer(80));
			c.put("XML", "XML_Stefan");
			db.store(c);

			c = new WOWCharacter();
			c.put("NAME", "Thomas");
			c.put("SERVER", "Server1");
			c.put("REGION", "EU");
			c.put("CLASS", "Class_Thomas");
			c.put("LEVEL", new Integer(20));
			c.put("XML", "XML_Thomas");
			db.store(c);

			c = new WOWCharacter();
			c.put("NAME", "Goran");
			c.put("SERVER", "Lothar");
			c.put("REGION", "US");
			c.put("CLASS", "Class_Goran");
			c.put("LEVEL", new Integer(5));
			c.put("XML", "XML_Goran");
			db.store(c);

			c = new WOWCharacter();
			c.put("NAME", "Anton");
			c.put("SERVER", "Server3");
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
	 * 
	 */
	public void deleteAll() {
		ObjectSet<WOWCharacter> result = db.queryByExample(new Object());
		while (result.hasNext()) {
			db.delete(result.next());
		}
		db.commit();
	}

	@SuppressWarnings("serial")
	/** demo (evtl. zu erweitern um mehrere Attribute und All-, Existenzquantor) */
	public ObjectSet<WOWCharacter> get(final Object attribute,
			final Object value) {
		ObjectSet<WOWCharacter> os = db.query(new Predicate<WOWCharacter>() {
			public boolean match(WOWCharacter character) {
				boolean b = false;
				Object o = character.get(attribute);
				if (o != null) {
					b = o.equals(value);
				}
				return b;
			}
		});
		return os;
	}

	/**
	 * 
	 * @param result
	 */
	public void listResult(ObjectSet<WOWCharacter> result) {
		while (result.hasNext()) {
			WOWCharacter test = result.next();
			Log.i("db4o", test.toString());
		}
	}
}
