package de.stm.android.wowcharacter.activitiy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Model;
import de.stm.android.wowcharacter.data.WOWCharacter;
import de.stm.android.wowcharacter.util.BitmapDb4o;

public class Characterview extends Activity {
	WOWCharacter character;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String sCharacter = getIntent().getStringExtra(WOWCharacter.ID_WOWCHARACTER);
		character = Model.getInstance().getMapCharacters().get(sCharacter);
		init();
		fillHeader();
	}

	private void init() {
		setContentView( R.layout.characterview );
		
		TabHost tabs = (TabHost) findViewById(R.id.CharacterViewTab);
		tabs.setup();

		TabHost.TabSpec spec = tabs.newTabSpec("details");
		spec.setContent(R.id.characterviewtabstats);
		spec.setIndicator("Details");
		tabs.addTab(spec);

		spec = tabs.newTabSpec("items");
		spec.setContent(R.id.characterviewtabitems);
		spec.setIndicator("Items");
		tabs.addTab(spec);

		tabs.setCurrentTab(0);
	}
	
	private void fillHeader() {
		if (character != null) {
			Object o = character.get( "BITMAP" );
			if (o instanceof BitmapDb4o) {
				ImageView charImage = (ImageView)findViewById( R.id.CharImage );
				BitmapDb4o bmDb4o = (BitmapDb4o)o;
				int[] pixels = bmDb4o.getPixels();
				int width = bmDb4o.getWidth();
				int height = bmDb4o.getHeight();
				Bitmap bm = Bitmap.createBitmap( pixels, 0, width, width, height,
						Bitmap.Config.ARGB_8888 );// TODO Modus noch abspeichern
				charImage.setImageBitmap( bm );
			}
			o = character.get( "LEVEL" );
			Object o1 = character.get( "RACE" );
			Object o2 = character.get( "CLASS" );
			if (o != null && o1 != null && o2 != null) {
				String level = o.toString();
				String race = o1.toString();
				String _class = o2.toString();
				if (level.length() > 0 || race.length() > 0 || _class.length() > 0) {
					TextView charLevelRaceClass = (TextView)findViewById( R.id.CharLevelRaceClass );
					charLevelRaceClass.setText( "Level: " + level + " " + race + "-" + _class );
				}
			}
			o = character.get( "GUILD" );
			if (o != null) {
				String guild = o.toString();
				if (guild.length() > 0) {
					TextView charGuild = (TextView)findViewById( R.id.CharGuild );
					String s = getString( R.string.searchListAdapter_guild );
					charGuild.setText( s + " " + guild );
				}
			}
			TextView charNameRealm = (TextView)findViewById( R.id.CharNameRealm );
			charNameRealm.setText( character.toString() );
		}		
	}
}
