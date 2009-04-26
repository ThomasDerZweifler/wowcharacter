package de.stm.android.wowcharacter.xml;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.stm.android.wowcharacter.data.Item;

/**
 * Iteminfos (XML-Daten) interpretieren
 * 
 * @author <a href="mailto:thomasfunke71@googlemail.com">Thomas Funke</a>,
 * <a href="mailto:stefan.moldenhauer@googlemail.com">Stefan Moldenhauer</a>
 * 
 */
public class InterpretIteminfo {
	public static Item getData(StringBuilder xml, int id) {
		Document doc = getDoc(xml);

		NodeList nl = doc.getElementsByTagName("item");

		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			
			//node.
		}
		
		return null;
	}
	
	private static Document getDoc(StringBuilder xml) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(xml.toString())));

			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
