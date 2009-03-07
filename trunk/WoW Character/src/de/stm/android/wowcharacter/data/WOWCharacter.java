package de.stm.android.wowcharacter.data;

import java.util.HashMap;

@SuppressWarnings("serial")
public class WOWCharacter extends HashMap<Object, Object> implements
		Comparable<WOWCharacter> {

	public int compareTo(WOWCharacter other) {
		Object o = get("REALM");
		if (o != null) {
			return o.toString().compareTo(other.get("REALM").toString());
		}
		return 0;
	}

	@Override
	public String toString() {
		return get("NAME") + " @ " + get("REALM");
	}
}
