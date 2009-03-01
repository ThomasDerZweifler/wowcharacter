package de.stm.android.wowcharacter.data;

import java.util.HashMap;
import java.util.Map;

import de.stm.android.wowcharacter.util.Persister;

/**
 * Modell (singleton), mit Model.getInstance() anzusprechen
 * 
 * @author tfunke
 * 
 */
public class Model {

	/** Name der DB-Datei */
	private final static String DB4OFILENAME = "/sdcard/wow.db4o";
	/** Character Map (key=Region+"."+Server+"."+Name) */
	private Map<String, WOWCharacter> mapCharacters;

	/** Modell */
	private static Model model;

	private Persister persister;

	private Model() {
		mapCharacters = new HashMap<String, WOWCharacter>();
		init();
	}

	/**
	 * Einmalige Instanz zurueckgeben
	 * 
	 * @return
	 */
	public static Model getInstance() {
		if (model != null) {
			return model;
		}
		return new Model();
	}

	/**
	 * Initialisierungen
	 */
	private void init() {
		loadCharacters();
	}

	public Map<String,WOWCharacter> getMapCharacters() {
		return mapCharacters;
	}

	public void deleteAllFavoriteCharacters() {
		persister.deleteAll();
		mapCharacters.clear();
	}
	
	/**
	 * 
	 */
	private void loadCharacters() {
		mapCharacters = new HashMap<String, WOWCharacter>();
		persister = new Persister(DB4OFILENAME);
		mapCharacters = persister.getMapCharacters();
	}
}
