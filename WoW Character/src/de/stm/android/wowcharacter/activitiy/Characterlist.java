package de.stm.android.wowcharacter.activitiy;

import java.util.*;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.*;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Model;
import de.stm.android.wowcharacter.data.WOWCharacter;
import de.stm.android.wowcharacter.renderer.SearchListAdapter;

/**
 * 
 * 
 * @version $Revision:  $Date: $
 * @author <a href="mailto:tfunke@icubic.de">Thomas Funke</a>
 *
 */
public class Characterlist extends ListActivity {
	private Model model;
	/** true beim erstmaligen Aufruf */
	private boolean atFirst = true;
	protected final static int CONTEXTMENU_REMOVE_FAVORITE = 0;
	/** Bestaetigungsdialog zum Loeschen aller Character */
	private Builder alertDeleteAll;
	/** Sortierrichtungen */
	public static enum SortDirection {
		ASCEND, DESCEND
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
		Map<String, WOWCharacter> mapCharacters = model.getMapCharacters();
		// wenn Characterliste leer, dann gleich zur Suche springen (nur beim ersten Aufruf)
		if (atFirst && mapCharacters.size() == 0) {
			goToSearch();
			atFirst = false;
		} else {
			sortAndFill( "NAME", SortDirection.ASCEND );
		}
	}

	private void init() {
		setContentView( R.layout.characterlist );
		String sAppName = getString( R.string.app_name );
		String sTitle = getString( R.string.charlist_title );
		setTitle( sAppName + " (" + sTitle + ")" );
		model = Model.getInstance();
		getListView().setOnCreateContextMenuListener( new OnCreateContextMenuListener() {
			public void onCreateContextMenu( ContextMenu cm, View view, ContextMenuInfo cmi ) {
				WOWCharacter character = (WOWCharacter)getListAdapter().getItem(
						((AdapterView.AdapterContextMenuInfo)cmi).position );
				cm.setHeaderTitle( character.toString() );
				cm.add( 0, CONTEXTMENU_REMOVE_FAVORITE, 0,
						R.string.charlist_contextMenu_removeFromFavorites );
			}
		} );
		alertDeleteAll = new AlertDialog.Builder( this ).setTitle( R.string.warn ).setMessage(
				R.string.charlist_deleteAll ).setPositiveButton( R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int whichButton ) {
						model.deleteAllFavoriteCharacters();
						sortAndFill( "NAME", SortDirection.ASCEND );
					}
				} ).setNegativeButton( R.string.no, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int whichButton ) {
			}
		} );
	}

	@Override
	protected void onListItemClick( ListView l, View v, int position, long id ) {
		WOWCharacter character = (WOWCharacter)getListAdapter().getItem( position );
		model.getInfos( character );
	}

	@Override
	public boolean onContextItemSelected( MenuItem item ) {
		switch (item.getItemId()) {
		case CONTEXTMENU_REMOVE_FAVORITE:
			AdapterView.AdapterContextMenuInfo cmi = (AdapterView.AdapterContextMenuInfo)item
					.getMenuInfo();
			WOWCharacter character = (WOWCharacter)getListAdapter().getItem( cmi.position );
			boolean success = Model.getInstance().removeFavorite( character );
			if (success) {
				String s = getString( R.string.charlist_favorite_deleted_toast );
				s = s.replace( "%1", character.toString() );
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
			sortAndFill( "LEVEL", SortDirection.ASCEND );
			return (true);
		case R.id.sort_level_desc:
			sortAndFill( "LEVEL", SortDirection.DESCEND );
			return (true);
		case R.id.sort_name_asc:
			sortAndFill( "NAME", SortDirection.ASCEND );
			return (true);
		case R.id.sort_name_desc:
			sortAndFill( "NAME", SortDirection.DESCEND );
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
		Map<String, WOWCharacter> mapCharacters = model.getMapCharacters();
		Collection<WOWCharacter> c = mapCharacters.values();
		ArrayList<WOWCharacter> al = new ArrayList<WOWCharacter>( c );
		// Comparator der Attribut zum Sortieren nutzt TODO auf/absteigende Sortierung
		Comparator<WOWCharacter> comp = new Comparator<WOWCharacter>() {
			public int compare( WOWCharacter thisObject, WOWCharacter otherObject ) {
				Object o = thisObject.get( attribute );
				int result = 0;// nicht Vertauschen
				if (o instanceof String) {
					result = o.toString().compareTo( otherObject.get( attribute ).toString() );
				} else if (o instanceof Integer) {
					result = ((Integer)o).compareTo( ((Integer)otherObject.get( attribute )) );
				}
				if (direction == SortDirection.DESCEND) {
					result *= -1;
				}
				return result;
			}
		};
		Collections.sort( al, comp );
		// in Liste fuellen
		setListAdapter( new SearchListAdapter( this, al ) );
		// setListAdapter( new ArrayAdapter<WOWCharacter>( this,
		// android.R.layout.simple_list_item_1,
		// a ) {
		// @Override
		// public View getView( int position, View convertView, ViewGroup parent ) {
		// View view = super.getView( position, convertView, parent );
		// // Drawable d = Model.getInstance().rowBackground;
		// // view.setBackgroundDrawable( d );
		// GradientDrawable d = new GradientDrawable( GradientDrawable.Orientation.BL_TR,
		// new int[] {
		// Color.GRAY, Color.LTGRAY
		// } );
		// d.setCornerRadius( 3f );
		// view.setBackgroundDrawable( d );
		// return view;
		// }
		// } );
	}

	/**
	 * 
	 */
	private void goToSearch() {
		Intent intent = new Intent( this, de.stm.android.wowcharacter.activitiy.Search.class );
		startActivity( intent );
	}
}
