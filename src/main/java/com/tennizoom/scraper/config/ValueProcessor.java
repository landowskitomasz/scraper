package com.tennizoom.scraper.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ValueProcessor {

	private ValueProcessorType processorType;
	
	private List<ValueProcessorOption> options = new ArrayList<ValueProcessorOption>();

	@XmlAttribute(name="processorType", required=true)
	public ValueProcessorType getProcessorType() {
		return processorType;
	}

	public void setProcessorType(ValueProcessorType processorType) {
		this.processorType = processorType;
	}

	@XmlElement(name="option", nillable=true)
	public List<ValueProcessorOption> getOptions() {
		return options;
	}

	public void setOptions(List<ValueProcessorOption> options) {
		this.options = options;
	}
	
	
}
