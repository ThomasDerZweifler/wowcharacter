package de.stm.android.wowcharacter.renderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.WOWCharacter;

@SuppressWarnings("unchecked")
public class SearchListAdapter extends ArrayAdapter {
	private static final int res = R.layout.searchlistitem;
	Activity context;
	WOWCharacter[] item;

	public SearchListAdapter(Activity context, WOWCharacter[] item) {
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

		String _level = item[position].get("LEVEL").toString();
		String _race = item[position].get("RACE").toString();
		String _class = item[position].get("CLASS").toString();

		String _guild = item[position].get("GUILD").toString();
		if (_guild.length() != 0) {
			_guild = "Gilde: " + _guild;
		}

		charNameRealm.setText(item[position].toString());
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
