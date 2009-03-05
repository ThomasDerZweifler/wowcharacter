package de.stm.android.wowcharacter.util;

import java.io.File;
import java.util.*;

import android.os.Environment;
import android.util.Log;

import com.db4o.*;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.ext.DatabaseReadOnlyException;
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

	public Persister( String dbName ) {
		this.dbName = dbName;
		load();
		// testDB4o();
	}

	private void load() {
		File root = Environment.getExternalStorageDirectory();
		if (root.canWrite()) {
			try {
				db = Db4o.openFile( dbName );
				WOWCharacter proto = new WOWCharacter();// alle Objekte
				ObjectSet<WOWCharacter> result = db.queryByExample( proto );
				while (result.hasNext()) {
					WOWCharacter character = result.next();
					addCharacterToMap( character );
				}
			} catch (Exception e) {
				Log.e( getClass().getName(), "keine SD-Card ansprechbar" );
			}
		} else {
			Log.i( getClass().getName(), "SD-Card ist schreibgeschuetzt!" );
		}
	}

	/**
	 * @param character
	 * @return
	 */
	public boolean addCharacterToMap( WOWCharacter character ) {
		String region = character.get( "REGION" ).toString();
		String server = character.get( "SERVER" ).toString();
		String name = character.get( "NAME" ).toString();
		String key = region + "." + server + "." + name;
		mapCharacters.put( key, character );
		return true;
	}

	/**
	 * @param character
	 * @return
	 */
	public boolean removeCharacterFromMap( WOWCharacter character ) {
		String region = character.get( "REGION" ).toString();
		String server = character.get( "SERVER" ).toString();
		String name = character.get( "NAME" ).toString();
		String key = region + "." + server + "." + name;
		mapCharacters.remove( key );
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
			db = Db4o.openFile( dbName );
			deleteAll();
			Log.i( "db4o", "db4o version: " + Db4o.version() );
			WOWCharacter c = new WOWCharacter();
			c.put( "NAME", "Stefan" );
			c.put( "SERVER", "Lothar" );
			c.put( "REGION", "EU" );
			c.put( "CLASS", "Class_Stefan" );
			c.put( "LEVEL", new Integer( 80 ) );
			c.put( "XML", "XML_Stefan" );
			db.store( c );
			c = new WOWCharacter();
			c.put( "NAME", "Thomas" );
			c.put( "SERVER", "Server1" );
			c.put( "REGION", "EU" );
			c.put( "CLASS", "Class_Thomas" );
			c.put( "LEVEL", new Integer( 20 ) );
			c.put( "XML", "XML_Thomas" );
			db.store( c );
			c = new WOWCharacter();
			c.put( "NAME", "Goran" );
			c.put( "SERVER", "Lothar" );
			c.put( "REGION", "US" );
			c.put( "CLASS", "Class_Goran" );
			c.put( "LEVEL", new Integer( 5 ) );
			c.put( "XML", "XML_Goran" );
			db.store( c );
			c = new WOWCharacter();
			c.put( "NAME", "Anton" );
			c.put( "SERVER", "Server3" );
			c.put( "REGION", "US" );
			c.put( "CLASS", "Class_Wincent" );
			c.put( "LEVEL", new Integer( 40 ) );
			c.put( "XML", "XML_Wincent" );
			db.store( c );
			Log.i( "db4o", "...stored characters: " );
			WOWCharacter proto = new WOWCharacter();// alle Objekte
			ObjectSet<WOWCharacter> result = db.queryByExample( proto );
			listResult( result );
			Log.i( "db4o", "...get object(s) (Name = Thomas): " );
			result = get( "NAME", "Thomas" );
			listResult( result );
			// TODO Sort-Direction
		} catch (DatabaseClosedException e1) {
			Log.e( "db4o", e1.toString() );
		} catch (DatabaseReadOnlyException e1) {
			Log.e( "db4o", e1.toString() );
		} finally {
			db.close();
		}
	}

	/**
	 * Alle Eintraege loeschen
	 */
	public void deleteAll() {
		if (db != null) {
			ObjectSet<WOWCharacter> result = db.queryByExample( new Object() );
			while (result.hasNext()) {
				db.delete( result.next() );
			}
			db.commit();
			mapCharacters.clear();
		}
	}

	/**
	 * Speichern eines Eintrages
	 * 
	 * @param value
	 * @return
	 */
	public boolean add( WOWCharacter character ) {
		Log.i( getClass().getName(), "add " + character );
		db.store( character );
		db.commit();
		addCharacterToMap( character );
		return true;
	}

	/**
	 * Favorit loeschen
	 * 
	 * @param character
	 */
	public void remove( WOWCharacter character ) {
		if (db != null) {
			Object region = character.get( "REGION" );
			Object server = character.get( "SERVER" );
			Object name = character.get( "NAME" );
			ObjectSet<WOWCharacter> result = get( new Object[] {
					"REGION", "SERVER", "NAME"
			}, new Object[] {
					region, server, name
			}, QUANTOR_ALL );
			while (result.hasNext()) {
				db.delete( result.next() );
			}
			db.commit();
			removeCharacterFromMap( character );
		}
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
	public ObjectSet<WOWCharacter> get( final Object attribute, final Object value ) {
		ObjectSet<WOWCharacter> os = null;
		if (db != null) {
			os = db.query( new Predicate<WOWCharacter>() {
				public boolean match( WOWCharacter character ) {
					boolean b = false;
					Object o = character.get( attribute );
					if (o != null) {
						b = o.equals( value );
					}
					return b;
				}
			} );
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
	public ObjectSet<WOWCharacter> get( final Object[] attributes, final Object[] values,
			final int quantor ) {
		ObjectSet<WOWCharacter> os = null;
		if (db != null) {
			os = db.query( new Predicate<WOWCharacter>() {
				public boolean match( WOWCharacter character ) {
					boolean b = false;
					if (quantor == QUANTOR_EXISTS) {
						int i = 0;
						for (Object attribute : attributes) {
							Object o = character.get( attribute );
							if (o != null) {
								b = o.equals( values[i] );
								// mindestens eine Uebereinstimmung, dann keine weiteren Tests
								break;
							}
							i++;
						}
					} else if (quantor == QUANTOR_ALL) {
						int i = 0;
						b = true;
						for (Object attribute : attributes) {
							Object o = character.get( attribute );
							if (o != null) {
								if (!o.equals( values[i] )) {
									b = false;
									break;
								}
							}
							i++;
						}
					}
					return b;
				}
			} );
		}
		return os;
	}

	/**
	 * @param result
	 */
	public void listResult( ObjectSet<WOWCharacter> result ) {
		while (result.hasNext()) {
			WOWCharacter character = result.next();
			Log.i( getClass().getName(), character.toString() );
		}
	}
}
