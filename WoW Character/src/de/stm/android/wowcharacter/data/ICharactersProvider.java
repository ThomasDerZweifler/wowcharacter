package de.stm.android.wowcharacter.data;

/**
 * Schnittstelle fuer CharactersProvider
 *
 * @see CharactersProvider
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
public interface ICharactersProvider {
	
	public static final String PROVIDER_NAME = "net.wowcharacter.provider";
	public static final String CONTENT_NAME_CHARACTERS = "content://" + PROVIDER_NAME + "/characters";
	
	public static final String DATABASE_NAME = "DB_WOWCharacters";
	public static final int DATABASE_VERSION = 1;

	public static final int TRUE = 1;
	public static final int FALSE = 0;

	public static enum Column {
		IS_FAVOURITE,
		NAME,
		REALM,
		FACTIONID,
		RELEVANCE,
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
	
	public static final String TABLE_CHARACTERS = "CHARACTERS";

	public static final String DATABASE_CREATE_TABLE_CHARACTERS = "CREATE TABLE " + TABLE_CHARACTERS
		+ " (_id integer primary key autoincrement, "
		+ Column.IS_FAVOURITE.name() + " integer, "
		+ Column.NAME.name() + " text not null, "
		+ Column.REALM.name() + " text not null, "
		+ Column.FACTIONID.name() + " text, "
		+ Column.RELEVANCE.name() + " integer, "
		+ Column.LEVEL.name() + " integer, "
		+ Column.GENDERID.name() + " text, "
		+ Column.RACE.name() + " text, "
		+ Column.RACEID.name() + " text, "
		+ Column.CLASS.name() + " text, "
		+ Column.CLASSID.name() + " text, "
		+ Column.GUILD.name() + " text, "
		+ Column.URL.name() + " text, "
		+ Column.REGION.name() + " text not null, "
		+ Column.XML.name() + " blob, "
		+ Column.BITMAP.name() + " blob);";

	public static final String DATABASE_DROP_TABLE = "DROP TABLE IF EXISTS ";

}
