package de.stm.android.wowcharacter.data;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.*;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Persistieren und Bereitstellen der Charakter (Favoriten)
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>,
 */
public class CharactersProvider extends ContentProvider implements ICharactersProvider {
	Uri CONTENT_URI_CHARACTERS;
	private SQLiteDatabase dbCharacters;
	private static final int CHARACTERS = 1;
	private static final int CHARACTERS_ID = 2;
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
		uriMatcher.addURI( PROVIDER_NAME, "characters", CHARACTERS );
		uriMatcher.addURI( PROVIDER_NAME, "characters/#", CHARACTERS_ID );
	}
	private class DatabaseHelper extends SQLiteOpenHelper implements ICharactersProvider {
		DatabaseHelper( Context context ) {
			super( context, DATABASE_NAME, null, DATABASE_VERSION );
		}

		@Override
		public void onCreate( SQLiteDatabase db ) {
			db.execSQL( DATABASE_CREATE_TABLE_CHARACTERS );
		}

		@Override
		public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
			Log.w( "Content provider database", "Upgrading database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data" );
			db.execSQL( DATABASE_DROP_TABLE + TABLE_CHARACTERS );
			onCreate( db );
		}
	}

	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs ) {
		int count = 0;
		switch (uriMatcher.match( uri )) {
		case CHARACTERS:
			count = dbCharacters.delete( TABLE_CHARACTERS, selection, selectionArgs );
			break;
		default:
			throw new IllegalArgumentException( "Unknown URI " + uri );
		}
		getContext().getContentResolver().notifyChange( uri, null );
		return count;
	}

	@Override
	public String getType( Uri uri ) {
		switch (uriMatcher.match( uri )) {
		// ---get all characters---
		case CHARACTERS:
			return "vnd.android.cursor.dir/vnd.wowcharacter.provider.character ";
			// ---get one character ---
		case CHARACTERS_ID:
			return "vnd.android.cursor.item/vnd.wowcharacter.provider.character ";
		default:
			throw new IllegalArgumentException( "Unsupported URI: " + uri );
		}
	}

	@Override
	public Uri insert( Uri uri, ContentValues values ) {
		if (uriMatcher.match( uri ) == CHARACTERS) {
			// ---add a new character---
			long rowID = dbCharacters.insert( TABLE_CHARACTERS, "", values );
			// ---if added successfully---
			if (rowID > 0) {
				Uri _uri = ContentUris.withAppendedId( CONTENT_URI_CHARACTERS, rowID );
				getContext().getContentResolver().notifyChange( _uri, null );
				return _uri;
			}
		}
		throw new SQLException( "Failed to insert row into " + uri );
	}

	@Override
	public boolean onCreate() {
		CONTENT_URI_CHARACTERS = Uri.parse( CONTENT_NAME_CHARACTERS );
		Context context = getContext();
		DatabaseHelper dbHelper = new DatabaseHelper( context );
		dbCharacters = dbHelper.getWritableDatabase();
		return (dbCharacters == null) ? false : true;
	}

	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder ) {
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables( TABLE_CHARACTERS );
		if (sortOrder == null || sortOrder == "")
			sortOrder = Column.NAME.name();
		Cursor c = sqlBuilder.query( dbCharacters, projection, selection, selectionArgs, null,
				null, sortOrder );
		// ---register to watch a content URI for changes---
		c.setNotificationUri( getContext().getContentResolver(), uri );
		return c;
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ) {
		int count = 0;
		switch (uriMatcher.match( uri )) {
		case CHARACTERS:
			count = dbCharacters.update( TABLE_CHARACTERS, values, selection, selectionArgs );
			break;
		case CHARACTERS_ID:
			count = dbCharacters.update( TABLE_CHARACTERS, values, "_id = "
					+ uri.getPathSegments().get( 1 )
					+ (!TextUtils.isEmpty( selection ) ? " AND (" + selection + ')' : ""),
					selectionArgs );
			break;
		default:
			throw new IllegalArgumentException( "Unknown URI " + uri );
		}
		getContext().getContentResolver().notifyChange( uri, null );
		return count;
	}
}
