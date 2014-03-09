package com.tennizoom.scraper.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;

import com.tennizoom.scraper.exception.FieldRequiredException;
import com.tennizoom.scraper.exception.ValidateException;

public class DataEntry implements Cloneable{
	
	private static Logger log = Logger.getLogger(DataEntry.class.getName());

	private String name;
	
	private String xPath;
	
	private List<DataField> fields = new ArrayList<DataField>();

	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name="field", nillable=false)
	public List<DataField> getFields() {
		return fields;
	}

	public void setFields(List<DataField> fields) {
		this.fields = fields;
	}
	
	@XmlAttribute(name="xPath", required=true)
	public String getxPath() {
		return xPath;
	}

	public void setxPath(String xPath) {
		this.xPath = xPath;
	}

	public List<Map<String, Object>> findData(Document document, DefaultValueProcessors defaultValueProcessors) {
		List<Map<String, Object>> entryValues = new ArrayList<Map<String,Object>>();
		try {
			String xPathQuery = getxPath();
			if(StringUtils.isEmpty(xPathQuery)){
				xPathQuery = "/*";
			}
			XPath xPath = new DOMXPath(xPathQuery);
			List<?> foundDataEntryNodes = xPath.selectNodes(document);
			
			log.info(String.format("Found %d entries.", foundDataEntryNodes.size()));
			for(Object node : foundDataEntryNodes){
				try{
					Map<String, Object> fields = new HashMap<String, Object>();
					for(DataField field : getFields()){
						Object value = field.findFieldValue(node, defaultValueProcessors.getFieldValueProcessors(field.getName()));
						fields.put(field.getName(),value);
					}
					entryValues.add(fields);
				} catch(ValidateException e){
					throw new ValidateException("Data entry '"+getName()+"' in "+
							(foundDataEntryNodes.indexOf(node)+1)+" node has invalid data.", e);
				}
				catch(FieldRequiredException e){
					log.warn("Node dismissed, reason: " + e.getMessage());
				}
			}
		} catch (JaxenException e) {
			log.error("Unable to find document entries.",e);
		}
		return entryValues;
	}
	
	
}
