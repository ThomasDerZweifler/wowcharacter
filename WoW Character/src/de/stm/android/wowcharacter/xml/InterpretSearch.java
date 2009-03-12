package de.stm.android.wowcharacter.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import de.stm.android.wowcharacter.data.WOWCharacter;
import de.stm.android.wowcharacter.util.Armory.R.Region;

/**
 * Suchergebnis interpretieren
 * 
 * @version $Revision: $Date: $
 */
public class InterpretSearch extends DefaultHandler {
	private static final String TAG = "InterpretSearch";
	private ArrayList<WOWCharacter> listModel;
	private Region region;

	@Override
	public void startElement( String uri, String localName, String name, Attributes attributes )
			throws SAXException {
		if (localName.trim().equals( "character" )) {
			try {
				WOWCharacter sr = new WOWCharacter();
				sr.put( "NAME", attributes.getValue( "name" ) );
				sr.put( "REALM", attributes.getValue( "realm" ) );
				sr.put( "LEVEL", new Integer( attributes.getValue( "level" ) ) );
				sr.put( "RACE", attributes.getValue( "race" ) );
				sr.put( "CLASS", attributes.getValue( "class" ) );
				sr.put( "GUILD", attributes.getValue( "guild" ) );
				sr.put( "URL", attributes.getValue( "url" ) );
				sr.put( "ICON", null );
				sr.put( "REGION", region.name() );
				listModel.add( sr );
			} catch (Exception e) {
				/** */
			}
		}
	}

	/**
	 * XML String lesen
	 * 
	 * @param listView
	 * @param xml
	 * @param region
	 * @param listModel
	 * @return
	 */
	public void readXML( String xml, Region region, ArrayList<WOWCharacter> listModel ) {
		this.listModel = listModel;
		this.region = region;
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler( this );
			xr.parse( new InputSource( new StringReader( xml ) ) );
		} catch (ParserConfigurationException e) {
			Log.e( TAG, e.getMessage() );
		} catch (SAXException e) {
			Log.e( TAG, e.getMessage() );
		} catch (IOException e) {
			Log.e( TAG, e.getMessage() );
		}
	}
}
