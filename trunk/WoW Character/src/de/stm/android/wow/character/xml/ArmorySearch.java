package de.stm.android.wow.character.xml;

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
import de.stm.android.wow.character.util.SearchResult;

public class ArmorySearch extends DefaultHandler {
	ArrayList<SearchResult> al;

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
	    if (localName.trim().equals("character")) {
	    	SearchResult sr = new SearchResult();
	    	sr.name = attributes.getValue("name");
	    	sr.realm = attributes.getValue("realm");
	    	
	    	al.add(sr);
	    }
	}
	
	public ArrayList<SearchResult> readXML(String xml) {
		al = new ArrayList<SearchResult>();
		
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
