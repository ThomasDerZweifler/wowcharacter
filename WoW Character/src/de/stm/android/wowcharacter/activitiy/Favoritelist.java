package de.stm.android.wowcharacter.activitiy;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.animation.AnimationUtils;
import android.widget.*;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Character;
import de.stm.android.wowcharacter.data.ICharactersProvider;
import de.stm.android.wowcharacter.renderer.FavoriteListAdapter;
import de.stm.android.wowcharacter.util.Armory;

/**
 * Einstiegspunkt fuer Splash gefolgt von der Favoritenliste. Wenn Favoritenliste leer, dann wird in
 * die Suche verzweigt
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
public class Favoritelist extends ListActivity implements ICharactersProvider, IFavoritelist {
	protected final static int CONTEXTMENU_REMOVE_FAVORITE = 0;
	/** Bestaetigungsdialog zum Loeschen aller Character */
	private Builder alertDeleteAll;
	private ViewFlipper flipper;
	private boolean optionsMenuOpen = false;
	/** NachrichtenID zum Beenden des Splash */
	private static final int STOP_SPLASH = 0;
	/** time in milliseconds */
	private static final long SPLASHTIME = 5000;
	// private Animation anim = null;
	private Menu optionsMenu;
	private Thread refreshThread;
	/** Sortierrichtungen */
	public static enum SortDirection {
		ASCEND, DESCEND
	};
	private Handler handler = new Handler() {
		/*
		 * (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage( Message msg ) {
			switch (msg.what) {
			case STOP_SPLASH:
				goToCharacterList();
				break;
			}
			super.handleMessage( msg );
		}
	};

	@Override
	protected void onSaveInstanceState( Bundle outState ) {
		// bei erneutem onCreate ist dann ein Bundle vorhanden, was bedeutet,
		// dass die Applikation schon laeuft
		outState.putBoolean( ConfigData.APP_INITIALIZED.name(), true );
		int selectedItemPosition = getListView().getSelectedItemPosition();
		outState.putInt( ConfigData.SELECTED_ITEM_POSITION.name(), selectedItemPosition );
		outState.putBoolean( ConfigData.OPTIONS_MENU_OPEN.name(), optionsMenuOpen );
		super.onSaveInstanceState( outState );
	}

	@Override
	protected void onRestoreInstanceState( Bundle state ) {
		super.onRestoreInstanceState( state );
		goToCharacterList();
		int selectedItemPosition = state.getInt( ConfigData.SELECTED_ITEM_POSITION.name() );
		getListView().setSelection( selectedItemPosition );
		optionsMenuOpen = state.getBoolean( ConfigData.OPTIONS_MENU_OPEN.name() );
		if (optionsMenuOpen) {
			// Handler mHandler = new Handler();
			// mHandler.postDelayed( new Runnable() {
			// public void run() {
			// openOptionsMenu();
			// }}, 3000 );
			// showOptionsMenu();
			// ActivityManager am =
			// (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
			// Activity activity = getActivity(getListView());
			// if (activity != null) {
			// getApplicationContext().
			// }
		}
	}

	private void showOptionsMenu() {
		KeyEvent ku = new KeyEvent( KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MENU );
		getWindow().openPanel( Window.FEATURE_OPTIONS_PANEL, ku );
		// openOptionsMenu();
	}

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		init();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		populateMenu( menu );
		optionsMenuOpen = true;
		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onMenuOpened( int featureId, Menu menu ) {
		MenuItem mi = menu.findItem( R.id.sort );
		mi.setEnabled( getListView().getCount() > 1 );// Sortierung nur bei mehr
		// als einem Eintrag
		// aktivieren
		return super.onMenuOpened( featureId, menu );
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

	/**
	 * Fullscreen aufheben vor setContentView aufzurufen!
	 */
	public void setWindowDecorations() {
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN );
	}

	private void init() {
		// fuer Fortschrittskreis in Titelzeile
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.favoritelist );
		getIntent().setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
		// anim = AnimationUtils.loadAnimation( this, R.anim.magnify );
		Message msg = new Message();
		msg.what = STOP_SPLASH;
		handler.sendMessageDelayed( msg, SPLASHTIME );
		initSplash();
		getListView().setOnCreateContextMenuListener( new OnCreateContextMenuListener() {
			public void onCreateContextMenu( ContextMenu cm, View view, ContextMenuInfo cmi ) {
				Cursor cursor = (Cursor)getListAdapter().getItem(
						((AdapterView.AdapterContextMenuInfo)cmi).position );
				String realm = cursor.getString( cursor.getColumnIndex( Column.REALM.name() ) );
				String name = cursor.getString( cursor.getColumnIndex( Column.NAME.name() ) );
				cm.setHeaderTitle( realm + " @ " + name );
				cm.add( 0, CONTEXTMENU_REMOVE_FAVORITE, 0,
						R.string.charlist_contextMenu_removeFromFavorites );
			}
		} );
		// getListView().setOnItemSelectedListener( new OnItemSelectedListener()
		// {
		// public void onItemSelected( AdapterView<?> parent, View view, int
		// position, long id ) {
		// view.startAnimation( anim );
		// }
		// public void onNothingSelected( AdapterView<?> parent ) {
		// }
		// });
		alertDeleteAll = new AlertDialog.Builder( this ).setTitle( R.string.warn ).setMessage(
				R.string.charlist_deleteAll ).setPositiveButton( R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int whichButton ) {
						Uri allFavourites = Uri.parse( CONTENT_NAME_CHARACTERS );
						int i = getContentResolver().delete( allFavourites, null, null );
						goToSearch();
					}
				} ).setNegativeButton( R.string.no, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int whichButton ) {
			}
		} );
		sortAndFill( "NAME", SortDirection.ASCEND );
	}

	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			android.os.Process.killProcess( android.os.Process.myPid() );
		}
		return super.onKeyDown( keyCode, event );
	}

	private void initSplash() {
		((RelativeLayout)findViewById( R.id.splash )).setOnClickListener( new OnClickListener() {
			public void onClick( View v ) {
				goToCharacterList();
			}
		} );
		TextView mTextView = (TextView)findViewById( R.id.splash_label_ver );
		try {
			PackageInfo pi = getPackageManager().getPackageInfo( getPackageName(), 0 );
			mTextView.setText( "Version: " + pi.versionName );
		} catch (NameNotFoundException e) {
			// should never happen
		}
	}

	@Override
	protected void onListItemClick( ListView l, View v, int position, long id ) {
		Cursor c = (Cursor)getListAdapter().getItem( position );
		// v.startAnimation( anim );
		goToDetails( c );
	}

	@Override
	public boolean onContextItemSelected( MenuItem item ) {
		switch (item.getItemId()) {
		case CONTEXTMENU_REMOVE_FAVORITE:
			AdapterView.AdapterContextMenuInfo cmi = (AdapterView.AdapterContextMenuInfo)item
					.getMenuInfo();
			Cursor c = (Cursor)getListAdapter().getItem( cmi.position );
			String sRegion = c.getString( c.getColumnIndex( Column.REGION.name() ) );
			String sRealm = c.getString( c.getColumnIndex( Column.REALM.name() ) );
			String sName = c.getString( c.getColumnIndex( Column.NAME.name() ) );
			Uri allFavourites = Uri.parse( CONTENT_NAME_CHARACTERS );
			String where = Column.REGION.name() + " = \"" + sRegion + "\" AND "
					+ Column.REALM.name() + " = \"" + sRealm + "\" AND " + Column.NAME.name()
					+ " = \"" + sName + "\" AND IS_FAVOURITE =" + TRUE;
			int i = getContentResolver().delete( allFavourites, where, null );
			if (i > 0) {
				String s = getString( R.string.charlist_favorite_deleted_toast );
				s = s.replace( "%1", sRealm + " @ " + sName );
				Toast.makeText( this, s, Toast.LENGTH_SHORT ).show();
				sortAndFill( "NAME", SortDirection.ASCEND );
				if (getListAdapter().getCount() == 0) {
					goToSearch();
				}
				return true;
			}
		}
		// TODO evtl. noch das fehlerhafte Loeschen behandeln
		return false;
	}

	/**
	 * @param menu
	 */
	private void populateMenu( Menu menu ) {
		new MenuInflater( getApplication() ).inflate( R.menu.characterlist, menu );
	}

	/**
	 * Menuepunkt-Auswahl auswerten
	 * 
	 * @param item
	 * @return
	 */
	private boolean applyMenuChoice( MenuItem item ) {
		switch (item.getItemId()) {
		case R.id.refresh:
			refresh();
			return (true);
		case R.id.search:
			goToSearch();
			return (true);
		case R.id.sort_level_asc:
			sortAndFill( Column.LEVEL.name(), SortDirection.ASCEND );
			return (true);
		case R.id.sort_level_desc:
			sortAndFill( Column.LEVEL.name(), SortDirection.DESCEND );
			return (true);
		case R.id.sort_name_asc:
			sortAndFill( Column.NAME.name(), SortDirection.ASCEND );
			return (true);
		case R.id.sort_name_desc:
			sortAndFill( Column.NAME.name(), SortDirection.DESCEND );
			return (true);
		case R.id.clear_list:
			alertDeleteAll.show();
			return (true);
		case R.id.feedback:
			sendEmail();
			return (true);
		}
		return false;
	}

	private void sendEmail() {
		String sEmail = "thomasfunke71@googlemail.com";
		Intent intent = new Intent( Intent.ACTION_SENDTO, Uri.fromParts( "mailto", sEmail, null ) );
		String version = "";
		try {
			PackageInfo pi = getPackageManager().getPackageInfo( getPackageName(), 0 );
			version = pi.versionName;
		} catch (NameNotFoundException e) {
		}
		intent.putExtra( Intent.EXTRA_SUBJECT, "WOWCharacter (ver. " + version + ")" );
		startActivity( Intent.createChooser( intent, "Send email..." ) );
	}
	
	/**
	 * Nach Attribut sortierte Charaktere (Favoriten) in Liste fuellen
	 * 
	 * @param attribute
	 *            Attribut nach dem sortiert werden soll
	 */
	private void sortAndFill( final Object attribute, final SortDirection direction ) {
		try {
			Uri CONTENT_URI = Uri.parse( CONTENT_NAME_CHARACTERS );
			String[] projection = new String[] {
					Column.BITMAP.name(), Column.REALM.name(), Column.NAME.name()
			};
			String where = "IS_FAVOURITE = " + TRUE;
			Cursor c = managedQuery( CONTENT_URI, null, where, null, attribute
					+ ((direction == SortDirection.ASCEND) ? " asc" : " desc") );
			startManagingCursor( c );
			// __listCharacters();
			c.moveToFirst();
			if (c != null) {
				int[] to = new int[] {
						R.id.CharLevelRaceClass, R.id.CharImage, R.id.CharNameRealm,
						R.id.CharNameRealm
				};
				ListAdapter mAdapter = new FavoriteListAdapter( this, R.layout.characterlistitem,
						c, projection, to );
				this.setListAdapter( mAdapter );
			}
		} catch (Exception e) {
			Log.e( "[ERR]", e.getMessage() );
		}
	}

	/**
	 * nur zu Testzwecken
	 */
	private void __listCharacters() {
		// Uri allTitles = Uri.parse(
		// "content://net.wowcharacter.provider.characters/characters" );
		// Cursor c = managedQuery( allTitles, null, null, null, "name desc" );
		Uri allTitles = Uri.parse( CONTENT_NAME_CHARACTERS );
		Cursor c = managedQuery( allTitles, null, null, null, "name desc" );
		if (c.moveToFirst()) {
			do {
				byte[] blob = c.getBlob( c.getColumnIndex( Column.BITMAP.name() ) );
				Bitmap bmp = BitmapFactory.decodeByteArray( blob, 0, blob.length );
				Log.i( getClass().getName(), c.getString( c.getColumnIndex( Column.NAME.name() ) )
						+ ", " + c.getString( c.getColumnIndex( Column.REALM.name() ) )
						+ ", bitmap: " + bmp );
			} while (c.moveToNext());
		}
	}

	private void goToCharacterList() {
		handler.removeMessages( STOP_SPLASH );
		// TODO fullscreen aufheben
		flipper = (ViewFlipper)findViewById( R.id.flipperView );
		flipper.setInAnimation( AnimationUtils.loadAnimation( this, R.anim.fade_in ) );
		flipper.setOutAnimation( AnimationUtils.loadAnimation( this, R.anim.fade_out ) );
		flipper.showNext();
		String sAppName = getString( R.string.app_name );
		String sTitle = getString( R.string.charlist_title );
		setTitle( sAppName + " (" + sTitle + ")" );
		Cursor cursor = getAllFavourites();
		if (cursor == null || cursor.getCount() == 0) {
			// keine Favouriten vorhanden, dann zur Suche gehen
			goToSearch();
		}
	}

	@Override
	protected void onPause() {
		if (refreshThread != null) {
			refreshThread.interrupt();
			while (refreshThread.isAlive()) {
			}
			runOnUiThread( new Runnable() {
				public void run() {
					setProgressBarIndeterminateVisibility( false );
				};
			});
		}
		super.onPause();
	}

	/**
	 * @return
	 */
	private Cursor getAllFavourites() {
		Uri allFavourites = Uri.parse( CONTENT_NAME_CHARACTERS );
		Cursor cursor = null;
		try {
			String where = "IS_FAVOURITE = " + TRUE;
			cursor = managedQuery( allFavourites, null, where, null, null );
			startManagingCursor( cursor );
		} catch (Exception e) {
		}
		return cursor;
	}

	/**
	 * Favoriten-Details erneuern
	 */
	private void refresh() {
		refreshThread = new Thread( new Runnable() {
			public void run() {
				try {
					final Cursor cursor = getAllFavourites();
					int count;
					if (cursor != null && (count = cursor.getCount()) > 0) {
						for (int i = 0; i < count; i++) {
							if (Thread.interrupted()) {
								break;
							}
							cursor.moveToPosition( i );
							final ContentValues cv = new ContentValues();
							StringBuilder sb = getXML( cursor );
							cv.put( Column.XML.name(), sb.toString() );
							final String id = cursor.getString( cursor.getColumnIndex( BaseColumns._ID ) );
							runOnUiThread( new Runnable() {
								public void run() {
									boolean success = update( id, cv );
									if (success) {
										String name = cursor.getString( cursor
												.getColumnIndex( Column.REALM.name() ) );
										String realm = cursor.getString( cursor
												.getColumnIndex( Column.NAME.name() ) );
										String s = getString( R.string.charlist_favorite_updated_toast );
										s = s.replace( "%1", realm + " @ " + name );
										Toast.makeText( getListView().getContext(), s,
												Toast.LENGTH_SHORT ).show();
									}
								}
							} );
						}
					}
					runOnUiThread( new Runnable() {
						public void run() {
							setProgressBarIndeterminateVisibility( false );
						};
					});
				} catch (Throwable t) {
					// just end the background thread
				}
			}
		}, "WOW-Refresh" );
		setProgressBarIndeterminateVisibility( true );
		refreshThread.start();
	}

	/**
	 * 
	 */
	private void goToSearch() {
		Intent intent = new Intent( this, de.stm.android.wowcharacter.activitiy.Searchlist.class );
		startActivity( intent );
	}

	/**
	 * @param url
	 * @param region
	 * @return
	 */
	private StringBuilder getXML( String url, String region ) {
		return Armory.charactersheet( url, Armory.R.Region.valueOf( region ) );
	}

	/**
	 * @param cursor
	 * @return
	 */
	private StringBuilder getXML( Cursor cursor ) {
		String url = cursor.getString( cursor.getColumnIndex( Column.URL.name() ) );
		String region = cursor.getString( cursor.getColumnIndex( Column.REGION.name() ) );
		return getXML( url, region );
	}

	/**
	 * Details als XML zum Character hinzufuegen
	 * 
	 * @param character
	 * @return
	 */
	private boolean setXMLtoCharacter( Cursor cursor ) {
		StringBuilder sb = getXML( cursor );
		if (sb != null) {
			String xml = sb.toString();
			try {
				ContentValues cv = new ContentValues();
				cv.put( Column.XML.name(), xml );
				update( cursor, cv );
				return true;
			} catch (Exception e) {
				// wenn Persistieren nicht erfolgeich, zur Laufzeit XML auch
				// nicht behalten
				ContentValues cv = new ContentValues();
				cv.put( Column.XML.name(), "" );
				update( cursor, cv );
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean update( String id, ContentValues cv ) {
		Uri uri = Uri.parse( CONTENT_NAME_CHARACTERS + "/" + id );
		int count = getContentResolver().update( uri, cv, null, null );
		return count > 0;
	}

	private boolean update( Cursor cursor, ContentValues cv ) {
		Uri uri = Uri.parse( CONTENT_NAME_CHARACTERS + "/"
				+ cursor.getString( cursor.getColumnIndex( "_id" ) ) );
		int count = getContentResolver().update( uri, cv, null, null );
		return count > 0;
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
				// Persitiertes Ergebnis auch nicht verfuegbar, dann keine
				// Anzeige der Details
				Toast.makeText( this, "Keine Details verfuegbar!", Toast.LENGTH_SHORT ).show();
				return;
			}
		}
		Intent intent = new Intent( this, de.stm.android.wowcharacter.activitiy.Characterview.class );
		// Cursor kann leider nicht uebergeben werden, deshalb Schluessel an
		// dieser Stelle
		String region = cursor.getString( cursor.getColumnIndex( Column.REGION.name() ) );
		String realm = cursor.getString( cursor.getColumnIndex( Column.REALM.name() ) );
		String name = cursor.getString( cursor.getColumnIndex( Column.NAME.name() ) );
		intent.putExtra( Character.Data.REGION.name(), region );
		intent.putExtra( Character.Data.REALM.name(), realm );
		intent.putExtra( Character.Data.NAME.name(), name );
		intent.putExtra( "IS_TEMPORARY", false );
		intent.putExtra( "ONLINE", bOnline );// gibt an, ob das Ergebnis online
		// ermittelt wurde
		// (somit aktuell ist)
		startActivity( intent );
	}
}
