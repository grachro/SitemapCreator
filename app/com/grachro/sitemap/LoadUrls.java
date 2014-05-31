package com.grachro.sitemap;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LoadUrls {
	private Map<String, LoadedUrl> loaded = new LinkedHashMap<String, LoadedUrl>();
	private List<String> errUrls = new ArrayList<String>();

	public List<LoadedUrl> load(int maxDeep, String url) {
		try {
			load(maxDeep, 0, url);

			System.out.println("=====================");
			System.out.println("load finish");
			for (String errUrl : errUrls) {
				System.out.println("error:" + errUrl);
			}
			System.out.println("=====================");

			return new ArrayList<LoadedUrl>(loaded.values());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void load(int maxDeep, int deep, String url) throws IOException {

		ConnectResult connectResult = getDocument(url);
		if (connectResult == null) {
			return;
		}
		Document document = connectResult.getDocument();

		System.out.println(deep + "\t" + url + "\t" + document.title() + "\t" + getMetaDescription(document) + "\t" + connectResult.getTime() + "\t"
				+ document.html().length());

		LoadedUrl lUrl = new LoadedUrl();
		lUrl.deep = deep;
		lUrl.url = url;
		lUrl.title = document.title();
		lUrl.description = getMetaDescription(document);
		lUrl.loadTime = connectResult.getTime();
		lUrl.textLength = document.html().length();

		loaded.put(url, lUrl);

		if (deep >= maxDeep) {
			return;
		}

		Elements aElms = document.getElementsByTag("a");
		for (Element aTag : aElms) {
			String childUrl = aTag.attr("href");

			if (StringUtils.isEmpty(childUrl)) {
				continue;
			} else if (childUrl.startsWith("#")) {
				continue;
			} else if (childUrl.toLowerCase().startsWith("javascript")) {
				continue;
			} else if (childUrl.toLowerCase().startsWith("mailto:")) {
				continue;
			}

			if (childUrl.startsWith("http://")) {
			} else if (childUrl.startsWith("http://")) {
			} else if (childUrl.startsWith("//")) {
				String protocol = new URL(url).getProtocol();
				childUrl = protocol + ":" + childUrl;
			} else if (childUrl.startsWith("./")) {

				URL urlObj = new URL(url);
				String protocol = urlObj.getProtocol();
				String host = urlObj.getHost();
				String path = urlObj.getPath();

				childUrl = protocol + "://" + host + path + childUrl.substring(1);
			} else if (childUrl.startsWith("/")) {

				URL urlObj = new URL(url);
				String protocol = urlObj.getProtocol();
				String host = urlObj.getHost();
				String path = urlObj.getPath();

				childUrl = protocol + "://" + host + path + childUrl;
			} else {
				URL urlObj = new URL(url);
				String protocol = urlObj.getProtocol();
				String host = urlObj.getHost();
				String path = urlObj.getPath();

				childUrl = protocol + "://" + host + path + "/" + childUrl;
			}
			if (loaded.containsKey(childUrl)) {
				continue;
			}

			try {
				load(maxDeep, deep + 1, childUrl);
			} catch (Exception e) {
				System.out.println("err=" + url);
				e.printStackTrace();
			}
		}

	}

	private ConnectResult getDocument(String url) {
		try {
			try {
				return getConnectAndGet(url);
			} catch (IOException e) {
				Thread.sleep(3000);
				return getConnectAndGet(url);
			}
		} catch (Exception e) {
			errUrls.add(url);
			return null;
		}
	}

	private ConnectResult getConnectAndGet(String url) throws IOException {
		Date start = new Date();
		Document doc = Jsoup.connect(url).get();
		Date end = new Date();

		return new ConnectResult(doc, end.getTime() - start.getTime());

	}

	private String getMetaDescription(Document document) {
		Elements elms = document.select("meta[name=description]");
		if (elms.isEmpty()) {
			return StringUtils.EMPTY;
		}

		String des = elms.first().attr("content");

		des = des.replace("\r\n", "[改行]");
		des = des.replace("\n", "[改行]");
		des = des.replace("\r", "[改行]");
		des = des.replace("\t", " ");

		return des;
	}

	private static class ConnectResult {

		private final Document document;
		private final long time;

		public ConnectResult(Document document, long time) {
			super();
			this.document = document;
			this.time = time;
		}

		public Document getDocument() {
			return document;
		}

		public long getTime() {
			return time;
		}

	}
}
