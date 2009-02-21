package de.stm.android.wow.character;

import java.io.IOException;
import java.io.StringReader;

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
import de.stm.android.wow.character.util.Armory;

/**
 * Suchdialog
 * 
 * @author tfunke
 */
public class Search extends Activity {
	/** URL */
	private String ssearch;
	/** ScrollView */
	private ScrollView sv;
	/** TextView */
	private TextView tv;
	/** EditText (URL Eingabefeld) */
	private EditText et;
	/** geladene XML Seite */
	private StringBuilder sbXMLPage;
	/** Armory */
	private Armory armory = new Armory();

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
		//et.setText( sURL );
		Button bt = (Button)findViewById( R.id.buttonSearch );
		bt.setOnClickListener( new Button.OnClickListener() {
			public void onClick( View v ) {
				tv.setText( "loading webpage..." );
				sv.scrollTo( 0, 0 );
				ssearch = et.getText().toString();
				sbXMLPage = armory.search(ssearch, "EU");
				if( sbXMLPage.length() > 0 ) {
					tv.setText( sbXMLPage );				
					sv.scrollTo( 0, 0 );
				}
			}
		} );
		//bt.performClick();
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

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		init();
	}
}
