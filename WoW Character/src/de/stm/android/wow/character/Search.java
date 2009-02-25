package de.stm.android.wow.character;

import java.io.IOException;
import java.io.StringReader;
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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import de.stm.android.wow.character.util.Armory;
import de.stm.android.wow.character.util.Const;

/**
 * Suchdialog
 * 
 * @author tfunke
 */
public class Search extends Activity implements Const {
	/** URL */
	private String ssearch;
	/** ScrollView */
	//private ScrollView sv;
	/** TextView */
	//private TextView tv;
	/** EditText (URL Eingabefeld) */
	private EditText et;
	/** Button */
	private Button bt;
	/** geladene XML Seite */
	private StringBuilder sbXMLPage;
	/** Armory */
	private Armory armory = new Armory();
	/** Region */
	private Armory.R.Region region;
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (sbXMLPage != null && sbXMLPage.length() > 0) {
				//tv.setText(sbXMLPage);
				//sv.scrollTo(0, 0);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	/**
	 * Initialisierungen
	 */
	private void init() {
		/** View setzen */
		setContentView(R.layout.search);
		
		/** Textfeld finden */
		et = (EditText) findViewById(R.id.editTextName);
		et.setOnKeyListener(new EditText.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//Log.i("search", Integer.toString(keyCode) + "~" + et.getText().toString());
				
				//Suchbutton de-/aktivieren
				if (et.getText().toString().length() == 0) {
					bt.setEnabled(false);
				} else {
					bt.setEnabled(true);
				};
				
				//Return-Taste "deaktiviert"
				if (keyCode == 66) {
					return true;
				};
				return false;
			}
		});
		
		/** OnClickListener für Suchenbutton setzen */
		bt = (Button) findViewById(R.id.buttonSearch);
		bt.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				//tv.setText("loading webpage...");
				//sv.scrollTo(0, 0);
				ssearch = et.getText().toString();

				Thread background = new Thread(new Runnable() {
					public void run() {
						try {

							sbXMLPage = armory.search(ssearch, region);

							handler.sendMessage(handler.obtainMessage());
						} catch (Throwable t) {
							// just end the background thread
						}
					}
				}, "WOW-Search");

				background.start();

			}
		});
		

		/** 
		 * Bei Auswahl der Sparche in Android wird bei deutsch nur de geliefert (ohne Ländercode)
		 *  daher fallback auf von getCountry() auf getLanguage() eingebaut
		 */
		RadioGroup rg = (RadioGroup) findViewById(R.id.radio_region);
		String locale = Locale.getDefault().getCountry();
		if (locale.length() == 0) {
			locale = Locale.getDefault().getLanguage();
		}
		
		if (locale.equalsIgnoreCase("US")) {
			rg.check(R.id.radio_US);
		} else {
			rg.check(R.id.radio_EU);
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
			//tv.setText(node.getLocalName());
			for (int i = 0; i < nodesMap.getLength(); i++) {
				Node n = nodesMap.item(i);
				//tv.append(n.getNodeName() + ": " + n.getNodeValue() + "; ");
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
