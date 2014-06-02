package com.grachro.sitemap;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SitemapXml {

	public void create(List<LoadedSite> urls, String filePath) {
		try {
			Document xmlDocument = createDocument(urls);
			write(xmlDocument, filePath);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private Document createDocument(List<LoadedSite> urls) throws ParserConfigurationException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		Element root = document.createElement("urlset");
		document.appendChild(root);

		for (LoadedSite url : urls) {
			addUrl(document, root, url);
		}

		return document;

	}

	private void addUrl(Document document, Element root, LoadedSite url) {
		Element urlElm = document.createElement("url");
		root.appendChild(urlElm);

		Element locElm = document.createElement("loc");
		urlElm.appendChild(locElm);
		locElm.setTextContent(url.url);
	}

	private void write(Document document, String filePath) throws TransformerException {

		File f = new File(filePath);
		StreamResult result = new StreamResult(f);

		DOMSource source = new DOMSource(document);

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty("encoding", "UTF-8");
		transformer.setOutputProperty("indent", "yes");
		transformer.transform(source, result);
	}
}
