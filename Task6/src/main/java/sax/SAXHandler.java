package sax;

import reflection.ReflectionHelper;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

class SAXHandler extends DefaultHandler {
	private static final String CLASSNAME = "class";
	private String element = null;
	private Object object = null;
	
	Object getObject() {
		return object;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName != CLASSNAME) {
			element = qName;
		} else {
			String className = attributes.getValue(0);
			object = ReflectionHelper.createInstance(className);
		}
	}
	
	public void endElement(String uri, String localName, String qName) {
		element = null;
	}
	
	public void characters(char[] ch, int start, int length) {
		if (element != null) {
			String value = new String(ch, start, length);
			ReflectionHelper.setFieldValue(object, element, value);
		}
	}
}