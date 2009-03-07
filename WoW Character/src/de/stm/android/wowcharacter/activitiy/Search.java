package de.stm.android.wowcharacter.activitiy;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
 * @author tfunke
 */
public class Search extends ListActivity {
	private EditText et;
	private Button bt;
	private ToggleButton tb_EU;
	private ToggleButton tb_US;
	/** geladene XML Seite */
	private StringBuilder sbXMLPage;
	private Model model;

	private Armory armory = new Armory();
	private Armory.R.Region region;

	private InterpretSearch is = new InterpretSearch();
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			bt.setEnabled(true);
			if (sbXMLPage != null && sbXMLPage.length() > 0) {
				Region region = Region.EU;
				if (tb_US.isChecked()) {
					region = Region.US;
				}

				ArrayList<WOWCharacter> alsr = is.readXML(sbXMLPage.toString(),
						region);
				Collections.sort(alsr);
				WOWCharacter sr[] = new WOWCharacter[alsr.size()];
				sr = alsr.toArray(sr);
				setListAdapter(new SearchListAdapter(Search.this, sr));
			}
		}
	};

	@SuppressWarnings("unchecked")

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	/**
	 * Initialisierungen
	 */
	private void init() {
		model = Model.getInstance();
		/** View und Titel setzen */
		setContentView(R.layout.search);
		String sAppName = getString(R.string.app_name);
		String sTitle = getString(R.string.search_title);
		setTitle(sAppName + " (" + sTitle + ")");

		/** Textfeld finden */
		et = (EditText) findViewById(R.id.editTextName);
		et.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable arg0) {
				textChanged();
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				textChanged();
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				textChanged();
			}

			private void textChanged() {
				handleTextChanged();
			}
		});
		et.setOnKeyListener(new EditText.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					bt.performClick();
					return true;
				}
				return false;
			}
		});

		/** OnClickListener für Suchenbutton setzen */
		bt = (Button) findViewById(R.id.buttonSearch);
		bt.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				bt.setEnabled(false);
				Thread background = new Thread(new Runnable() {
					public void run() {
						try {
							sbXMLPage = armory.search(et.getText().toString(),
									region);
							handler.sendMessage(handler.obtainMessage());
						} catch (Throwable t) {
							// just end the background thread
						}
					}
				}, "WOW-Search");
				background.start();
			}
		});

		/** Togglebuttonfunktionalität */
		tb_EU = (ToggleButton) findViewById(R.id.toggle_EU);
		tb_US = (ToggleButton) findViewById(R.id.toggle_US);
		tb_EU.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				region = Region.EU;
				tb_US.setChecked(false);// Radio-Button Funktionalitaet
				if (!tb_US.isChecked()) {
					tb_EU.setChecked(true);
				}
			}
		});
		tb_US.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				region = Region.US;
				tb_EU.setChecked(false);// Radio-Button Funktionalitaet
				if (!tb_EU.isChecked()) {
					tb_US.setChecked(true);
				}
			}
		});

		/**
		 * - bei Initialisierung gem. der Ländereinstellung bereits einen
		 * Togglebutton markieren (bei der Auswahl der Sparche wird in Android
		 * bei deutsch nur de geliefert (ohne Ländercode) daher Fallback von
		 * getCountry() auf getLanguage() eingebaut
		 */
		String locale = Locale.getDefault().getCountry();
		if (locale.length() == 0) {
			locale = Locale.getDefault().getLanguage();
		}
		if (locale.equalsIgnoreCase("US")) {
			tb_US.setChecked(true);
			// tb_EU.setChecked(false);
		} else {
			tb_EU.setChecked(true);
			// tb_US.setChecked(false);
		}

		/** Kontextmenü registrieren */
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		new MenuInflater(getApplication()).inflate(R.menu.searchcontext, menu);

		WOWCharacter character = (WOWCharacter) getListAdapter().getItem(
				((AdapterView.AdapterContextMenuInfo) menuInfo).position);
		menu.setHeaderTitle(character.toString());
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.searchcontextmenu_add_favorite:
			AdapterView.AdapterContextMenuInfo cmi = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();
			WOWCharacter character = (WOWCharacter) getListAdapter().getItem(
					cmi.position);
			Model.getInstance().addFavorite(character);
			return true;
		}
		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		WOWCharacter character = (WOWCharacter) getListAdapter().getItem(
				position);
		model.getInfos(character);
	}

	/**
	 * auf Aenderungen im EditText reagieren
	 */
	private void handleTextChanged() {
		// Suchbutton de-/aktivieren
		if (et.getText().toString().length() == 0) {
			bt.setEnabled(false);
		} else {
			bt.setEnabled(true);
		}
	}

	/**
	 * XML auswerten
	 * 
	 * @param xmlString
	 */
	public void interpretXML(String xmlString) {
		StringReader inStream = new StringReader(xmlString);
		InputSource inSource = new InputSource(inStream);
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			// dbf.setValidating( false );
			// dbf.setNamespaceAware( false );
			doc = db.parse(inSource);
			// Demo TODO Auswerten (DTD evtl. nutzen), Bilder laden etc.
			NodeList nodeList = doc.getElementsByTagName("character");
			Node node = nodeList.item(0);
			NamedNodeMap nodesMap = node.getAttributes();
			// tv.setText(node.getLocalName());
			for (int i = 0; i < nodesMap.getLength(); i++) {
				Node n = nodesMap.item(i);
				// tv.append(n.getNodeName() + ": " + n.getNodeValue() + "; ");
			}
		} catch (SAXException e) {
			Log.e("Search", "SAX" + e.getMessage());
		} catch (IOException e) {
			Log.e("Search", "URI" + e.getMessage());
		} catch (ParserConfigurationException e) {
			Log.e("Search", "Parser Implementation" + e.getMessage());
		}
	}
}
