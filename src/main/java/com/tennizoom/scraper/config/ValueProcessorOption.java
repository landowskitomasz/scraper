package com.tennizoom.scraper.config;

import javax.xml.bind.annotation.XmlAttribute;

public class ValueProcessorOption {

	private String name;
	
	private String value;

	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name="value", required=true)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
