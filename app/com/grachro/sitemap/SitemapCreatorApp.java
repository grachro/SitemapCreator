package com.grachro.sitemap;

import java.io.IOException;
import java.util.List;

public class SitemapCreatorApp {
	public static void main(String[] args) throws IOException {
		String rootUrl = args[0];
		int maxDeep = Integer.parseInt(args[1]);
		String filePath = args[2];
		new SitemapCreatorApp().execute(rootUrl, maxDeep, filePath);
	}

	public void execute(String rootUrl, int maxDeep, String filePath) {
		SiteLoader loader = new SiteLoader();
		loader.load(maxDeep, rootUrl);
		List<LoadedSite> urls = loader.getResult();

		new SitemapXml().create(urls, filePath);
	}
}
