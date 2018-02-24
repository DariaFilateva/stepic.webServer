package sax;

import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ReadXMLFileSAX {
	public static Object readXML(String path) {
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			
			SAXHandler saxHandler = new SAXHandler();
			saxParser.parse(path, saxHandler);
			
			return saxHandler.getObject();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}