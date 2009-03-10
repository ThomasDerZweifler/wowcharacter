package de.stm.android.wowcharacter.renderer;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
	Activity context;
	ArrayList<WOWCharacter> item;

	public SearchListAdapter(Activity context, ArrayList<WOWCharacter> item) {
		super(context, res, item);

		this.context = context;
		this.item = item;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(res, null);
		}

		TextView charNameRealm = (TextView) row
				.findViewById(R.id.CharNameRealm);
		TextView charLevelRaceClass = (TextView) row
				.findViewById(R.id.CharLevelRaceClass);
		TextView charGuild = (TextView) row.findViewById(R.id.CharGuild);

		WOWCharacter character = item.get(position);

		Object o = character.get("ICON");
		if( o != null ) {
			Bitmap icon = (Bitmap)o;
			if (icon != null) {
				ImageView charImage = (ImageView) row.findViewById(R.id.CharImage);
				charImage.setImageBitmap(icon);
			}			
		}

		String _level = character.get("LEVEL").toString();
		String _race = character.get("RACE").toString();
		String _class = character.get("CLASS").toString();

		String _guild = character.get("GUILD").toString();
		if (_guild.length() != 0) {
			_guild = "Gilde: " + _guild;
		}

		charNameRealm.setText(character.toString());
		charLevelRaceClass.setText("Level: " + _level + " " + _race + "-"
				+ _class);
		charGuild.setText(_guild);

		GradientDrawable d = new GradientDrawable(
				GradientDrawable.Orientation.BL_TR, new int[] { Color.GRAY,
						Color.LTGRAY });
		d.setCornerRadius(3f);
		// row.setBackgroundDrawable(d);

		return row;
	}
}
