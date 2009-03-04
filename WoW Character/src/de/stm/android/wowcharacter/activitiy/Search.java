package de.stm.android.wowcharacter.activitiy;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.ListActivity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import de.stm.android.wowcharacter.R;
import de.stm.android.wowcharacter.util.Armory;
import de.stm.android.wowcharacter.util.SearchResult;
import de.stm.android.wowcharacter.util.Armory.R.Region;
import de.stm.android.wowcharacter.xml.InterpretSearch;

/**
 * Suchdialog
 * 
 * @author tfunke
 */
public class Search extends ListActivity {
	/** URL */
	private String searchtext;
	/** EditText (URL Eingabefeld) */
	private EditText et;
	/** Button */
	private Button bt;

	private ToggleButton tb_EU;

	private ToggleButton tb_US;
	
	/** geladene XML Seite */
	private StringBuilder sbXMLPage;
	/** Armory */
	private Armory armory = new Armory();
	/** Region */
	private Armory.R.Region region;
	private InterpretSearch is = new InterpretSearch();
	Handler handler = new Handler() {
		@Override
		public void handleMessage( Message msg ) {
			bt.setEnabled( true );
			if (sbXMLPage != null && sbXMLPage.length() > 0) {
				ArrayList<SearchResult> alsr = is.readXML( sbXMLPage.toString() );
				Collections.sort( alsr );
				SearchResult sr[] = new SearchResult[alsr.size()];
				sr = alsr.toArray( sr );
				setListAdapter( new ArrayAdapter<SearchResult>( Search.this,
						android.R.layout.simple_list_item_1, sr ) {
					@Override
					public View getView( int position, View convertView, ViewGroup parent ) {
						View view = super.getView( position, convertView, parent );
						// Drawable d = Model.getInstance().rowBackground;
						// view.setBackgroundDrawable( d );
						GradientDrawable d = new GradientDrawable(
								GradientDrawable.Orientation.BL_TR, new int[] {
										Color.GRAY, Color.LTGRAY
								} );
						d.setCornerRadius( 3f );
						view.setBackgroundDrawable( d );
						return view;
					}
				} );
			}
		}
	};

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		init();
	}

	/**
	 * Initialisierungen
	 */
	private void init() {
		/** View setzen */
		setContentView( R.layout.search );
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
				/**
				 * Return-Taste (keyCode 66) "deaktiviert" hab noch keine Konstante gefunden
				 */
				if (keyCode == 66) {
					bt.performClick();
					return true;
				}
				;
				return false;
			}
		} );
		
		bt = (Button)findViewById( R.id.buttonSearch );
		/** OnClickListener für Suchenbutton setzen */
		bt.setOnClickListener( new Button.OnClickListener() {
			public void onClick( View v ) {
				bt.setEnabled( false );
				searchtext = et.getText().toString();
				Thread background = new Thread( new Runnable() {
					public void run() {
						try {
							sbXMLPage = armory.search( searchtext, region );
							handler.sendMessage( handler.obtainMessage() );
						} catch (Throwable t) {
							// just end the background thread
						}
					}
				}, "WOW-Search" );
				background.start();
			}
		} );
		
		/**
		 * Bei Auswahl der Sparche in Android wird bei deutsch nur de geliefert (ohne Ländercode)
		 * daher fallback auf von getCountry() auf getLanguage() eingebaut
		 */

		tb_EU = (ToggleButton)findViewById( R.id.toggle_EU );
		tb_US = (ToggleButton)findViewById( R.id.toggle_US );

		tb_EU.setOnClickListener( new OnClickListener() {
			public void onClick( View arg0 ) {
				region = Region.EU;
				tb_US.setChecked( false );//Radio-Button Funktionalitaet
				if( !tb_US.isChecked() ) {
					tb_EU.setChecked(true);
				}
			}
		});

		tb_US.setOnClickListener( new OnClickListener() {
			public void onClick( View arg0 ) {
				region = Region.US;
				tb_EU.setChecked( false );//Radio-Button Funktionalitaet
				if( !tb_EU.isChecked() ) {
					tb_US.setChecked(true);
				}
			}
		});

		
//		rg.setOnCheckedChangeListener( new OnCheckedChangeListener() {
//			public void onCheckedChanged( RadioGroup group, int checkedId ) {
//				switch (checkedId) {
//				case R.id.toggle_EU:
//					region = Region.EU;
//					break;
//				case R.id.toggle_US:
//					region = Region.US;
//					break;
//				}
//			}
//		} );
		String locale = Locale.getDefault().getCountry();
		if (locale.length() == 0) {
			locale = Locale.getDefault().getLanguage();
		}
		if (locale.equalsIgnoreCase( "US" )) {
			tb_US.setChecked( true );
			tb_EU.setChecked( false );
		} else {
			tb_EU.setChecked( true );		
			tb_US.setChecked( false );
		}
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
		;
	}

	/**
	 * XML auswerten
	 * 
	 * @param xmlString
	 */
	public void interpretXML( String xmlString ) {
		StringReader inStream = new StringReader( xmlString );
		InputSource inSource = new InputSource( inStream );
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			// dbf.setValidating( false );
			// dbf.setNamespaceAware( false );
			doc = db.parse( inSource );
			// Demo TODO Auswerten (DTD evtl. nutzen), Bilder laden etc.
			NodeList nodeList = doc.getElementsByTagName( "character" );
			Node node = nodeList.item( 0 );
			NamedNodeMap nodesMap = node.getAttributes();
			// tv.setText(node.getLocalName());
			for (int i = 0; i < nodesMap.getLength(); i++) {
				Node n = nodesMap.item( i );
				// tv.append(n.getNodeName() + ": " + n.getNodeValue() + "; ");
			}
		} catch (SAXException e) {
			Log.e( "Search", "SAX" + e.getMessage() );
		} catch (IOException e) {
			Log.e( "Search", "URI" + e.getMessage() );
		} catch (ParserConfigurationException e) {
			Log.e( "Search", "Parser Implementation" + e.getMessage() );
		}
	}
}
