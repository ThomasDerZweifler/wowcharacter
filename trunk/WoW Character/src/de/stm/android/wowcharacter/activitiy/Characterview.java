package de.stm.android.wowcharacter.activitiy;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Character;
import de.stm.android.wowcharacter.data.Model;
import de.stm.android.wowcharacter.data.Character.Data;
import de.stm.android.wowcharacter.gui.CustomProgressBar;
import de.stm.android.wowcharacter.util.Armory;
import de.stm.android.wowcharacter.util.BitmapDb4o;

/**
 * 
 * Detailansicht eines Charakters
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>,
 * <a href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 * 
 */
public class Characterview extends Activity {
	private Character character;
	private Armory armory = new Armory();
	private Document doc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String sCharacter = getIntent().getStringExtra(Character.ID_WOWCHARACTER);
		character = Model.getInstance().getMapCharacters().get(sCharacter);
		init();
		fillHeader();
		fillDetails();
		fillItems();
	}

	private void init() {
		setContentView( R.layout.characterview );

		String sAppName = getString( R.string.app_name );
		String sTitle = getString( R.string.charview_title );
		setTitle( sAppName + " (" + sTitle + ")" );

		initTabs();
		doc = getXML();
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
				return inflater.inflate(R.layout.characterviewtabitemlist, null);
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
		NodeList nl;
		CustomProgressBar progbar;
		int value_progress;
		int value_max;
		String barname = "";
				
		if (doc == null) {
			// XML holen fehlgeschlagen
			return;
		}
		
		nl = doc.getElementsByTagName("characterTab");
		
		if (nl.getLength() == 0) {
			// keine Charakterdaten vorhanden
			return;
		}
		
		/* erster Balken */
		
		nl = doc.getElementsByTagName("health");
		value_progress = value_max = Integer.parseInt(nl.item(0).getAttributes().getNamedItem("effective").getNodeValue());
		
		progbar = (CustomProgressBar)findViewById(R.id.progress_health);
		progbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_horizontal_life));
		progbar.setMax(value_max);
		progbar.setProgress(value_progress);
		barname = getResources().getString(R.string.charview_health);
		progbar.setProcessingText(barname + ": " + value_progress);
		
		/* zweiter Balken */
		
		String secondType = "";
		int secondText;
		Drawable secondColor;
		
		nl = doc.getElementsByTagName("secondBar");
		secondType = nl.item(0).getAttributes().getNamedItem("type").getNodeValue();
		value_progress = value_max = Integer.parseInt(nl.item(0).getAttributes().getNamedItem("effective").getNodeValue());

		if (secondType.equals("r")) {
			secondColor = getResources().getDrawable(R.drawable.progress_horizontal_rage);
			secondText = R.string.charview_second_rage;
		} else if (secondType.equals("e")) {
			secondColor = getResources().getDrawable(R.drawable.progress_horizontal_energy);
			secondText = R.string.charview_second_energy;
		} else if (secondType.equals("p")) {
			secondColor = getResources().getDrawable(R.drawable.progress_horizontal_runic);
			secondText = R.string.charview_second_runic;
		} else {
			secondColor = getResources().getDrawable(R.drawable.progress_horizontal_mana);
			secondText = R.string.charview_second_mana;
		}
		
		progbar = (CustomProgressBar)findViewById(R.id.progress_res);
		progbar.setProgressDrawable(secondColor);
		progbar.setMax(value_max);
		progbar.setProgress(value_progress);
		barname = getResources().getString(secondText);
		progbar.setProcessingText(barname + ": " + value_progress);
		
		/* dritter und vierter Balken */

		nl = doc.getElementsByTagName("skill");
		
		for (int i = 0; i < nl.getLength(); i++) {
			int bar = (i == 0) ? R.id.progress_prof_one : R.id.progress_prof_two;

			value_progress = Integer.parseInt(nl.item(i).getAttributes().getNamedItem("value").getNodeValue());
			value_max = Integer.parseInt(nl.item(i).getAttributes().getNamedItem("max").getNodeValue());
			barname = nl.item(i).getAttributes().getNamedItem("name").getNodeValue();
			
			progbar = (CustomProgressBar)findViewById(bar);
			progbar.setMax(value_max);
			progbar.setProgress(value_progress);
			progbar.setProcessingText(barname + ": " + value_progress + "/" + value_max);

		}
	}
	
	private void fillItems(){
		
	}
	
	private Document getXML() {
		String xml = (String)character.get(Data.XML);
		
		if (xml == null || xml.length() == 0) {
			StringBuilder sb = armory.charactersheet((String)character.get(Data.URL), Armory.R.Region.valueOf((String)character.get(Data.REGION)));
			if(sb != null) {
				xml = sb.toString();
				character.put(Data.XML, xml);

				try {
					Model.getInstance().changeCharacter(character);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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
