package com.tennizoom.scraper.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;


public class Category {

	private static Logger log = Logger.getLogger(Category.class.getName());

	private String name;
	
	private String url;
	
	private List<DataEntry> dataEntries = new ArrayList<DataEntry>();

	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name="url", required=true)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@XmlElement(name="dataEntry", nillable=false)
	public List<DataEntry> getDataEntries() {
		return dataEntries;
	}

	public void setDataEntries(List<DataEntry> data) {
		this.dataEntries = data;
	}

	public Map<String, Object> findData(Document document) {
		Map<String, Object> entries = new HashMap<String, Object>();
		for(DataEntry entry : getDataEntries()){
			log.info("Looking for '"+ entry.getName() + "' entry values.");
			List<Map<String, Object>> entryValues = entry.findData(document);
			entries.put(entry.getName(), entryValues);
		}
		
		return entries;
	}
	
}
