package de.stm.android.wowcharacter.renderer;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.*;
import android.widget.*;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.ICharactersProvider;

/**
 * Charakterzeilenrenderer fuer Favoritenliste (Items sind Cursor)
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
public class FavoriteListAdapter extends SimpleCursorAdapter implements ICharactersProvider {
	private int res;
	private Activity context;

	/**
	 * @param context
	 * @param item
	 */
	public FavoriteListAdapter( Activity context, int res, Cursor items, String[] from, int[] to ) {
		super( context, res, items, from, to );
		this.res = res;
		this.context = context;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate( res, null );
		}
		Cursor cursor = getCursor();
		cursor.moveToPosition( position );
		byte[] blob = cursor.getBlob( cursor.getColumnIndex( Column.BITMAP.name() ) );
		Bitmap bitmap = BitmapFactory.decodeByteArray( blob, 0, blob.length );
		ImageView charImage = (ImageView)row.findViewById( R.id.CharImage );
		charImage.setImageBitmap( bitmap );
		String name = cursor.getString( cursor.getColumnIndex( Column.NAME.name() ) );
		String server = cursor.getString( cursor.getColumnIndex( Column.REALM.name() ) );
		TextView charNameRealm = (TextView)row.findViewById( R.id.CharNameRealm );
		charNameRealm.setText( name + "@" + server );
		Object o = cursor.getString( cursor.getColumnIndex( Column.LEVEL.name() ) );
		Object o1 = cursor.getString( cursor.getColumnIndex( Column.RACE.name() ) );
		Object o2 = cursor.getString( cursor.getColumnIndex( Column.CLASS.name() ) );
		if (o != null && o1 != null && o2 != null) {
			String level = o.toString();
			String race = o1.toString();
			String _class = o2.toString();
			if (level.length() > 0 || race.length() > 0 || _class.length() > 0) {
				TextView charLevelRaceClass = (TextView)row.findViewById( R.id.CharLevelRaceClass );
				charLevelRaceClass.setText( "Level: " + level + " " + race + "-" + _class );
			}
		}
		o = cursor.getString( cursor.getColumnIndex( Column.GUILD.name() ) );
		if (o != null) {
			String guild = o.toString();
			if (guild.length() > 0) {
				TextView charGuild = (TextView)row.findViewById( R.id.CharGuild );
				String s = context.getString( R.string.searchListAdapter_guild );
				charGuild.setText( s + " " + guild );
			}
		}
		return row;
	}
}
