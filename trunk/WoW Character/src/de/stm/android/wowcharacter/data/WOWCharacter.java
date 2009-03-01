package de.stm.android.wowcharacter.data;

import java.util.HashMap;

@SuppressWarnings("serial")
public class WOWCharacter extends HashMap<Object, Object> implements
		Comparable<WOWCharacter> {

	public int compareTo(WOWCharacter other) {
		Object o = get("SERVER");
		if (o != null) {
			return o.toString().compareTo(other.get("SERVER").toString());
		}
		return 0;
	}

	@Override
	public String toString() {
		return get("NAME") + " (" + get("SERVER") + "; " + get("LEVEL") + ")";
	}
}
