package com.vgerbot.test.orm.influxdb.testcase;

import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlValidation {
	public static void main(String[] args) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new URL("http://www.vgerbot.com/schema/influxdb/influxdb.xsd"));
		factory.setValidating(true);
		factory.setSchema(schema);

		SAXParser parser = factory.newSAXParser();

		parser.parse(XmlValidation.class.getClassLoader().getResourceAsStream("vld.xml"), new DefaultHandler() {
			@Override
			public void startDocument() throws SAXException {
				System.out.println("start document");
			}

			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				System.out.println("start element: " + uri + ", " + localName + ", " + qName);
			}

			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				System.out.println("characters : " + new String(ch, start, length));
			}
		});
	}
}
