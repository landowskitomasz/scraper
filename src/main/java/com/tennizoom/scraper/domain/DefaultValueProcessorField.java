package com.tennizoom.scraper.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.tennizoom.scraper.config.ValueProcessorConfig;

public class DefaultValueProcessorField {

	private String name;
	
	private List<ValueProcessorConfig> valueProcessors = new ArrayList<ValueProcessorConfig>();
	
	@XmlElement(name="valueProcessor", nillable=true)
	public List<ValueProcessorConfig> getValueProcessors() {
		return valueProcessors;
	}

	public void setValueProcessors(List<ValueProcessorConfig> valueProcessors) {
		this.valueProcessors = valueProcessors;
	}

	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
