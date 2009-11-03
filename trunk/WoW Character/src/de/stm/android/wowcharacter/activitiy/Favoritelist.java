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
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.animation.AnimationUtils;
import android.widget.*;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.*;
import de.stm.android.wowcharacter.data.Character;
import de.stm.android.wowcharacter.data.Character.Data;
import de.stm.android.wowcharacter.renderer.FavoriteListAdapter;
import de.stm.android.wowcharacter.util.Armory;

/**
 * Einstiegspunkt für Splash gefolgt von der Favoritenliste. Wenn Favoritenliste leer, dann wird in
 * die Suche verzweigt
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>, <a
 *         href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 */
public class Favoritelist extends ListActivity implements ICharactersProvider {
	protected final static int CONTEXTMENU_REMOVE_FAVORITE = 0;
	/** Bestaetigungsdialog zum Loeschen aller Character */
	private Builder alertDeleteAll;
	private ViewFlipper flipper;
	/** NachrichtenID zum Beenden des Splash */
	private static final int STOPSPLASH = 0;
	/** time in milliseconds */
	private static final long SPLASHTIME = 5000;
	/** Sortierrichtungen */
	public static enum SortDirection {
		ASCEND, DESCEND
	};
	private Handler splashHandler = new Handler() {
		/*
		 * (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage( Message msg ) {
			switch (msg.what) {
			case STOPSPLASH:
				goToCharacterList();
				break;
			}
			super.handleMessage( msg );
		}
	};

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		init();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		populateMenu( menu );
		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		return (applyMenuChoice( item ) || super.onOptionsItemSelected( item ));
	}

	@Override
	protected void onStart() {
		super.onStart();
		sortAndFill( "NAME", SortDirection.ASCEND );
	}

	/**
	 * Fullscreen einstellen vor setContentView aufzurufen!
	 */
	public void setFullscreen() {
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN );
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
		setFullscreen();
		setContentView( R.layout.favoritelist );
		Message msg = new Message();
		msg.what = STOPSPLASH;
		splashHandler.sendMessageDelayed( msg, SPLASHTIME );
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
		alertDeleteAll = new AlertDialog.Builder( this ).setTitle( R.string.warn ).setMessage(
				R.string.charlist_deleteAll ).setPositiveButton( R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int whichButton ) {
						Uri allFavourites = Uri.parse( CONTENT_NAME_FAVOURITES );
						int i = getContentResolver().delete( allFavourites, null, null );
					}
				} ).setNegativeButton( R.string.no, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int whichButton ) {
			}
		} );
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
			Uri allFavourites = Uri.parse( CONTENT_NAME_FAVOURITES );
			String where = Column.REGION.name() + " = \"" + sRegion + "\" AND " + Column.REALM.name()
					+ " = \"" + sRealm + "\" AND " + Column.NAME.name() + " = \"" + sName + "\"";
			int i = getContentResolver().delete( allFavourites, where, null );
			if (i > 0) {
				String s = getString( R.string.charlist_favorite_deleted_toast );
				s = s.replace( "%1", sRealm + " @ " + sName );
				Toast.makeText( this, s, Toast.LENGTH_SHORT ).show();
				sortAndFill( "NAME", SortDirection.ASCEND );
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
	 * @param item
	 * @return
	 */
	private boolean applyMenuChoice( MenuItem item ) {
		switch (item.getItemId()) {
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
		}
		return false;
	}

	/**
	 * Nach Attribut sortierte Charaktere (Favoriten) in Liste fuellen
	 * 
	 * @param attribute
	 *            Attribut nach dem sortiert werden soll
	 */
	private void sortAndFill( final Object attribute, final SortDirection direction ) {
		try {
			Uri CONTENT_URI = Uri.parse( CONTENT_NAME_FAVOURITES );
			String[] projection = new String[] {
					Column.BITMAP.name(), Column.REALM.name(), Column.NAME.name()
			};
			Cursor c = managedQuery( CONTENT_URI, null, null, null, attribute
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
		Uri allTitles = Uri.parse( CONTENT_NAME_FAVOURITES );
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
		splashHandler.removeMessages( STOPSPLASH );
		// TODO fullscreen aufheben
		flipper = (ViewFlipper)findViewById( R.id.flipperView );
		flipper.setInAnimation( AnimationUtils.loadAnimation( this, R.anim.fade_in ) );
		flipper.setOutAnimation( AnimationUtils.loadAnimation( this, R.anim.fade_out ) );
		flipper.showNext();
		String sAppName = getString( R.string.app_name );
		String sTitle = getString( R.string.charlist_title );
		setTitle( sAppName + " (" + sTitle + ")" );
		Uri allFavourites = Uri.parse( CONTENT_NAME_FAVOURITES );
		Cursor c = managedQuery( allFavourites, null, null, null, null );
		startManagingCursor( c );
		if (c.getCount() == 0) {
			// keine Favouriten vorhanden, dann zur Suche gehen
			goToSearch();
		}
	}

	/**
	 * 
	 */
	private void goToSearch() {
		Intent intent = new Intent( this, de.stm.android.wowcharacter.activitiy.Searchlist.class );
		startActivity( intent );
	}

	/**
	 * Details als XML zum Character hinzufuegen
	 * 
	 * @param character
	 * @return
	 */
	private boolean setXMLtoCharacter( Cursor cursor ) {
		String xml = null;
		Armory armory = new Armory();
		String url = cursor.getString( cursor.getColumnIndex( Column.URL.name() ) );
		String region = cursor.getString( cursor.getColumnIndex( Column.REGION.name() ) );
		StringBuilder sb = armory.charactersheet( url, Armory.R.Region.valueOf( region ) );
		if (sb != null) {
			xml = sb.toString();
			try {
				ContentValues cv = new ContentValues();
				cv.put( Column.XML.name(), xml );
				Uri uri = Uri.parse( CONTENT_NAME_FAVOURITES + "/"
						+ cursor.getString( cursor.getColumnIndex( "_id" ) ) );
				getContentResolver().update( uri, cv, null, null );
				return true;
			} catch (Exception e) {
				// wenn Persistieren nicht erfolgeich, zur Laufzeit XML auch nicht behalten
				ContentValues cv = new ContentValues();
				cv.put( Column.XML.name(), "" );
				Uri uri = Uri.parse( CONTENT_NAME_FAVOURITES + "/"
						+ cursor.getString( cursor.getColumnIndex( "_id" ) ) );
				getContentResolver().update( uri, cv, null, null );
				e.printStackTrace();
			}
		}
		return false;
	}

	private void __characterChanged( ContentValues values, Data[] datas ) {
		try {
			// ContentValues values = new ContentValues();
			// values.put( Column.CLASS.name(), character.get( Character.Data.CLASS ).toString() );
			// values.put( Column.CLASSID.name(), character.get( Character.Data.CLASSID ).toString()
			// );
			// values.put( Column.FACTIONID.name(), character.get( Character.Data.FACTIONID )
			// .toString() );
			// values
			// .put( Column.GENDERID.name(), character.get( Character.Data.GENDERID )
			// .toString() );
			// values.put( Column.GUILD.name(), character.get( Character.Data.GUILD ).toString() );
			// values.put( Column.LEVEL.name(), character.get( Character.Data.LEVEL ).toString() );
			// values.put( Column.NAME.name(), character.get( Character.Data.NAME ).toString() );
			// values.put( Column.RACE.name(), character.get( Character.Data.RACE ).toString() );
			// values.put( Column.RACEID.name(), character.get( Character.Data.RACEID ).toString()
			// );
			// values.put( Column.REALM.name(), character.get( Character.Data.REALM ).toString() );
			// values.put( Column.REGION.name(), character.get( Character.Data.REGION ).toString()
			// );
			// values.put( Column.URL.name(), character.get( Character.Data.URL ).toString() );
			// values.put( Column.BITMAP.name(), Model.getInstance().getImage( character ) );
			// values.put( Column.XML.name(), character.get( Character.Data.XML ).toString() );
			Uri contentUri = Uri.parse( CONTENT_NAME_FAVOURITES );
			int r = getContentResolver().update( contentUri, values, null, null );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Details aufrufen
	 * 
	 * @param character
	 */
	private void goToDetails( Cursor cursor ) {
		// TODO Aktivitaetsanzeige
		boolean bOnline = setXMLtoCharacter( cursor );
		if (!bOnline) {
			// Online XML nicht verfuegbar
			String xml = cursor.getString( cursor.getColumnIndex( Column.XML.name() ) );
			if (xml == null || "".equals( xml )) {
				// Persitiertes Ergebnis auch nicht verfuegbar, dann keine Anzeige der Details
				Toast.makeText( Favoritelist.this, "Keine Details verfuegbar!", Toast.LENGTH_SHORT )
						.show();
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
		intent.putExtra( "ONLINE", bOnline );// gibt an, ob das Ergebnis online ermittelt wurde
		// (somit aktuell ist)
		startActivity( intent );
	}
}
