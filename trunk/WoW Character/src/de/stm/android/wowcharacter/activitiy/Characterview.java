package de.stm.android.wowcharacter.activitiy;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Model;
import de.stm.android.wowcharacter.data.WOWCharacter;
import de.stm.android.wowcharacter.data.WOWCharacter.Data;
import de.stm.android.wowcharacter.util.Armory;
import de.stm.android.wowcharacter.util.BitmapDb4o;

public class Characterview extends Activity {
	private WOWCharacter character;
	private Armory armory = new Armory();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String sCharacter = getIntent().getStringExtra(WOWCharacter.ID_WOWCHARACTER);
		character = Model.getInstance().getMapCharacters().get(sCharacter);
		init();
		fillHeader();
		fillDetails();		
	}

	private void init() {
		setContentView( R.layout.characterview );

		String sAppName = getString( R.string.app_name );
		String sTitle = getString( R.string.charview_title );
		setTitle( sAppName + " (" + sTitle + ")" );

		initTabs();
	}

	private void initTabs() {
		TabHost tabs = (TabHost) findViewById(R.id.CharacterViewTab);
		tabs.setup();

		TabHost.TabSpec spec;
		
		tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				Log.i("Characterview", tabId);
			}
		});

		spec = tabs.newTabSpec("details");
		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				LayoutInflater inflater = getLayoutInflater();
				return inflater.inflate(R.layout.characterviewtabstats, null);
			}
		});
		spec.setIndicator("Details");
		tabs.addTab(spec);

		spec = tabs.newTabSpec("items");
		spec.setContent(new TabHost.TabContentFactory() {
			public View createTabContent(String tag) {
				LayoutInflater inflater = getLayoutInflater();
				return inflater.inflate(R.layout.characterviewtabitems, null);
			}
		});
		spec.setIndicator("Items");
		tabs.addTab(spec);

		tabs.setCurrentTab(0);
	}
	
	private void fillHeader() {
		if (character != null) {
			Object o = character.get( Data.BITMAP );
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
			o = character.get( Data.LEVEL );
			Object o1 = character.get( Data.RACE );
			Object o2 = character.get( Data.CLASS );
			if (o != null && o1 != null && o2 != null) {
				String level = o.toString();
				String race = o1.toString();
				String _class = o2.toString();
				if (level.length() > 0 || race.length() > 0 || _class.length() > 0) {
					TextView charLevelRaceClass = (TextView)findViewById( R.id.CharLevelRaceClass );
					charLevelRaceClass.setText( "Level: " + level + " " + race + "-" + _class );
				}
			}
			o = character.get( Data.GUILD );
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

	private void fillDetails() {
		Document doc = getXML();
		NodeList nl;
				
		if (doc == null) {
			// XML holen fehlgeschlagen
			return;
		}
		
		nl = doc.getElementsByTagName("characterTab");
		
		if (nl.getLength() == 0) {
			// keine Charakterdaten vorhanden
			return;
		}
		
		nl = doc.getElementsByTagName("health");

		int health = Integer.parseInt(nl.item(0).getAttributes().getNamedItem("effective").getNodeValue());
		ProgressBar pb = (ProgressBar)findViewById(R.id.progress_health);
		pb.setMax(health);
		pb.setProgress(health);
		//Log.i("Characterview:interpretXML", nl.item(0).getAttributes().getNamedItem("effective").getNodeValue());
	}
	
	private Document getXML() {
		String xml = (String)character.get(Data.XML);
		
		if (xml == null || xml.length() == 0) {
			xml = armory.charactersheet((String)character.get(Data.URL), Armory.R.Region.valueOf((String)character.get(Data.REGION))).toString();
		}
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(xml.toString())));

			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
