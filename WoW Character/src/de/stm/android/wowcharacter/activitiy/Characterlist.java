package de.stm.android.wowcharacter.activitiy;

import java.util.*;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.*;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Model;
import de.stm.android.wowcharacter.data.WOWCharacter;

public class Characterlist extends ListActivity {
	Model model;
	protected final static int CONTEXTMENU_REMOVE_FAVORITE = 0;
	private Builder alertDeleteAll;

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
		// wenn Characterliste leer, dann gleich zur Suche springen
		if (mapCharacters.size() == 0) {
			goToSearch();
		} else {
			sortAndFill( "NAME" );
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
		alertDeleteAll = new AlertDialog.Builder( this ).setTitle( R.string.warn ).setMessage( R.string.charlist_deleteAll ).setPositiveButton(
				R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int whichButton ) {
						model.deleteAllFavoriteCharacters();
						sortAndFill( "NAME" );
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
			Model.getInstance().removeFavorite( character );
			sortAndFill( "NAME" );
			return true;
		}
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
		case R.id.sort_level:
			sortAndFill( "LEVEL" );
			return (true);
		case R.id.sort_name:
			sortAndFill( "NAME" );
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
	private void sortAndFill( final Object attribute ) {
		Map<String, WOWCharacter> mapCharacters = model.getMapCharacters();
		Collection<WOWCharacter> c = mapCharacters.values();
		ArrayList<WOWCharacter> al = new ArrayList<WOWCharacter>( c );
		// Comparator der Attribut zum Sortieren nutzt TODO auf/absteigende Sortierung
		Comparator<WOWCharacter> comp = new Comparator<WOWCharacter>() {
			public int compare( WOWCharacter thisObject, WOWCharacter otherObject ) {
				Object o = thisObject.get( attribute );
				if (o instanceof String) {
					return o.toString().compareTo( otherObject.get( attribute ).toString() );
				} else if (o instanceof Integer) {
					return ((Integer)o).compareTo( ((Integer)otherObject.get( attribute )) );
				}
				return 0;
			}
		};
		Collections.sort( al, comp );
		WOWCharacter[] a = al.toArray( new WOWCharacter[al.size()] );
		// in Liste fuellen
		setListAdapter( new ArrayAdapter<WOWCharacter>( this, android.R.layout.simple_list_item_1,
				a ) {
			@Override
			public View getView( int position, View convertView, ViewGroup parent ) {
				View view = super.getView( position, convertView, parent );
				// Drawable d = Model.getInstance().rowBackground;
				// view.setBackgroundDrawable( d );
				GradientDrawable d = new GradientDrawable( GradientDrawable.Orientation.BL_TR,
						new int[] {
								Color.GRAY, Color.LTGRAY
						} );
				d.setCornerRadius( 3f );
				view.setBackgroundDrawable( d );
				return view;
			}
		} );
	}

	/**
	 * 
	 */
	private void goToSearch() {
		Intent intent = new Intent( this, de.stm.android.wowcharacter.activitiy.Search.class );
		startActivity( intent );
	}
}
