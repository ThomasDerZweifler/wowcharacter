package de.stm.android.wow.character;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Suchdialog
 * 
 * @author tfunke
 * 
 */
public class Search extends Activity {
	private URL url;
	private URLConnection urlConn = null;
	private ScrollView sv;
	private TextView tv;
	private EditText et;
	private Button bt;
	private String sURL = "http://eu.wowarmory.com/character-sheet.xml?r=Lothar&n=Etienne";

	private void init() {
		setContentView(R.layout.search);
		// displayXML("http://web.de");

		sv = (ScrollView) findViewById(R.id.scrollView);
		
		tv = (TextView) findViewById(R.id.textView);
		// tv.setMovementMethod(ArrowKeyMovementMethod.getInstance());
		// tv.setFocusable(true);

		et = (EditText) findViewById(R.id.editText);

		et.setText(sURL);

		bt = (Button) findViewById(R.id.button);
		bt.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				tv.setText("loading webpage...");
				sv.scrollTo(0, 0);
				String text = et.getText().toString();
				displayXML(text);
			}
		});
		
		bt.performClick();
	}

	/**
	 * Parse XML file and display
	 */
	public void displayXML(String strURL) {

		if( !strURL.startsWith("http://") ) {
			strURL = "http://"+strURL;
			et.setText( strURL );
		}
		
		try {
			url = new URL(strURL);
			
			//Wichtig, Zugriff erlauben durch: <uses-permission android:name="android.permission.INTERNET" /> (in manifest)
			urlConn = url.openConnection();
		} catch (IOException ioe) {
			Log.e("Search", "Could not connect to server!");
			tv.setText("check url!");
			return;
		}

		// Document doc = null;
		try {
			// DocumentBuilderFactory dbf =
			// DocumentBuilderFactory.newInstance();
			// DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = urlConn.getInputStream();

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			StringBuilder sb = new StringBuilder();
			
			while (br.ready()) {
				String line = br.readLine();
				sb.append(line);
			}

			tv.setText(sb);
			sv.scrollTo(0, 0);
			
			// doc = db.parse(is);
		} catch (IOException ioe) {
//			Log.e("Search", "Invalid XML format!!");
			tv.setText("check page!");

		}
		// catch (ParserConfigurationException pce) {
		// Log.e("Search", "Could not parse XML!");
		// }
		// catch (SAXException se) {
		// Log.e("Search", "Could not parse XML!");
		// }

		// String s = doc.toString();
		//		
		// Log.i("Search",s);
		//		
		// int size = doc.getElementsByTagName("tagName").getLength();
		//
		// for (int i = 0; i < size; i++) {
		// Element e = (Element) doc.getElementsByTagName("tagName").item(i);
		// }
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
}
