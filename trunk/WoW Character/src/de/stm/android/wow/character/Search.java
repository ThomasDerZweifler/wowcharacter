package de.stm.android.wow.character;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

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
 */
public class Search extends Activity {
	/** URL */
	private URL url;
	/** URL */
	private String sURL = "http://eu.wowarmory.com/character-sheet.xml?r=Lothar&n=Etienne";
	/** ScrollView */
	private ScrollView sv;
	/** TextView */
	private TextView tv;
	/** EditText (URL Eingabefeld) */
	private EditText et;
	/** geladene XML Seite */
	private StringBuilder sbXMLPage;

	/**
	 * Initialisierungen
	 */
	private void init() {
		setContentView( R.layout.search );
		// displayXML("http://web.de");
		sv = (ScrollView)findViewById( R.id.scrollView );
		tv = (TextView)findViewById( R.id.textView );
		// tv.setMovementMethod(ArrowKeyMovementMethod.getInstance());
		// tv.setFocusable(true);
		et = (EditText)findViewById( R.id.editText );
		et.setText( sURL );
		Button bt = (Button)findViewById( R.id.buttonSearch );
		bt.setOnClickListener( new Button.OnClickListener() {
			public void onClick( View v ) {
				tv.setText( "loading webpage..." );
				sv.scrollTo( 0, 0 );
				sURL = et.getText().toString();
				sbXMLPage = getXML( sURL, true );
			}
		} );
		bt.performClick();
		bt = (Button)findViewById( R.id.buttonXML );
		bt.setOnClickListener( new Button.OnClickListener() {
			public void onClick( View v ) {
				interpretXML( sbXMLPage.toString() );
			}
		} );
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
			tv.setText( node.getLocalName() );
			for (int i = 0; i < nodesMap.getLength(); i++) {
				Node n = nodesMap.item( i );
				tv.append( n.getNodeName() + ": " + n.getNodeValue() + "; " );
			}
		} catch (SAXException e) {
			Log.e( "Search", "SAX" + e.getMessage() );
		} catch (IOException e) {
			Log.e( "Search", "URI" + e.getMessage() );
		} catch (ParserConfigurationException e) {
			Log.e( "Search", "Parser Implementation" + e.getMessage() );
		}
	}

	/**
	 * XML von URL lesen
	 * 
	 * @param strURL
	 * @param packed
	 * @return
	 */
	public StringBuilder getXML( String strURL, boolean packed ) {
		if (!strURL.startsWith( "http://" )) {
			strURL = "http://" + strURL;
			et.setText( strURL );
		}
		URLConnection urlConn = null;
		try {
			url = new URL( strURL );
			// Wichtig, Zugriff erlauben durch: <uses-permission
			// android:name="android.permission.INTERNET" /> (in manifest)
			urlConn = url.openConnection();
			urlConn.addRequestProperty( "User-Agent", "Firefox/3.0" );
//			urlConn
//					.addRequestProperty(
//							"User-Agent",
//							"Mozilla/5.0 (Windows; U; Windows NT 6.0; rv:1.9.1b3pre) Gecko/20090218 Firefox/3.0 SeaMonkey/2.0a3pre" );
//			urlConn.addRequestProperty( "Accept",
//					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
//			urlConn.addRequestProperty( "Accept-Charset", "ISO-8859-15,utf-8;q=0.7,*;q=0.7" );
//			urlConn.addRequestProperty( "Accept-Charset", "ISO-8859-1,ISO-8859-2" );

//			urlConn.addRequestProperty( "Accept-Language", "de-de,de;q=0.8" );
			urlConn.addRequestProperty( "Accept-Language", "en-us;q=0.5,en;q=0.3" );

			urlConn.addRequestProperty( "Accept-Encoding", packed ? "gzip,deflate" : "identity" );
		} catch (IOException ioe) {
			Log.e( "Search", "Could not connect to server!" );
			tv.setText( "check url!" );
			return null;
		}
		InputStream is = null;
		InputStreamReader isr = null;
		StringBuilder sb = null;
		try {
			is = urlConn.getInputStream();
			String encoding = urlConn.getContentEncoding();
			if (encoding != null ) {
				if( encoding.equalsIgnoreCase("gzip") ) {
					is = new GZIPInputStream( is );					
				} else if( encoding.equalsIgnoreCase("deflate") ) {
					is = new InflaterInputStream( is, new Inflater( true ) );
				}
			}
			isr = new InputStreamReader( is );
			sb = new StringBuilder();
			char chars[] = new char[1024];
			int len = 0;
			while ((len = isr.read( chars, 0, chars.length )) >= 0) {
				sb.append( chars, 0, len );
			}
			if( sb.length() > 0 ) {
				tv.setText( sb );				
				sv.scrollTo( 0, 0 );
			}
		} catch (IOException ioe) {
			Log.e( "Search", "Invalid format!!" );
			tv.setText( "check page!" );
		} finally {
			try {
				isr.close();
			} catch (Exception e) {
				Log.e( "Search", "finally" + e.getMessage() );
			}
		}
		return sb;
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		init();
	}
}
