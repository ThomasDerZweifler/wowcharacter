package de.stm.android.wowcharacter.activitiy;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Character;
import de.stm.android.wowcharacter.data.ICharactersProvider;
import de.stm.android.wowcharacter.gui.CustomProgressBar;
import de.stm.android.wowcharacter.renderer.ItemListAdapter;
import de.stm.android.wowcharacter.util.Armory;
import de.stm.android.wowcharacter.util.Connection;
import de.stm.android.wowcharacter.util.Armory.R.Region;

/**
 * Detailansicht eines Charakters
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
public class Characterview extends Activity implements ICharactersProvider {
	private Document doc;
	private TabHost tabHost;
	private Cursor cursor;
	/** Karteikarte "Details" gefuellt */
	private boolean initializedTab1 = false;
	/** Karteikarte Details */
	private TabHost.TabSpec specDetails;
	/** Karteikarte Items */
	private TabHost.TabSpec specItems;
	private ListView listViewItems;
	private ItemListAdapter itemListAdapter;
	private View viewItemList;

	/** Nachrichten-Handler dient dem Akoppeln des Threads vom Erneuern der Oberflaeche */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage( Message msg ) {
			Bundle bundle = msg.getData();
			boolean error = bundle.getBoolean( "ERROR" );
			if(!error) {
				Bitmap bitmap = bundle.getParcelable( "BITMAP" );					
				String name = bundle.getString( "NAME" );
				String level = bundle.getString( "LEVEL" );
				Object[] o = new Object[] { bitmap, name, level };
				itemListAdapter.add(o);
				
				//TODO (bug?) Setzen der Karteikartenreiter-Beschriftung [ Items (x/count), wenn fertig geladen nur noch Items (count) ]
//				specItems.setIndicator( "Items (" + (itemNumber+1) + "/" + itemCount + ")" );
			}

			int itemNumber = bundle.getInt( "ITEM_NUMBER" );
			int itemCount = bundle.getInt( "ITEM_COUNT" );
			if(error || (itemNumber == itemCount-1) ) {
				setProgressBarIndeterminateVisibility( false );				
//				specItems.setIndicator( "Items (" + itemCount + ")" );
			}
		}
	};

	/**
	 * Items laden und jedes einzelne dem Handler zum Anzeigen uebergeben
	 * 
	 * <br><br>
	 * korrospondiert mit:
	 * @see Characterview#handler
	 */
	private void readItems() {
		/** Thread der nebenlaeufig die Items laedt */
		Thread thread = new Thread( new Runnable() {
			public void run() {
				NodeList nl = doc.getElementsByTagName( "item" );
				int length = nl.getLength();
				if(length > 0) {
					setProgressBarIndeterminateVisibility( true );
				}
				// jedes Item betrachten
				for (int i = 0; i < length; i++) {
					// Infos fuer ein Item holen
					String id = nl.item( i ).getAttributes().getNamedItem( "id" ).getNodeValue();
					StringBuilder sb = Armory.iteminfo( Integer.parseInt( id ), Region.EU );//TODO Zeit fuer das Erkennen eines Verbindungsverlustes verringern
					if(sb != null) {
						// Icon fuer das Item
						Bitmap bitmap = null;
						try {
							String iconName = "http://eu.wowarmory.com/wow-icons/_images/51x51/"
									+ nl.item( i ).getAttributes().getNamedItem( "icon" )
											.getNodeValue() + ".jpg";
							bitmap = Connection.getBitmap( new URL( iconName ) );
						} catch (MalformedURLException e) {
						} catch (IOException e) {
						}

						Document doc = xmlToDocument( sb.toString() );
						NodeList nl1 = doc.getElementsByTagName( "item" );
						String name = "";
						String level = "";
						for (int j = 0; j < nl1.getLength(); j++) {
							Node n = nl1.item( j );
							if (n.getAttributes().getNamedItem( "id" ).getNodeValue().equals( id )) {
								NamedNodeMap nnm = nl1.item( j ).getAttributes();
								name = nnm.getNamedItem( "name" ).getNodeValue();
								level = nnm.getNamedItem( "level" ).getNodeValue();
								break;
							}
						}
						
						//Item kann hier nicht direkt zum Adapter hinzugefuegt werden (wegen Oberflaechen-Erneuerung),
						// deshalb Abkopplung dieses Thread mittels Nachricht
						Message msg = new Message();
						Bundle bundle = new Bundle();
						bundle.putString( "NAME", name );
						bundle.putString( "LEVEL", level );
						bundle.putParcelable( "BITMAP", bitmap );
						bundle.putInt( "ITEM_COUNT", length );					
						bundle.putInt( "ITEM_NUMBER", i );
						msg.setData( bundle );
						handler.sendMessage( msg );
					}	
					
					else {
						//konnte ein Item nicht geladen werden (Netzwerkverbindung unterbrochen?), dann ist die Wahrscheinlichkeit hoch, dass
						//  folgende Items auch nicht geladen werden koennen, deshalb hier abbrechen

						break;
					} 

				} 
			}
		} );
		thread.start();

	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		init();
		fillHeader();
		initTabs();
		Boolean onlineResults = getIntent().getBooleanExtra( "ONLINE", false );
		if(onlineResults) {
			//nur bei Netzwerkverbindung Items (versuchen zu) laden
			readItems();
		}
	}

	/**
	 * Initialisierungen
	 */
	private void init() {
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );// fuer
		// Fortschrittskreis
		// in
		// Titelzeile
		setContentView( R.layout.characterview );
		setProgressBarIndeterminateVisibility( false );
		Boolean onlineResults = getIntent().getBooleanExtra( "ONLINE", false );
		String sAppName = getString( R.string.app_name );
		String sTitle = getString( R.string.charview_title );
		setTitle( sAppName + " (" + sTitle + ")" + " - " + (onlineResults ? "online" : "offline") );
		String sRegion = getIntent().getStringExtra( Character.Data.REGION.name() );
		String sRealm = getIntent().getStringExtra( Character.Data.REALM.name() );
		String sName = getIntent().getStringExtra( Character.Data.NAME.name() );
		Uri allFavourites = Uri.parse( CONTENT_NAME_FAVOURITES );
		// >>"<< statt >>'<< <-- wichtig, sodass Strings mit >>'<< funktionieren
		String where = Column.REGION.name() + " = \"" + sRegion + "\" AND " + Column.REALM.name()
				+ " = \"" + sRealm + "\" AND " + Column.NAME.name() + " = \"" + sName + "\"";
		cursor = managedQuery( allFavourites, null, where, null, null );
		startManagingCursor( cursor );
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			String xml = new String( cursor.getBlob( cursor.getColumnIndex( Column.XML.name() ) ) );
			doc = xmlToDocument( xml );
		}
	}

	/**
	 * Karteikarten initialisieren
	 */
	private void initTabs() {
		tabHost = (TabHost)findViewById( R.id.CharacterViewTab );
		tabHost.setup();
		tabHost.setOnTabChangedListener( new TabHost.OnTabChangeListener() {
			public void onTabChanged( String tabId ) {
				if (tabId.equals( "details" )) {
					fillDetails();
				} else if (tabId.equals( "items" )) {
				}
			}
		} );
		specDetails = tabHost.newTabSpec( "details" );
		specDetails.setContent( new TabHost.TabContentFactory() {
			public View createTabContent( String tag ) {
				LayoutInflater inflater = getLayoutInflater();
				return inflater.inflate( R.layout.characterviewtabstats, null );
			}
		} );
		specDetails.setIndicator( "Details" );
		tabHost.addTab( specDetails );
		specItems = tabHost.newTabSpec( "items" );
		
		itemListAdapter = new ItemListAdapter( Characterview.this );
		LayoutInflater inflater = getLayoutInflater();
		viewItemList = inflater.inflate( R.layout.characterviewtabitemlist, null );
		listViewItems = (ListView)viewItemList.findViewById( R.id.ItemListView );
		listViewItems.setAdapter( itemListAdapter );//Model an View binden
		
		specItems.setContent( new TabHost.TabContentFactory() {
			public View createTabContent( String tag ) {
				return viewItemList;
			}
		} );
		specItems.setIndicator( "Items" );
		tabHost.addTab( specItems );
		tabHost.setCurrentTab( 0 );
	}

	/**
	 * Kopf fuellen
	 */
	private void fillHeader() {
		if (cursor != null) {
			Object o = cursor.getBlob( cursor.getColumnIndex( Column.BITMAP.name() ) );
			if (o instanceof byte[]) {
				ImageView charImage = (ImageView)findViewById( R.id.CharImage );
				byte[] blob = (byte[])o;
				Bitmap bm = BitmapFactory.decodeByteArray( blob, 0, blob.length );
				charImage.setImageBitmap( bm );
			}
			o = cursor.getString( cursor.getColumnIndex( Column.LEVEL.name() ) );
			Object o1 = cursor.getString( cursor.getColumnIndex( Column.RACE.name() ) );
			Object o2 = cursor.getString( cursor.getColumnIndex( Column.CLASS.name() ) );
			if (o != null && o1 != null && o2 != null) {
				String level = o.toString();
				String race = o1.toString();
				String _class = o2.toString();
				if (level.length() > 0 || race.length() > 0 || _class.length() > 0) {
					TextView charLevelRaceClass = (TextView)findViewById( R.id.CharLevelRaceClass );
					charLevelRaceClass.setText( "Level: " + level + " " + race + "-" + _class );
				}
			}
			o = cursor.getString( cursor.getColumnIndex( Column.GUILD.name() ) );
			if (o != null) {
				String guild = o.toString();
				if (guild.length() > 0) {
					TextView charGuild = (TextView)findViewById( R.id.CharGuild );
					String s = getString( R.string.searchListAdapter_guild );
					charGuild.setText( s + " " + guild );
				}
			}
			TextView charNameRealm = (TextView)findViewById( R.id.CharNameRealm );
			String name = cursor.getString( cursor.getColumnIndex( Column.NAME.name() ) );
			String realm = cursor.getString( cursor.getColumnIndex( Column.REALM.name() ) );
			charNameRealm.setText( name + " @ " + realm );
		}
	}

	/**
	 * Details fuellen
	 */
	private void fillDetails() {
		if (tabHost.getCurrentTab() != 0) {
			return;
		}
		// mehrmaliges Fuellen unterbinden
		if (initializedTab1) {
			return;
		}
		NodeList nl;
		CustomProgressBar progbar;
		int value_progress;
		int value_max;
		String barname = "";
		if (doc == null) {
			// XML holen fehlgeschlagen
			return;
		}
		nl = doc.getElementsByTagName( "characterTab" );
		if (nl.getLength() == 0) {
			// keine Charakterdaten vorhanden
			return;
		}
		/* erster Balken */
		nl = doc.getElementsByTagName( "health" );
		value_progress = value_max = Integer.parseInt( nl.item( 0 ).getAttributes().getNamedItem(
				"effective" ).getNodeValue() );
		progbar = (CustomProgressBar)findViewById( R.id.progress_health );
		progbar.setProgressDrawable( getResources().getDrawable(
				R.drawable.progress_horizontal_life ) );
		progbar.setMax( value_max );
		progbar.setProgress( value_progress );
		barname = getResources().getString( R.string.charview_health );
		progbar.setProcessingText( barname + ": " + value_progress );
		/* zweiter Balken */
		String secondType = "";
		int secondText;
		Drawable secondColor;
		nl = doc.getElementsByTagName( "secondBar" );
		secondType = nl.item( 0 ).getAttributes().getNamedItem( "type" ).getNodeValue();
		value_progress = value_max = Integer.parseInt( nl.item( 0 ).getAttributes().getNamedItem(
				"effective" ).getNodeValue() );
		if (secondType.equals( "r" )) {
			secondColor = getResources().getDrawable( R.drawable.progress_horizontal_rage );
			secondText = R.string.charview_second_rage;
		} else if (secondType.equals( "e" )) {
			secondColor = getResources().getDrawable( R.drawable.progress_horizontal_energy );
			secondText = R.string.charview_second_energy;
		} else if (secondType.equals( "p" )) {
			secondColor = getResources().getDrawable( R.drawable.progress_horizontal_runic );
			secondText = R.string.charview_second_runic;
		} else {
			secondColor = getResources().getDrawable( R.drawable.progress_horizontal_mana );
			secondText = R.string.charview_second_mana;
		}
		progbar = (CustomProgressBar)findViewById( R.id.progress_res );
		progbar.setProgressDrawable( secondColor );
		progbar.setMax( value_max );
		progbar.setProgress( value_progress );
		barname = getResources().getString( secondText );
		progbar.setProcessingText( barname + ": " + value_progress );
		/* dritter und vierter Balken */
		nl = doc.getElementsByTagName( "skill" );
		for (int i = 0; i < nl.getLength(); i++) {
			int bar = (i == 0) ? R.id.progress_prof_one : R.id.progress_prof_two;
			value_progress = Integer.parseInt( nl.item( i ).getAttributes().getNamedItem( "value" )
					.getNodeValue() );
			value_max = Integer.parseInt( nl.item( i ).getAttributes().getNamedItem( "max" )
					.getNodeValue() );
			barname = nl.item( i ).getAttributes().getNamedItem( "name" ).getNodeValue();
			progbar = (CustomProgressBar)findViewById( bar );
			progbar.setMax( value_max );
			progbar.setProgress( value_progress );
			progbar.setProcessingText( barname + ": " + value_progress + "/" + value_max );
		}
		initializedTab1 = true;
	}

	/**
	 * xml zu document wandeln
	 * 
	 * @param xml
	 * @return
	 */
	private Document xmlToDocument( String xml ) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse( new InputSource( new StringReader( xml.toString() ) ) );
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
