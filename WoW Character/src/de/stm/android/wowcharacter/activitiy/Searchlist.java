package de.stm.android.wowcharacter.activitiy;

import java.util.*;

import android.app.ListActivity;
import android.content.*;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Character;
import de.stm.android.wowcharacter.data.ICharactersProvider;
import de.stm.android.wowcharacter.data.Character.Data;
import de.stm.android.wowcharacter.renderer.SearchListAdapter;
import de.stm.android.wowcharacter.util.Armory;
import de.stm.android.wowcharacter.util.Armory.R.Region;
import de.stm.android.wowcharacter.xml.InterpretSearch;

/**
 * Suchdialog
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
public class Searchlist extends ListActivity implements ICharactersProvider, ISearchList {
	private EditText et;
	private ToggleButton tb_EU;
	private ToggleButton tb_US;
	private boolean ready;
	private WindowManager windowManager;
	/** geladene XML Seite */
	private StringBuilder sbXMLPage;
	private int selectedItemPosition = -1;
	/** Sortierung nach Realm bzw. Relevance */
	private int sortBy = R.id.sort_realm_asc;
	private TextView sectionTooltip;
	private Armory.R.Region region;
	private boolean optionsMenuOpen = false;
	/**
	 * Map fuer Such-Werte (um Aenderungen an den Eingabewerten zu erkennen), Schluessel: NAME,
	 * REGION
	 */
	private Map<String, Object> mapValuesForResult;
	private Thread searchThread;
	private InterpretSearch is = new InterpretSearch();
	private ArrayList<Character> listModel = new ArrayList<Character>();
	private final class RemoveWindow implements Runnable {
		public void run() {
			removeWindow();
		}
	}
	private RemoveWindow mRemoveWindow = new RemoveWindow();
	Handler handler = new Handler() {
		@Override
		public void handleMessage( Message msg ) {
		}
	};

	private void removeWindow() {
		sectionTooltip.setVisibility( View.INVISIBLE );
	}

	@Override
	protected void onSaveInstanceState( Bundle outState ) {
		String name = et.getText().toString();
		outState.putString( ConfigData.NAME.name(), name );
		String realm = tb_US.isSelected() ? "US" : "EU";
		outState.putString( ConfigData.REALM.name(), realm );
		int selectedItemPosition = getListView().getSelectedItemPosition();
		outState.putInt( ConfigData.SELECTED_ITEM_POSITION.name(), selectedItemPosition );
		super.onSaveInstanceState( outState );
	}

	protected void onRestoreInstanceState( Bundle state ) {
		super.onRestoreInstanceState( state );
		String name = state.getString( ConfigData.NAME.name() );
		String realm = state.getString( ConfigData.REALM.name() );
		selectedItemPosition = state.getInt( ConfigData.SELECTED_ITEM_POSITION.name() );
		search();
	}

	@Override
	protected void onPause() {
		super.onPause();
		removeWindow();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		windowManager.removeView( sectionTooltip );
		ready = false;
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		init();
	}

	/**
	 * Initialisierungen
	 */
	private void init() {
		// fuer Fortschrittskreis in Titelzeile
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		/** View und Titel setzen */
		setContentView( R.layout.searchlist );
		mapValuesForResult = new HashMap<String, Object>();
		windowManager = (WindowManager)getSystemService( Context.WINDOW_SERVICE );
		setProgressBarIndeterminateVisibility( false );
		String sAppName = getString( R.string.app_name );
		String sTitle = getString( R.string.search_title );
		setTitle( sAppName + " (" + sTitle + ")" );
		/** Textfeld finden */
		et = (EditText)findViewById( R.id.editTextName );
		et.addTextChangedListener( new TextWatcher() {
			public void afterTextChanged( Editable arg0 ) {
				textChanged();
			}

			public void beforeTextChanged( CharSequence arg0, int arg1, int arg2, int arg3 ) {
				textChanged();
			}

			public void onTextChanged( CharSequence arg0, int arg1, int arg2, int arg3 ) {
				textChanged();
			}

			private void textChanged() {
				handleTextChanged();
			}
		} );
		et.setOnKeyListener( new EditText.OnKeyListener() {
			public boolean onKey( View v, int keyCode, KeyEvent event ) {
				checkEmpty();
				if( event.getAction() == KeyEvent.ACTION_DOWN ) {
					if (keyCode == KeyEvent.KEYCODE_ENTER) {
						search();
						return true;
					}
				}
				return false;
			}
		} );
		/** Togglebuttonfunktionalität */
		tb_EU = (ToggleButton)findViewById( R.id.toggle_EU );
		tb_US = (ToggleButton)findViewById( R.id.toggle_US );
		tb_EU.setOnClickListener( new OnClickListener() {
			public void onClick( View arg0 ) {
				region = Region.EU;
				tb_US.setChecked( false );// Radio-Button Funktionalitaet
				if (!tb_US.isChecked()) {
					tb_EU.setChecked( true );
				}
				checkValues();
			}
		} );
		tb_US.setOnClickListener( new OnClickListener() {
			public void onClick( View arg0 ) {
				region = Region.US;
				tb_EU.setChecked( false );// Radio-Button Funktionalitaet
				if (!tb_EU.isChecked()) {
					tb_US.setChecked( true );
				}
				checkValues();
			}
		} );
		/**
		 * - bei Initialisierung gem. der Ländereinstellung bereits einen Togglebutton markieren
		 * (bei der Auswahl der Sparche wird in Android bei deutsch nur 'DE' geliefert (ohne
		 * Ländercode) daher Fallback von getCountry() auf getLanguage() eingebaut
		 */
		String locale = Locale.getDefault().getCountry();
		if (locale.length() == 0) {
			locale = Locale.getDefault().getLanguage();
		}
		if (locale.equalsIgnoreCase( "US" )) {
			region = Region.US;
			tb_US.setChecked( true );
			// tb_EU.setChecked(false);
		} else {
			region = Region.EU;
			tb_EU.setChecked( true );
			// tb_US.setChecked(false);
		}
		checkEmpty();
		/** Kontextmenü registrieren */
		registerForContextMenu( getListView() );
		getListView().setFastScrollEnabled( true );
		// getListView().addFooterView( buildFooter() );//TODO funktioniert nicht
		sectionTooltip = (TextView)getLayoutInflater().inflate( R.layout.list_position, null );
		sectionTooltip.setVisibility( View.INVISIBLE );
		getListView().setOnScrollListener( new OnScrollListener() {
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount ) {
				if (ready) {// TODO Abfrage, ob Scroll-Marker eingeblendet ist, oder gleich an
					// diesen einen Listener registrieren...
					int indexOfMiddleItem = firstVisibleItem + visibleItemCount / 2;
					if (indexOfMiddleItem < listModel.size()) {
						Character character = listModel.get( firstVisibleItem + visibleItemCount
								/ 2 );
						String text = getString( R.string.tooltipSearchlist_Realm );
						if (sortBy == R.id.sort_realm_asc) {
							String realm = character.get( Data.REALM ).toString();
							text = text.replaceAll( "%1", String.valueOf( realm.charAt( 0 ) ) );
						} else if (sortBy == R.id.sort_relevance_desc) {
							text = getString( R.string.tooltipSearchlist_Relevance );
							int relevance = Integer.parseInt( character.get( Data.RELEVANCE )
									.toString() );
							text = text.replaceAll( "%1", "" + relevance );
						}
						sectionTooltip.setText( text );
						sectionTooltip.setVisibility( View.VISIBLE );
						
						handler.removeCallbacks( mRemoveWindow );
						handler.postDelayed( mRemoveWindow, 1500 );// 1.5s ist die Zeit, die auch
						// der Scroll-Marker zum
						// Ausblenden benoetigt
					}
				}
			}

			public void onScrollStateChanged( AbsListView view, int scrollState ) {
			}
		} );
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams( LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT );
		windowManager.addView( sectionTooltip, lp );
	}

	private void search() {
		// Werte speichern
		String name = et.getText().toString();
		mapValuesForResult.put( ConfigData.NAME.name(), name );
		mapValuesForResult.put( ConfigData.REALM.name(), region );
		if (searchThread != null && searchThread.isAlive()) {
			searchThread.interrupt();
		}
		while (searchThread != null && searchThread.isAlive()) {
			// solange warten, bis ein laufender Suchthread beendet wird
		}
		searchThread = new Thread( new Runnable() {
			public void run() {
				try {
					sbXMLPage = Armory.search( et.getText().toString(), region );
					runOnUiThread( new Runnable() {
						public void run() {
							listModel.clear();
						}
					});
					if (sbXMLPage != null && sbXMLPage.length() > 0) {
						Region region = Region.EU;
						if (tb_US.isChecked()) {
							region = Region.US;
						}
						is.readXML( sbXMLPage.toString(), region, listModel );

						runOnUiThread( new Runnable() {
							public void run() {
								String s;
								int listsize = listModel.size();
								if (listsize == 0) {
									s = getString( R.string.search_char_found_none_toast );
								} else if (listsize == 1) {
									s = getString( R.string.search_char_found_one_toast );
								} else {
									s = getString( R.string.search_char_found_more_toast );
									s = s.replace( "%1", Integer.toString( listsize ) );
								}
								Toast.makeText( Searchlist.this, s, Toast.LENGTH_SHORT ).show();
							}
						});
						ready = true;
						// new Handler().postDelayed(new Runnable() {
						// public void run() {
						// getListView().setSelection( selectedItemPosition );
						// }
						// }, 6000);
					} else {
						runOnUiThread( new Runnable() {
							public void run() {
								String s = getString( R.string.search_char_found_none_toast );
								Toast.makeText( Searchlist.this, s, Toast.LENGTH_SHORT ).show();
							}
						});
					}
					runOnUiThread( new Runnable() {
						public void run() {
							sortAndFill( sortBy );
							setProgressBarIndeterminateVisibility( false );
							TextView tf = (TextView)findViewById( R.id.valuesChanged );
							tf.setText( "" );
						}
					} );
				} catch (Throwable t) {
					// just end the background thread
				}
			}
		}, "WOW-Search" );
		setProgressBarIndeterminateVisibility( true );
		searchThread.start();
	}

	/**
	 * @param menu
	 */
	private void populateMenu( Menu menu ) {
		new MenuInflater( getApplication() ).inflate( R.menu.searchlist, menu );
	}

	@Override
	public void onOptionsMenuClosed( Menu menu ) {
		super.onOptionsMenuClosed( menu );
		optionsMenuOpen = false;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		return (applyMenuChoice( item ) || super.onOptionsItemSelected( item ));
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		populateMenu( menu );
		optionsMenuOpen = true;
		return super.onCreateOptionsMenu( menu );
	}

	/**
	 * Menuepunkt-Auswahl auswerten
	 * 
	 * @param item
	 * @return
	 */
	private boolean applyMenuChoice( MenuItem item ) {
		switch (item.getItemId()) {
		case R.id.sort_realm_asc:
			sortBy = R.id.sort_realm_asc;
			sortAndFill( R.id.sort_realm_asc );
			return true;
		case R.id.sort_relevance_desc:
			sortBy = R.id.sort_relevance_desc;
			sortAndFill( R.id.sort_relevance_desc );
			return true;
		}
		return false;
	}

	/**
	 * Test auf leeren Namen bei der Suche
	 * 
	 * @param input
	 * @return false = leeres Eingabefeld
	 */
	private boolean checkEmpty() {
		boolean result = et.getText().toString().equals( "" );
		if (result) {
			tb_EU.setEnabled( false );
			tb_US.setEnabled( false );
		} else {
			tb_EU.setEnabled( true );
			tb_US.setEnabled( true );
		}
		return !result;
	}

	/**
	 * @return
	 */
	private View buildFooter() {
		TextView txt = new TextView( this );
		txt.setText( "xxxxxxxxxxxxxxxxxxxx" );
		return (txt);
	}

	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo ) {
		new MenuInflater( getApplication() ).inflate( R.menu.searchcontext, menu );
		Character character = (Character)getListAdapter().getItem(
				((AdapterView.AdapterContextMenuInfo)menuInfo).position );
		menu.setHeaderTitle( character.toString() );
		super.onCreateContextMenu( menu, v, menuInfo );
	}

	/**
	 * Character als Favourite speichern
	 * 
	 * @TODO Handy-Speicher ueberpruefen, ggf. SD Card anbieten
	 * @param name
	 * @param server
	 * @param bitmap
	 */
	private void addFavourite( Character character ) {
		ContentValues values = character.getContentValues();
		byte[] bitmap = Armory.getCharIcon( character );
		if (bitmap != null) {
			values.put( Column.BITMAP.name(), bitmap );
		}
		values.put( Column.IS_FAVOURITE.name(), TRUE );
		Uri contentUri = Uri.parse( CONTENT_NAME_CHARACTERS );
		Uri uri = getContentResolver().insert( contentUri, values );
	}

	/**
	 * Character temporaer speichern (IS_FAVOURITE = FALSE)
	 * 
	 * @param name
	 * @param server
	 * @param bitmap
	 */
	private void addCharacterTemporary( Character character ) {
		ContentValues values = character.getContentValues();
		Object bitmap = Armory.getCharIcon( character );
		if (bitmap != null) {
			values.put( Column.BITMAP.name(), (byte[])bitmap );
		}
		values.put( Column.IS_FAVOURITE.name(), FALSE );
		String where = "IS_FAVOURITE = " + FALSE;
		Uri contentUri = Uri.parse( CONTENT_NAME_CHARACTERS );
		// Versuch, einen vorhandenen temporaeren Charakter zu ueberschreiben (Wiederverwendung der
		// _id)
		int count = getContentResolver().update( contentUri, values, where, null );
		if (count == 0) {
			// noch kein temporaerer Charakter vorhanden
			Uri uri = getContentResolver().insert( contentUri, values );
		}
	}

	/**
	 * Alle Character die keine Favoriten sind loeschen
	 */
	private void removeAllNonFavourites() {
		Uri contentUri = Uri.parse( CONTENT_NAME_CHARACTERS );
		String where = "IS_FAVOURITE = " + FALSE;
		int count = getContentResolver().delete( contentUri, where, null );
	}

	@Override
	public boolean onContextItemSelected( MenuItem item ) {
		switch (item.getItemId()) {
		case R.id.searchcontextmenu_add_favorite:
			AdapterView.AdapterContextMenuInfo cmi = (AdapterView.AdapterContextMenuInfo)item
					.getMenuInfo();
			Character character = (Character)getListAdapter().getItem( cmi.position );
			try {
				addFavourite( character );
				// Don 't call it Schnitzel;o)
				String s = getString( R.string.search_addToFavorites_ok_toast );
				s = s.replace( "%1", character.toString() );
				Toast.makeText( this, s, Toast.LENGTH_SHORT ).show();
			} catch (Exception e) {
				String s = getString( R.string.search_addToFavorites_fail_toast );
				s = s.replace( "%1", character.toString() );
				Toast.makeText( this, s, Toast.LENGTH_SHORT ).show();
			}
		}
		return true;
	}

	/**
	 * @param sortBy
	 */
	private void sortAndFill( int sortBy ) {
		if (sortBy == R.id.sort_realm_asc) {
			Collections.sort( listModel );
			SearchListAdapter sla = new SearchListAdapter( Searchlist.this, listModel );
			setListAdapter( sla );
		} else if (sortBy == R.id.sort_relevance_desc) {
			Collections.sort( listModel, new Comparator<Character>() {
				public int compare( Character object1, Character object2 ) {
					int i1 = Integer.parseInt( object1.get( Data.RELEVANCE ).toString() );
					int i2 = Integer.parseInt( object2.get( Data.RELEVANCE ).toString() );
					if (i1 < i2) {
						return 1;
					} else if (i1 > i2) {
						return -1;
					}
					return 0;
				}
			} );
			SearchListAdapter sla = new SearchListAdapter( Searchlist.this, listModel );
			setListAdapter( sla );
		}
	}

	@Override
	protected void onListItemClick( ListView l, View v, int position, long id ) {
		Character character = (Character)getListAdapter().getItem( position );
		addCharacterTemporary( character );
		Uri allFavourites = Uri.parse( CONTENT_NAME_CHARACTERS );
		Cursor cursor = null;
		try {
			String where = "IS_FAVOURITE = 0";
			cursor = managedQuery( allFavourites, null, where, null, null );
			startManagingCursor( cursor );
		} catch (Exception e) {
		}
		if (cursor != null && cursor.getCount() > 0) {
			boolean b = cursor.moveToPosition( 0 );
			goToDetails( cursor );
		}
	}

	/**
	 * Test auf Aenderungen der Suchanfrage
	 */
	private void checkValues() {
		Object name = mapValuesForResult.get( ConfigData.NAME.name() );
		Object realm = mapValuesForResult.get( ConfigData.REALM.name() );
		boolean valuesChanged = true;
		Object nameNew = et.getText().toString();
		valuesChanged = !nameNew.equals( name ) || !region.equals( realm );
		TextView tf = (TextView)findViewById( R.id.valuesChanged );
		tf.setText( valuesChanged ? "*" : "" );
	}

	/**
	 * auf Aenderungen im EditText reagieren
	 */
	private void handleTextChanged() {
		checkValues();
	}

	/**
	 * Details als XML zum Character hinzufuegen
	 * 
	 * @param character
	 * @return
	 */
	private boolean setXMLtoCharacter( Cursor cursor ) {
		String xml = null;
		String url = cursor.getString( cursor.getColumnIndex( Column.URL.name() ) );
		String region = cursor.getString( cursor.getColumnIndex( Column.REGION.name() ) );
		StringBuilder sb = Armory.charactersheet( url, Armory.R.Region.valueOf( region ) );
		if (sb != null) {
			xml = sb.toString();
			try {
				ContentValues cv = new ContentValues();
				cv.put( Column.XML.name(), xml );
				Uri uri = Uri.parse( CONTENT_NAME_CHARACTERS + "/"
						+ cursor.getString( cursor.getColumnIndex( "_id" ) ) );
				getContentResolver().update( uri, cv, null, null );
				return true;
			} catch (Exception e) {
				// wenn Persistieren nicht erfolgeich, zur Laufzeit XML auch nicht behalten
				ContentValues cv = new ContentValues();
				cv.put( Column.XML.name(), "" );
				Uri uri = Uri.parse( CONTENT_NAME_CHARACTERS + "/"
						+ cursor.getString( cursor.getColumnIndex( "_id" ) ) );
				getContentResolver().update( uri, cv, null, null );
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Details aufrufen
	 * 
	 * @param character
	 */
	private void goToDetails( Cursor cursor ) {
		boolean bOnline = setXMLtoCharacter( cursor );
		if (!bOnline) {
			// Online XML nicht verfuegbar
			String xml = cursor.getString( cursor.getColumnIndex( Column.XML.name() ) );
			if (xml == null || "".equals( xml )) {
				// Persitiertes Ergebnis auch nicht verfuegbar, dann keine Anzeige der Details
				Toast.makeText( this, "Keine Details verfuegbar!", Toast.LENGTH_SHORT ).show();
				return;
			}
		}
		Intent intent = new Intent( this, de.stm.android.wowcharacter.activitiy.Characterview.class );
		// Cursor kann leider nicht uebergeben werden, deshalb Schluessel an dieser Stelle
		String region = cursor.getString( cursor.getColumnIndex( Column.REGION.name() ) );
		String realm = cursor.getString( cursor.getColumnIndex( Column.REALM.name() ) );
		String name = cursor.getString( cursor.getColumnIndex( Column.NAME.name() ) );
		intent.putExtra( Character.Data.REGION.name(), region );
		intent.putExtra( Character.Data.REALM.name(), realm );
		intent.putExtra( Character.Data.NAME.name(), name );
		intent.putExtra( "IS_TEMPORARY", true );
		intent.putExtra( "ONLINE", bOnline );// gibt an, ob das Ergebnis online ermittelt wurde
		// (somit aktuell ist)
		startActivity( intent );
	}
}
