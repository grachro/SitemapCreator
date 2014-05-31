package com.grachro.sitemap;

public class LoadedUrl {
	public int deep;
	public String url;
	public String title;
	public String description;
	public long loadTime;
	public int textLength;

	public String saveLine() {
		return deep + "¥t" + url + "¥t" + title + "¥t" + description + "¥t" + loadTime + "¥t" + textLength;
	}
}
