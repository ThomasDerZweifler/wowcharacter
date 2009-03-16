package de.stm.android.wowcharacter.renderer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.WOWCharacter;

@SuppressWarnings("unchecked")
public class SearchListAdapter extends ArrayAdapter {
	private static final int res = R.layout.searchlistitem;
	private Activity context;
	private ArrayList<WOWCharacter> item;

	/**
	 * 
	 * @param context
	 * @param item
	 */
	public SearchListAdapter( Activity context, ArrayList<WOWCharacter> item ) {
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
		WOWCharacter character = item.get( position );
		if (character != null) {
			Object o = character.get( "ICON" );
			if (o != null) {
				Uri imageUri =  Uri.parse(o.toString());
				Bitmap icon;
				try {
					icon = android.provider.MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri );
					ImageView charImage = (ImageView)row.findViewById( R.id.CharImage );
					charImage.setImageBitmap( icon );
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
			o = character.get( "LEVEL" );
			Object o1 = character.get( "RACE" );
			Object o2 = character.get( "CLASS" );
			if (o != null && o1 != null && o2 != null) {
				String level = o.toString();
				String race = o1.toString();
				String _class = o2.toString();
				if (level.length() > 0 || race.length() > 0 || _class.length() > 0) {
					TextView charLevelRaceClass = (TextView)row
							.findViewById( R.id.CharLevelRaceClass );
					charLevelRaceClass.setText( "Level: " + level + " " + race + "-" + _class );
				}
			}
			o = character.get( "GUILD" );
			if (o != null) {
				String guild = o.toString();
				if (guild.length() > 0) {
					TextView charGuild = (TextView)row.findViewById( R.id.CharGuild );
					String s = context.getString( R.string.searchListAdapter_guild );
					charGuild.setText( s + " " + guild );
				}
			}
			TextView charNameRealm = (TextView)row.findViewById( R.id.CharNameRealm );
			charNameRealm.setText( character.toString() );
			// GradientDrawable d = new GradientDrawable(
			// GradientDrawable.Orientation.BL_TR, new int[] { Color.GRAY,
			// Color.LTGRAY });
			// d.setCornerRadius(3f);
			// row.setBackgroundDrawable(d);
		}
		return row;
	}
}
