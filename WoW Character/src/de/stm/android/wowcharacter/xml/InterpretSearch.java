package de.stm.android.wowcharacter.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import de.stm.android.wowcharacter.data.Character;
import de.stm.android.wowcharacter.util.Armory.R.Region;

/**
 * Suchergebnis (XML-Daten) interpretieren
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>,
 * <a href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 * 
 */
public class InterpretSearch extends DefaultHandler {
	private static final String TAG = "InterpretSearch";
	private ArrayList<Character> listModel;
	private Region region;

	@Override
	public void startElement( String uri, String localName, String name, Attributes attributes )
			throws SAXException {
		if (localName.trim().equals( "character" )) {
			try {
				Character sr = new Character();
				sr.put( Character.Data.NAME, attributes.getValue( "name" ) );
				sr.put( Character.Data.REALM, attributes.getValue( "realm" ) );
				sr.put( Character.Data.FACTIONID, attributes.getValue( "factionId" ) );
				sr.put( Character.Data.LEVEL, attributes.getValue( "level" ) );
				sr.put( Character.Data.GENDERID, attributes.getValue( "genderId" ) );
				sr.put( Character.Data.RACE, attributes.getValue( "race" ) );
				sr.put( Character.Data.RACEID, attributes.getValue( "raceId" ) );
				sr.put( Character.Data.CLASS, attributes.getValue( "class" ) );
				sr.put( Character.Data.CLASSID, attributes.getValue( "classId" ) );
				sr.put( Character.Data.GUILD, attributes.getValue( "guild" ) );
				sr.put( Character.Data.URL, attributes.getValue( "url" ) );
				sr.put( Character.Data.REGION, region.name() );
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
	public void readXML( String xml, Region region, ArrayList<Character> listModel ) {
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
