package de.stm.android.wowcharacter.activitiy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.data.Model;
import de.stm.android.wowcharacter.data.WOWCharacter;
import de.stm.android.wowcharacter.renderer.SearchListAdapter;
import de.stm.android.wowcharacter.util.Armory;
import de.stm.android.wowcharacter.util.Armory.R.Region;
import de.stm.android.wowcharacter.xml.InterpretSearch;

/**
 * Suchdialog
 * 
 * @version $Revision:  $Date: $
 * @author <a href="mailto:tfunke@icubic.de">Thomas Funke</a>
 * 
 */
public class Search extends ListActivity {
	private EditText et;
	private ImageButton bt;
	private ToggleButton tb_EU;
	private ToggleButton tb_US;
	/** geladene XML Seite */
	private StringBuilder sbXMLPage;
	private Model model;
	private Armory armory = new Armory();
	private Armory.R.Region region;
	private Thread searchThread;
	private InterpretSearch is = new InterpretSearch();
	ArrayList<WOWCharacter> listModel = new ArrayList<WOWCharacter>();
	Handler handler = new Handler() {
		@Override
		public void handleMessage( Message msg ) {
            setProgressBarIndeterminateVisibility(false);
			bt.setEnabled( true );
			if (sbXMLPage != null && sbXMLPage.length() > 0) {
				Region region = Region.EU;
				if (tb_US.isChecked()) {
					region = Region.US;
				}
				listModel.clear();
				is.readXML( sbXMLPage.toString(), region, listModel );
				SearchListAdapter sla = new SearchListAdapter( Search.this, listModel );
				Collections.sort( listModel );

				setListAdapter( sla );					

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
				Toast.makeText( Search.this, s, Toast.LENGTH_SHORT ).show();
				
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		init();
	}

	/**
	 * Initialisierungen
	 */
	private void init() {
		model = Model.getInstance();
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);//fuer Fortschrittskreis in Titelzeile
		/** View und Titel setzen */
		setContentView( R.layout.search );
        setProgressBarIndeterminateVisibility(false);
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
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					bt.performClick();
					return true;
				}
				return false;
			}
		} );
		/** OnClickListener für Suchenbutton setzen */
		bt = (ImageButton)findViewById( R.id.buttonSearch );
		bt.setOnClickListener( new Button.OnClickListener() {
			public void onClick( View v ) {
				bt.setEnabled( false );
                setProgressBarIndeterminateVisibility(true);
				searchThread = new Thread( new Runnable() {
					public void run() {
						try {
							sbXMLPage = armory.search( et.getText().toString(), region );
							handler.sendMessage( handler.obtainMessage() );
						} catch (Throwable t) {
							// just end the background thread
						}
					}
				}, "WOW-Search" );
				searchThread.start();
			}
		} );
		bt.setEnabled( false );
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
			}
		} );
		tb_US.setOnClickListener( new OnClickListener() {
			public void onClick( View arg0 ) {
				region = Region.US;
				tb_EU.setChecked( false );// Radio-Button Funktionalitaet
				if (!tb_EU.isChecked()) {
					tb_US.setChecked( true );
				}
			}
		} );
		/**
		 * - bei Initialisierung gem. der Ländereinstellung bereits einen Togglebutton markieren
		 * (bei der Auswahl der Sparche wird in Android bei deutsch nur de geliefert (ohne
		 * Ländercode) daher Fallback von getCountry() auf getLanguage() eingebaut
		 */
		String locale = Locale.getDefault().getCountry();
		if (locale.length() == 0) {
			locale = Locale.getDefault().getLanguage();
		}
		if (locale.equalsIgnoreCase( "US" )) {
			tb_US.setChecked( true );
			// tb_EU.setChecked(false);
		} else {
			tb_EU.setChecked( true );
			// tb_US.setChecked(false);
		}
		/** Kontextmenü registrieren */
		registerForContextMenu( getListView() );
	}

	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo ) {
		new MenuInflater( getApplication() ).inflate( R.menu.searchcontext, menu );
		WOWCharacter character = (WOWCharacter)getListAdapter().getItem(
				((AdapterView.AdapterContextMenuInfo)menuInfo).position );
		menu.setHeaderTitle( character.toString() );
		super.onCreateContextMenu( menu, v, menuInfo );
	}

	@Override
	public boolean onContextItemSelected( MenuItem item ) {
		switch (item.getItemId()) {
		case R.id.searchcontextmenu_add_favorite:
			AdapterView.AdapterContextMenuInfo cmi = (AdapterView.AdapterContextMenuInfo)item
					.getMenuInfo();
			WOWCharacter character = (WOWCharacter)getListAdapter().getItem( cmi.position );
			try {
				Model.getInstance().addFavorite( character );		
				//Don 't call it Schnitzel;o)
				String s = getString( R.string.search_addToFavorites_ok_toast );
				s = s.replace( "%1", character.toString() );
				Toast.makeText( this, s, Toast.LENGTH_SHORT ).show();
			} catch (Exception e) {
				String s = getString( R.string.search_addToFavorites_fail_toast );
				s = s.replace( "%1", character.toString() );
				Toast.makeText( this, s, Toast.LENGTH_SHORT ).show();
			}
			return true;
		}
		return false;
	}

	@Override
	protected void onListItemClick( ListView l, View v, int position, long id ) {
		WOWCharacter character = (WOWCharacter)getListAdapter().getItem( position );
		model.getInfos( character );
	}

	/**
	 * auf Aenderungen im EditText reagieren
	 */
	private void handleTextChanged() {
		// Suchbutton de-/aktivieren
		if (et.getText().toString().length() == 0) {
			bt.setEnabled( false );
		} else {
			bt.setEnabled( true );
		}
	}
}
