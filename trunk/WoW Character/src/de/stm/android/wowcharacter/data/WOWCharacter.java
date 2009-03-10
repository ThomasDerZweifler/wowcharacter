package de.stm.android.wowcharacter.data;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class WOWCharacter implements
		Comparable<WOWCharacter> {

	Map<Object,Object> map = new HashMap<Object,Object>();
	
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
	
	public Object get(Object key) {
		return map.get(key);
	}
	
	public void put( Object key, Object value ) {
		map.put(key, value);
	}
}
