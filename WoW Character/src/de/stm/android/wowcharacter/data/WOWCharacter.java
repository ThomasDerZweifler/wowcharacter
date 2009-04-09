package de.stm.android.wowcharacter.data;

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
	
	public static enum Data {
		NAME,
		REALM,
		FACTIONID,
		LEVEL,
		GENDERID,
		RACE,
		RACEID,
		CLASS,
		CLASSID,
		GUILD,
		URL,
		REGION,
		BITMAP,
		XML
	}
	
	Map<Object,Object> map = new HashMap<Object,Object>();
	
	public int compareTo(WOWCharacter other) {
		Object o = get(Data.REALM);
		if (o != null) {
			return o.toString().compareTo(other.get(Data.REALM).toString());
		}
		return 0;
	}

	@Override
	public String toString() {
		return get(Data.NAME) + " @ " + get(Data.REALM);
	}
	
	public Object get(Data key) {
		return map.get(key.name());
	}
	
	public void put( Data key, Object value ) {
		map.put(key.name(), value);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getKey() {
		String region = get(Data.REGION).toString();
		String realm = get(Data.REALM).toString();
		String name = get(Data.NAME).toString();
		String key = region + "." + realm + "." + name;
		return key;
	}
}
