package de.stm.android.wowcharacter.util;

public class SearchResult implements Comparable<SearchResult> {
	public String name = "";
	public String level = "";
	public String realm = "";
	
	@Override
	public String toString() {
		return name + " (" + realm + ")";
	}
	
	public int compareTo(SearchResult another) {
		return this.realm.compareTo(another.realm);
	}
}
