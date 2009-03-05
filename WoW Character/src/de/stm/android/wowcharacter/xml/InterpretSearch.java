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

public class InterpretSearch extends DefaultHandler {
	ArrayList<WOWCharacter> al;

	Region region;
	
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
	    if (localName.trim().equals("character")) {
	    	WOWCharacter sr = new WOWCharacter();
	    	sr.put( "NAME", attributes.getValue("name") );
	    	sr.put( "SERVER", attributes.getValue("realm") );
	    	sr.put( "LEVEL", attributes.getValue("level") );	    	
	    	sr.put( "URL", attributes.getValue("url") );
	    	sr.put( "REGION", region.name() );
	    	
	    	al.add(sr);
	    }
	}
	
	public ArrayList<WOWCharacter> readXML(String xml, Region region) {
		this.region = region;
		al = new ArrayList<WOWCharacter>();
		
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			xr.setContentHandler(this);			
			xr.parse(new InputSource(new StringReader(xml)));
		} catch (ParserConfigurationException e) {
			Log.e("ArmorySearch", e.getMessage());
		} catch (SAXException e) {
			Log.e("ArmorySearch", e.getMessage());
		} catch (IOException e) {
			Log.e("ArmorySearch", e.getMessage());
		}
		
		return al;
	}	
}
