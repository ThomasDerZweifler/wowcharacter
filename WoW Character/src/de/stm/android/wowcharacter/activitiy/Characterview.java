package de.stm.android.wowcharacter.activitiy;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Character;
import de.stm.android.wowcharacter.data.ICharactersProvider;
import de.stm.android.wowcharacter.gui.CustomProgressBar;
import de.stm.android.wowcharacter.renderer.*;
import de.stm.android.wowcharacter.util.Armory;

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
	/** Karteikarte Values */
	private TabHost.TabSpec specValues;
	private ListView listViewItems;
	private ItemListAdapter itemListAdapter;
	private ExpandableListView listViewValues;
	private ValuesListAdapter valuesListAdapter;
	/** Karteikarte RSS */
	private TabHost.TabSpec specRSS;
	private RSSListAdapter rssListAdapter;
	/** Thread fuer das Laden der Items */
	private Thread threadItems;
	private Button setAsFavourite;
	private TextView charNameRealm;

	/**
	 * Items laden und jedes einzelne dem Handler zum Anzeigen uebergeben <br>
	 * <br>
	 * korrospondiert mit:
	 * 
	 * @see Characterview#handler
	 */
	private void readItems() {
		itemListAdapter.clear();
		itemListAdapter.notifyDataSetChanged();
		if (doc == null) {
			threadItems = null;
			return;
		}
		shutdownThread( threadItems );
		/** Thread der nebenlaeufig die Items laedt */
		threadItems = new Thread( new Runnable() {
			public void run() {
				NodeList nl = doc.getElementsByTagName( "item" );
				int length = nl.getLength();
				if (length > 0) {
					runOnUiThread( new Runnable() {
						public void run() {
							setProgressBarIndeterminateVisibility( true );
						}
					});
				}
				// jedes Item betrachten
				for (int i = 0; i < length; i++) {
					if (Thread.interrupted()) {
						break;
					}
					boolean error = false;
					// FIXME Fehler besser interpretieren, da bei Problemen eine
					// "0" geliefert wird
					NamedNodeMap nnm = nl.item( i ).getAttributes();
					String name = nnm.getNamedItem( "name" ).getNodeValue();
					String level = nnm.getNamedItem( "level" ).getNodeValue();
					Integer rarity = Integer.parseInt( nnm.getNamedItem( "rarity" ).getNodeValue() );
					// Icon laden, bei Fehler ist bitmap = null
					String iconName = nnm.getNamedItem( "icon" ).getNodeValue();
					String region = cursor
							.getString( cursor.getColumnIndex( Column.REGION.name() ) );
					// Icon fuer das Item
					Bitmap bitmap = Armory.getItemIcon( iconName, region );
					
					final int f_i = i;
					final int f_length = length;
					if (!error) {
						final Object[] o = new Object[] {
								bitmap, name, level, rarity
						};
						runOnUiThread( new Runnable() {
							public void run() {
								itemListAdapter.add( o );
								TextView tv = (TextView)tabHost.getTabWidget().getChildAt( 1 ).findViewById(
										android.R.id.title );
								tv.setText( getResources().getString( R.string.charview_tab_items ) + " ("
										+ (f_i + 1) + "/" + f_length + ")" );
								}
						});
					}
					if (error || (i == length - 1)) {
						runOnUiThread( new Runnable() {
							public void run() {
								setProgressBarIndeterminateVisibility( false );
								TextView tv = (TextView)tabHost.getTabWidget().getChildAt( 1 ).findViewById(
										android.R.id.title );
								tv.setText( getResources().getString( R.string.charview_tab_items ) + " ("
										+ f_length + ")" );
								}
						});
					}

					// Item kann hier nicht direkt zum Adapter hinzugefuegt
					// werden (wegen Oberflaechen-Erneuerung),
					// deshalb Abkopplung dieses Thread mittels Nachricht
//					Message msg = new Message();
//					msg.what = MSG_TYPES.ITEM_LOADED.ordinal();
//					Bundle bundle = new Bundle();
//					bundle.putBoolean( "ERROR", error );
//					if (!error) {
//						bundle.putString( "NAME", name );
//						bundle.putString( "LEVEL", level );
//						bundle.putInt( "RARITY", rarity );
//						if (bitmap == null) {
//							bitmap = BitmapFactory.decodeResource( getResources(),
//									R.drawable.question_mark );
//						}
//						bundle.putParcelable( "BITMAP", bitmap );
//						bundle.putInt( "ITEM_COUNT", length );
//						bundle.putInt( "ITEM_NUMBER", i );
//					}
//					
//					msg.setData( bundle );
//					handler.sendMessage( msg );

					if (error) {
						// konnte ein Item nicht geladen werden
						// (Netzwerkverbindung unterbrochen?), dann ist die
						// Wahrscheinlichkeit hoch, dass
						// folgende Items auch nicht geladen werden koennen,
						// deshalb hier abbrechen
						break;
					}
				}// for
				runOnUiThread( new Runnable() {
					public void run() {
						setProgressBarIndeterminateVisibility( false );
					}
				});
			}// run
		} );
		threadItems.start();
	}

	@Override
	protected void onDestroy() {
		shutdownThread( threadItems );
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		shutdownThread( threadItems );
		super.onPause();
	}

	/**
	 * Thread stoppen
	 * 
	 * @param thread
	 */
	private void shutdownThread( Thread thread ) {
		if (thread != null) {
			thread.interrupt();
			// TODO noch zu ueberlegen, wenn Thread nicht beendet wird (dann nur
			// Home Taste mgl.)
			// evtl. Loesung: nur wenn Activity laeuft, Oberflaechenaktivitaeten
			// zulassen
			while (thread.isAlive()) {
			}
		}
	}

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		init();
	}

	@Override
	protected void onStart() {
		super.onStart();
		getCursor();
		fillHeader();
	}

	/**
	 * Initialisierungen
	 */
	private void init() {
		// fuer Fortschrittskreis in Titelzeile
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.characterview );
		setProgressBarIndeterminateVisibility( false );
		getCursor();
		initTabs();
	}

	private void getCursor() {
		Boolean onlineResults = getIntent().getBooleanExtra( "ONLINE", false );
		Boolean temporary = getIntent().getBooleanExtra( "IS_TEMPORARY", true );
		String sAppName = getString( R.string.app_name );
		String sTitle = getString( R.string.charview_title );
		setTitle( sAppName + " (" + sTitle + ")" + " - " + (onlineResults ? "online" : "offline") );
		String sRegion = getIntent().getStringExtra( Character.Data.REGION.name() );
		String sRealm = getIntent().getStringExtra( Character.Data.REALM.name() );
		String sName = getIntent().getStringExtra( Character.Data.NAME.name() );
		Uri allFavourites = Uri.parse( CONTENT_NAME_CHARACTERS );
		// >>"<< statt >>'<< <-- wichtig, sodass Strings mit >>'<< funktionieren
		String where = Column.REGION.name() + " = \"" + sRegion + "\" AND " + Column.REALM.name()
				+ " = \"" + sRealm + "\" AND " + Column.NAME.name() + " = \"" + sName + "\""
				+ " AND IS_FAVOURITE = " + (temporary ? +FALSE : TRUE);
		cursor = managedQuery( allFavourites, null, where, null, null );
		if (cursor != null) {
			startManagingCursor( cursor );
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				byte[] xml = cursor.getBlob( cursor.getColumnIndex( Column.XML.name() ) );
				if (xml != null) {
					doc = xmlToDocument( new String( xml ) );
				}
			}
		}
	}

	/**
	 * Karteikarten initialisieren
	 */
	private void initTabs() {
		String tabText;
		Drawable tabIcon;
		tabHost = (TabHost)findViewById( R.id.CharacterViewTab );
		tabHost.setup();
		tabHost.setOnTabChangedListener( new TabHost.OnTabChangeListener() {
			public void onTabChanged( String tabId ) {
				if (tabId.equals( "details" )) {
					fillDetails();
				} else if (tabId.equals( "items" )) {
					readItems();
				} else if (tabId.equals( "values" )) {
					fillValues();
				} else if (tabId.equals( "rss-feed" )) {
					readRSS();
				}
			}
		} );
		// Details Tab
		specDetails = tabHost.newTabSpec( "details" );
		specDetails.setContent( new TabHost.TabContentFactory() {
			public View createTabContent( String tag ) {
				LayoutInflater inflater = getLayoutInflater();
				return inflater.inflate( R.layout.characterviewtabstats, null );
			}
		} );
		tabText = getResources().getString( R.string.charview_tab_detail );
		tabIcon = getResources().getDrawable( android.R.drawable.ic_menu_view );
		specDetails.setIndicator( tabText, tabIcon );
		tabHost.addTab( specDetails );
		// Items Tab
		specItems = tabHost.newTabSpec( "items" );
		itemListAdapter = new ItemListAdapter( Characterview.this );
		specItems.setContent( new TabHost.TabContentFactory() {
			public View createTabContent( String tag ) {
				LayoutInflater inflater = getLayoutInflater();
				View viewItemList = inflater.inflate( R.layout.characterviewtabitemslist, null );
				listViewItems = (ListView)viewItemList.findViewById( R.id.ItemListView );
				listViewItems.setAdapter( itemListAdapter );// Model an View
				// binden
				return viewItemList;
			}
		} );
		tabText = getResources().getString( R.string.charview_tab_items );
		tabIcon = getResources().getDrawable( android.R.drawable.ic_menu_info_details );
		specItems.setIndicator( tabText, tabIcon );
		tabHost.addTab( specItems );
		// Values - Tab
		specValues = tabHost.newTabSpec( "values" );
		valuesListAdapter = new ValuesListAdapter( Characterview.this );
		specValues.setContent( new TabHost.TabContentFactory() {
			public View createTabContent( String tag ) {
				listViewValues = new ExpandableListView( Characterview.this );
				listViewValues.setAdapter( valuesListAdapter );// Model an View
				return listViewValues;
			}
		} );
		tabText = getResources().getString( R.string.charview_tab_values );
		tabIcon = getResources().getDrawable( android.R.drawable.ic_menu_help );
		specValues.setIndicator( tabText, tabIcon );
		tabHost.addTab( specValues );
		// RSS FEED - Tab
		specRSS = tabHost.newTabSpec( "rss-feed" );
		rssListAdapter = new RSSListAdapter( Characterview.this );
		specRSS.setContent( new TabHost.TabContentFactory() {
			public View createTabContent( String tag ) {
				ListView listViewRSS = new ListView( Characterview.this );
				// listViewRSS.setOnItemClickListener(new OnItemClickListener() {
				// public void onItemClick( AdapterView<?> arg0, View view, int position, long id )
				// {
				// rssListAdapter.setSelectedPosition(position);
				// }
				// });
				listViewRSS.setOnItemSelectedListener( new OnItemSelectedListener() {
					public void onItemSelected( AdapterView<?> arg0, View view, int position,
							long id ) {
						rssListAdapter.setSelectedPosition( position );
					}

					public void onNothingSelected( AdapterView<?> arg0 ) {
						rssListAdapter.setSelectedPosition( -1 );
					}
				} );
				listViewRSS.setFastScrollEnabled( true );
				listViewRSS.setAdapter( rssListAdapter );// Model an View
				return listViewRSS;
			}
		} );
		tabText = getResources().getString( R.string.charview_tab_rss );
		tabIcon = getResources().getDrawable( android.R.drawable.ic_menu_info_details );
		specRSS.setIndicator( tabText, tabIcon );
		tabHost.addTab( specRSS );
		tabHost.setCurrentTab( 0 );
	}

	/**
	 * Kopf fuellen
	 */
	private void fillHeader() {
		if (cursor != null) {
			int index = cursor.getColumnIndex( Column.BITMAP.name() );
			if (index >= 0) {
				Object o = cursor.getBlob( index );
				if (o instanceof byte[]) {
					ImageView charImage = (ImageView)findViewById( R.id.CharImage );
					byte[] blob = (byte[])o;
					Bitmap bm = BitmapFactory.decodeByteArray( blob, 0, blob.length );
					charImage.setImageBitmap( bm );
				}
			}
			Object o = cursor.getString( cursor.getColumnIndex( Column.LEVEL.name() ) );
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
			charNameRealm = (TextView)findViewById( R.id.CharNameRealm );
			String name = cursor.getString( cursor.getColumnIndex( Column.NAME.name() ) );
			String realm = cursor.getString( cursor.getColumnIndex( Column.REALM.name() ) );
			charNameRealm.setText( name + " @ " + realm );
			if (favouriteExists()) {
				setAsFavourite = (Button)findViewById( R.id.setAsFavourite );
				ViewGroup.LayoutParams params = setAsFavourite.getLayoutParams();
				params.height = 0;
				setAsFavourite.requestLayout();					
			} else {
				setAsFavourite = (Button)findViewById( R.id.setAsFavourite );
				setAsFavourite.setOnClickListener( new OnClickListener() {
					public void onClick( View v ) {
						setAsFavourite();
					}
				} );
			}
		}
	}

	/**
	 * Gibt zurueck, ob Charakter als Fovorit existiert
	 * 
	 * @return
	 */
	boolean favouriteExists() {
		Uri CONTENT_URI = Uri.parse( CONTENT_NAME_CHARACTERS );
		String name = cursor.getString( cursor.getColumnIndex( Column.NAME.name() ) );
		String realm = cursor.getString( cursor.getColumnIndex( Column.REALM.name() ) );
		String[] projection = new String[] {
				Column.REALM.name(), Column.NAME.name()
		};
		String where = "NAME LIKE \"" + name + "\" AND REALM LIKE \"" + realm
				+ "\" AND IS_FAVOURITE = " + TRUE;
		Cursor c = managedQuery( CONTENT_URI, projection, where, null, null );
		if (c != null) {
			startManagingCursor( c );
			return c.getCount() > 0;
		}
		return false;
	}

	/**
	 * 
	 */
	protected void setAsFavourite() {
		ContentValues cv = new ContentValues();
		cv.put( Column.IS_FAVOURITE.name(), true );
		Uri uri = Uri.parse( CONTENT_NAME_CHARACTERS + "/"
				+ cursor.getString( cursor.getColumnIndex( "_id" ) ) );
		int count = getContentResolver().update( uri, cv, null, null );
		if (count > 0) {
			String s = getString( R.string.search_addToFavorites_ok_toast );
			s = s.replace( "%1", charNameRealm.getText() );
			Toast.makeText( this, s, Toast.LENGTH_SHORT ).show();
			setAsFavourite = (Button)findViewById( R.id.setAsFavourite );
			ViewGroup.LayoutParams params = setAsFavourite.getLayoutParams();
			params.height = 0;
			setAsFavourite.requestLayout();					
		}
	}

	/**
	 * Details fuellen
	 */
	private void fillDetails() {
		if (doc == null) {
			// XML holen fehlgeschlagen
			return;
		}
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
		nl = doc.getElementsByTagName( "professions" );
		if (nl.getLength() > 0) {
			nl = nl.item( 0 ).getChildNodes();
			int bar = R.id.progress_prof_one ;
			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item( i ).getNodeType() == Node.ELEMENT_NODE ) {
					value_progress = Integer.parseInt( nl.item( i ).getAttributes().getNamedItem( "value" )
							.getNodeValue() );
					value_max = Integer.parseInt( nl.item( i ).getAttributes().getNamedItem( "max" )
							.getNodeValue() );
					barname = nl.item( i ).getAttributes().getNamedItem( "name" ).getNodeValue();

					progbar = (CustomProgressBar)findViewById( bar );
					progbar.setMax( value_max );
					progbar.setProgress( value_progress );
					progbar.setProcessingText( barname + ": " + value_progress + "/" + value_max );					

					bar = R.id.progress_prof_two;
				} 
			}
		}
		/* Talente */
		int[] idsImage = new int[] {
				R.id.ItemImageTalent1, R.id.ItemImageTalent2
		};
		int[] idsText = new int[] {
				R.id.talent1, R.id.talent2
		};
		nl = doc.getElementsByTagName( "talentSpec" );
		for (int i = 0; i < nl.getLength() && i < 2; i++) {
			String prim = nl.item( i ).getAttributes().getNamedItem( "prim" ).getNodeValue();
			int group = Integer.parseInt( nl.item( i ).getAttributes().getNamedItem( "group" )
					.getNodeValue() );// gibt die
			// Reihenfolge an
			String iconName = nl.item( i ).getAttributes().getNamedItem( "icon" ).getNodeValue();
			String region = cursor.getString( cursor.getColumnIndex( Column.REGION.name() ) );
			Bitmap bitmap = Armory.getItemIcon( iconName, region );
			if (bitmap != null) {
				ImageView img = (ImageView)findViewById( idsImage[group - 1] );
				img.setImageBitmap( bitmap );
			}
			String act = "";
			Node n = nl.item( i ).getAttributes().getNamedItem( "active" );
			if (n != null) {
				int active = Integer.parseInt( n.getNodeValue() );
				if (active == 1) {
					act = "*";// aktives Talent
				}
			}
			int treeOne = Integer.parseInt( nl.item( i ).getAttributes().getNamedItem( "treeOne" )
					.getNodeValue() );
			int treeTwo = Integer.parseInt( nl.item( i ).getAttributes().getNamedItem( "treeTwo" )
					.getNodeValue() );
			int treeThree = Integer.parseInt( nl.item( i ).getAttributes().getNamedItem(
					"treeThree" ).getNodeValue() );
			TextView tf = (TextView)findViewById( idsText[group - 1] );
			tf.setText( prim + act + " (" + treeOne + "/" + treeTwo + "/" + treeThree + ")" );
		}
		initializedTab1 = true;
	}

	/**
	 * Werte fuellen
	 */
	private void fillValues() {
		/**
		 * @TODO "Zauber" = spell, Nahkampf.Schaden extra auszuwerten, @TODO Lokalisieren
		 */
		String[] groups = {
				"Basiswerte", "Distanzwaffen", "Nahkampf", "Verteidigung"
		};// fuer Reihenfolge
		HashMap<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
		map.put( 0, getChilds( "baseStats" ) );
		map.put( 1, getChilds( "ranged" ) );
		map.put( 2, getChilds( "melee" ) );
		map.put( 3, getChilds( "defenses" ) );
		valuesListAdapter.setValues( groups, map );
		runOnUiThread( new Runnable() {
			public void run() {
				if (!listViewValues.isGroupExpanded( 0 )) {
					listViewValues.expandGroup( 0 );
				}
			}
		} );
	}

	/**
	 * RSS laden und jedes einzelne dem Handler zum Anzeigen uebergeben <br>
	 * <br>
	 * korrospondiert mit:
	 * 
	 * @see Characterview#handler
	 */
	private void readRSS() {
		rssListAdapter.clear();
		rssListAdapter.notifyDataSetChanged();
		shutdownThread( threadItems );
		/** Thread der nebenlaeufig die Items laedt */
		threadItems = new Thread( new Runnable() {
			public void run() {
				String name = cursor.getString( cursor.getColumnIndex( Column.NAME.name() ) );
				String region = cursor.getString( cursor.getColumnIndex( Column.REGION.name() ) );
				String realm = cursor.getString( cursor.getColumnIndex( Column.REALM.name() ) );
				StringBuilder xml = Armory.characterRssFeed( name, region, realm );
				if (xml != null) {
					Document doc1 = xmlToDocument( xml.toString() );
					if (doc1 != null) {
						NodeList nl = doc1.getElementsByTagName( "entry" );
						if (nl != null) {
							runOnUiThread( new Runnable() {
								public void run() {
									setProgressBarIndeterminateVisibility( true );
								}
							});
							for (int k = 0; k < nl.getLength(); k++) {
								if (Thread.interrupted()) {
									break;
								}
								Node nEntry = nl.item( k );
								NodeList nl1 = nEntry.getChildNodes();
								String title = "";
								String datetimestamp = "";
								CharSequence content = "";
								for (int i = 0; i < nl1.getLength(); i++) {
									if (Thread.interrupted()) {
										break;
									}
									Node n = nl1.item( i );
									short type = n.getNodeType();
									if (type != Node.TEXT_NODE) {
										String name1 = n.getNodeName();
										NodeList nl2 = n.getChildNodes();
										if (nl2.getLength() > 0) {
											Object value1 = nl2.item( 0 ).getNodeValue();
											if (name1.equals( "title" )) {
												title = value1.toString();
											} else if (name1.equals( "updated" )) {
												// 2010-02-18T20:51:00+00:00
												SimpleDateFormat sdfIn = new SimpleDateFormat(
														"yyyy-MM-dd'T'hh:mm:ssZ" );
												try {
													String s = value1.toString();
													Date date = sdfIn.parse( s );
													SimpleDateFormat sdf = new SimpleDateFormat(
															"dd.MM.yyyy HH:mm" );
													datetimestamp = sdf.format( date );
												} catch (ParseException e) {
												}
											} else if (name1.equals( "content" )) {
												content = value1.toString();
											}
										}// if
									}// if
								}// for
								final Object[] o = new Object[] {
										title, datetimestamp, content
								};
								runOnUiThread( new Runnable() {
									public void run() {
										rssListAdapter.add( o );
									}
								});
							}// for
							runOnUiThread( new Runnable() {
								public void run() {
									setProgressBarIndeterminateVisibility( false );
								}
							});
						}
					}
				}
			}
		} );
		threadItems.start();
	}

	/**
	 * @param tag
	 * @return
	 */
	private ArrayList<String> getChilds( String tag ) {
		ArrayList<String> al = new ArrayList<String>();
		if (doc != null) {
			NodeList nl = doc.getElementsByTagName( tag );
			if (nl != null) {
				Node n = nl.item( 0 );
				if (n != null) {
					nl = n.getChildNodes();
					for (int i = 0; i < nl.getLength(); i++) {
						n = nl.item( i );
						short type = n.getNodeType();
						if (type != Node.TEXT_NODE) {
							String name = n.getNodeName();
							NamedNodeMap m = n.getAttributes();
							String value = "";
							for (int k = 0; k < m.getLength(); k++) {
								// effective / value / percent auswerten
								Node n1 = m.item( k );
								String s1 = n1.getNodeName();
								String value1 = n1.getNodeValue();
								if (s1.equals( "effective" )) {
									value = value1;
									break;
								} else if (s1.equals( "value" )) {
									value = value1;
									break;
								} else if (s1.equals( "percent" )) {
									value = value1 + "%";
									break;
								}
							}
							al.add( name + ": " + value );
						}
					}
				}
			}
		}
		return al;
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
