package com.tennizoom.scraper.config;

import javax.xml.bind.annotation.XmlAttribute;

public class ScraperConfigurationEntry {

	private String shop;
	
	private String category;
	
	private String url;

	@XmlAttribute(name="shop", required=true)
	public String getShop() {
		return shop;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}

	@XmlAttribute(name="category", required=true)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@XmlAttribute(name="url", required=true)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
