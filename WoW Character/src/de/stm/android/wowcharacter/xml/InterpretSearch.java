package de.stm.android.wowcharacter.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import de.stm.android.wowcharacter.data.WOWCharacter;
import de.stm.android.wowcharacter.util.Armory.R.Region;

public class InterpretSearch extends DefaultHandler {
	private static final String TAG = "InterpretSearch";
	
	ArrayList<WOWCharacter> al;

	Region region;

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (localName.trim().equals("character")) {
			try {
				WOWCharacter sr = new WOWCharacter();
				sr.put("NAME", attributes.getValue("name"));
				sr.put("REALM", attributes.getValue("realm"));
				sr.put("LEVEL", new Integer(attributes.getValue("level")));
				sr.put("RACE", attributes.getValue("race"));
				sr.put("CLASS", attributes.getValue("class"));
				sr.put("GUILD", attributes.getValue("guild"));
				sr.put("URL", attributes.getValue("url"));
				sr.put("REGION", region.name());
				al.add(sr);
			} catch (Exception e) {
				/** */
			}
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
			Log.e(TAG, e.getMessage());
		} catch (SAXException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return al;
	}
}
