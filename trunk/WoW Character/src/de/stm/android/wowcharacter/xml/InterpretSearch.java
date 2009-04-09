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
 * @version $Revision:  $Date: $
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>
 * @author <a href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 * 
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
				sr.put( WOWCharacter.Data.NAME, attributes.getValue( "name" ) );
				sr.put( WOWCharacter.Data.REALM, attributes.getValue( "realm" ) );
				sr.put( WOWCharacter.Data.FACTIONID, attributes.getValue( "factionId" ) );
				sr.put( WOWCharacter.Data.LEVEL, new Integer( attributes.getValue( "level" ) ) );
				sr.put( WOWCharacter.Data.GENDERID, attributes.getValue( "genderId" ) );
				sr.put( WOWCharacter.Data.RACE, attributes.getValue( "race" ) );
				sr.put( WOWCharacter.Data.RACEID, attributes.getValue( "raceId" ) );
				sr.put( WOWCharacter.Data.CLASS, attributes.getValue( "class" ) );
				sr.put( WOWCharacter.Data.CLASSID, attributes.getValue( "classId" ) );
				sr.put( WOWCharacter.Data.GUILD, attributes.getValue( "guild" ) );
				sr.put( WOWCharacter.Data.URL, attributes.getValue( "url" ) );
				sr.put( WOWCharacter.Data.REGION, region.name() );
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
