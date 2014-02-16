package com.tennizoom.scraper.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Shop {

	private String name;
	
	private List<Category> categories = new ArrayList<Category>();
	
	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}

	public void setName(String shop) {
		this.name = shop;
	}

	@XmlElement(name="category", nillable=true)
	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
}
