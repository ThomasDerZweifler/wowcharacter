package de.stm.android.wowcharacter.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * WOW Character
 * 
 * @version $Revision:  $Date: $
 * @author <a href="mailto:tfunke@icubic.de">Thomas Funke</a>
 *
 */
public class WOWCharacter implements
		Comparable<WOWCharacter> {

	private static final long serialVersionUID = 1L;
	public static final String ID_WOWCHARACTER = "de.stm.android.wowcharacter.data.WOWCharacter";
	
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
	
	/**
	 * 
	 * @return
	 */
	public String getKey() {
		String region = get("REGION").toString();
		String realm = get("REALM").toString();
		String name = get("NAME").toString();
		String key = region + "." + realm + "." + name;
		return key;
	}
}
