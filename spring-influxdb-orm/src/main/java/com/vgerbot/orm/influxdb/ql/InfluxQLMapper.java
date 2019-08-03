package com.vgerbot.orm.influxdb.ql;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.ql.InfluxQLStatement.ACTION;

public class InfluxQLMapper {
	private static ResourceLoader resourceLoader = new DefaultResourceLoader();

	private Map<String, InfluxQLStatement> statementsMap = new HashMap<>();

	private static DocumentLoader documentLoader = new DefaultDocumentLoader();

	private static final Logger logger = Logger.getLogger(InfluxQLMapper.class.getName());

	private static final ErrorHandler DOCUMENT_BUILDER_ERROR_HANDLER = new ErrorHandler() {

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			logger.log(Level.WARNING, exception.getMessage(), exception);
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			logger.log(Level.SEVERE, exception.getMessage(), exception);
		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			logger.log(Level.OFF, exception.getMessage(), exception);
		}
	};

	private InfluxQLMapper() {
	}

	public InfluxQLStatement getStatement(String key) {
		return statementsMap.get(key);
	}

	public InfluxQLMapper union(InfluxQLMapper other) {
		InfluxQLMapper newmapper = new InfluxQLMapper();
		newmapper.statementsMap.putAll(this.statementsMap);
		newmapper.statementsMap.putAll(other.statementsMap);
		return newmapper;
	}

	public static final InfluxQLMapper empty() {
		return new InfluxQLMapper();
	}

	public static final InfluxQLMapper parseFrom(String resourceLocation) throws InfluxDBException {
		Resource resource = resourceLoader.getResource(resourceLocation);
		return parseFrom(resource);
	}

	private static final InfluxQLMapper parseFrom(Resource resource) throws InfluxDBException {

		InputSource inputSource;
		try {
			inputSource = new InputSource(resource.getInputStream());
		} catch (IOException e) {
			throw new InfluxDBException(e.getMessage(), e);
		}
		Document document = null;
		try {
			document = documentLoader.loadDocument(inputSource, null, DOCUMENT_BUILDER_ERROR_HANDLER,
					XmlValidationModeDetector.VALIDATION_XSD, false);
		} catch (Exception e) {
			throw new InfluxDBException("XML parse failed: " + resource.getDescription(), e);
		}
		Element root = document.getDocumentElement();
		NodeList includeNodeList = root.getElementsByTagName("include");

		InfluxQLMapper mapper = new InfluxQLMapper();

		for (int i = 0; i < includeNodeList.getLength(); i++) {
			Node item = includeNodeList.item(i);
			String location = item.getAttributes().getNamedItem("path").getNodeValue();
			Resource includedResource = resourceLoader.getResource(location);
			InfluxQLMapper includedMapper = parseFrom(includedResource);
			mapper = mapper.union(includedMapper);
		}

		mapper.statementsMap.putAll(parseQLTag(root, "select"));
		mapper.statementsMap.putAll(parseQLTag(root, "execute"));

		return mapper;
	}

	private static Map<String, InfluxQLStatement> parseQLTag(Element root, String tagName) {
		NodeList nodeList = root.getElementsByTagName(tagName);
		Map<String, InfluxQLStatement> map = new HashMap<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			String id = item.getAttributes().getNamedItem("id").getNodeValue();
			String ql = item.getTextContent().trim();
			if (map.containsKey(id)) {
				throw new InfluxDBException("Duplicate " + tagName + " ql id: " + id);
			}
			map.put(id, new InfluxQLStatement(ql, ACTION.valueOf(tagName.toUpperCase())));
		}
		return map;
	}

	public static final InfluxQLMapper parseFrom(InputStream stream) throws IOException {
		return parseFrom(new InputStreamResource(stream));
	}

}
