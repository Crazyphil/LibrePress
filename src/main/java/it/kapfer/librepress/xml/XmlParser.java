package it.kapfer.librepress.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class XmlParser {
    private final Document xmlDocument;

    public XmlParser(String xml) {
        this.xmlDocument = parseDocument(xml);
    }

    private Document parseDocument(String xml) {
        try (InputStream xmlStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            return parseDocument(xmlStream);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new IllegalArgumentException("Could not parse given XML string", e);
        }
    }

    private Document parseDocument(InputStream xmlStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlStream);
    }

    public String getAttribute(String element, String attribute) {
        NodeList nodes = xmlDocument.getElementsByTagName(element);
        if (nodes.getLength() == 0) {
            throw new IllegalStateException("Element '" + element + "' not found in XML");
        }
        Node node = nodes.item(0);
        return node.getAttributes().getNamedItem(attribute).getTextContent();
    }
}
