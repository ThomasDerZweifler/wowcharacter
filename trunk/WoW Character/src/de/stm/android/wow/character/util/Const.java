package de.stm.android.wow.character.util;

public interface Const {
	static enum Region {
		EU,
		US
	};
	
	final static public String URL_EU = "http://eu.wowarmory.com/";
	final static public String URL_US = "http://www.wowarmory.com/";
	
	final static public String SEARCHPAGE = "search.xml?searchQuery=";
	
	final static public String SEARCHTYPE_ALL = "&searchType=all";
	final static public String SEARCHTYPE_CHAR = "&searchType=characters";
}
