package de.stm.android.wowcharacter.renderer;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Character;
import de.stm.android.wowcharacter.data.Character.Data;

/**
 * Charakterzeilenrenderer fuer Suchliste (Items sind Character)
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
@SuppressWarnings("unchecked")
public class SearchListAdapter extends ArrayAdapter {
	private static final int res = R.layout.searchlistitem;
	private Activity context;
	private ArrayList<Character> item;

	/**
	 * @param context
	 * @param item
	 */
	public SearchListAdapter( Activity context, ArrayList<Character> item ) {
		super( context, res, item );
		this.context = context;
		this.item = item;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate( res, null );
		}
		Character character = item.get( position );
		TextView charLevelRaceClass = (TextView)row.findViewById( R.id.CharLevelRaceClass );
		charLevelRaceClass.setText( "" );
		if (character != null) {
			Object o = character.get( Data.LEVEL );
			Object o1 = character.get( Data.RACE );
			Object o2 = character.get( Data.CLASS );
			if (o != null && o1 != null && o2 != null) {
				String level = o.toString();
				String race = o1.toString();
				String _class = o2.toString();
				if (level.length() > 0 || race.length() > 0 || _class.length() > 0) {
					charLevelRaceClass.setText( "Level: " + level + " " + race + "-" + _class );
				}
			}
			TextView charGuild = (TextView)row.findViewById( R.id.CharGuild );
			charGuild.setText( "" );					
			o = character.get( Data.GUILD );
			if (o != null) {
				String guild = o.toString();
				if (guild.length() > 0) {
					String s = context.getString( R.string.searchListAdapter_guild );
					charGuild.setText( s + " " + guild );
				}
			}
			TextView charNameRealm = (TextView)row.findViewById( R.id.CharNameRealm );
			charNameRealm.setText( character.toString() );
		}
		return row;
	}
}
