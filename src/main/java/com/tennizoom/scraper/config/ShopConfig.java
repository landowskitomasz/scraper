package com.tennizoom.scraper.config;

import javax.xml.bind.annotation.XmlAttribute;

public class ShopConfig {

	private String name;
	
	private String path;
	
	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}

	public void setName(String shop) {
		this.name = shop;
	}

	@XmlAttribute(name="path", required=true)
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
