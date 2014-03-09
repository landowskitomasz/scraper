package com.tennizoom.scraper.domain;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Node;

import com.tennizoom.scraper.config.DefaultValueProcessorsPropagation;
import com.tennizoom.scraper.config.ValueProcessorConfig;
import com.tennizoom.scraper.exception.FieldRequiredException;
import com.tennizoom.scraper.exception.ValidateException;
import com.tennizoom.scraper.processor.ValueProcessorHelper;

public class DataField implements Cloneable {

	private static Logger log = Logger.getLogger(DataField.class.getName());

	private String name;
	
	private String xPath;
	
	private boolean required = true;
	
	private boolean debug;
	
	private DefaultValueProcessorsPropagation callDefaultValueProcessors = DefaultValueProcessorsPropagation.after; 
	
	private List<ValueProcessorConfig> valueProcessors = new ArrayList<ValueProcessorConfig>();

	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name="xPath", required=true)
	public String getxPath() {
		return xPath;
	}

	public void setxPath(String xPath) {
		this.xPath = xPath;
	}

	@XmlElement(name="valueProcessor", nillable=true)
	public List<ValueProcessorConfig> getValueProcessors() {
		return valueProcessors;
	}

	public void setValueProcessors(List<ValueProcessorConfig> valueProcessors) {
		this.valueProcessors = valueProcessors;
	}

	@XmlAttribute(name="required", required=false)
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@XmlAttribute(name="debug", required=false)
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	

	public DefaultValueProcessorsPropagation getCallDefaultValueProcessors() {
		return callDefaultValueProcessors;
	}
	
	@XmlAttribute(name="callDefaultValueProcessors", required=false)
	public void setCallDefaultValueProcessors(
			DefaultValueProcessorsPropagation callDefaultValueProcessors) {
		this.callDefaultValueProcessors = callDefaultValueProcessors;
	}

	public String findFieldValue(Object node, List<ValueProcessorConfig> defaultValueProcessors) throws FieldRequiredException {
		log_info("Processing node: \n" + toXml((Node)node));
		String value = null;
		try {
			Object foundEntryValue = node;
			if(!StringUtils.isEmpty(getxPath())){
				XPath xPath = new DOMXPath(getxPath());
				foundEntryValue = xPath.selectSingleNode(node);
			}
			
			if (foundEntryValue instanceof Node) {
				value = ((Node) foundEntryValue).getTextContent();
			} else {
				value = String.valueOf(foundEntryValue);
			}
			
			if(!StringUtils.isEmpty(value)){
				value = value.trim();
			}
			log_info("Value before processing: " + value);
			try{
				
				value =  ValueProcessorHelper.callProcessors(getAllValueProcessors(defaultValueProcessors), value);
				log_info("Value after processing: " + value);
			}
			catch(ValidateException e){
				throw new ValidateException("Field '"+getName()+"' has invalid data.", e);
			}
		} catch (JaxenException e) {
			log.error("Unavle to find '"+getName()+"' field value in entry node.",e);
		}
		
		if(isRequired() && StringUtils.isEmpty(value)){
			throw new FieldRequiredException("Field " + getName() + " is required.");
		}
		return value;
	}	
	
	private List<ValueProcessorConfig> getAllValueProcessors(
			List<ValueProcessorConfig> defaultValueProcessors) {
		List<ValueProcessorConfig> valueProcessors = new ArrayList<ValueProcessorConfig>();
		switch (callDefaultValueProcessors) {
		case after:
			valueProcessors.addAll(getValueProcessors());
			valueProcessors.addAll(defaultValueProcessors);
			break;
		case before:
			valueProcessors.addAll(defaultValueProcessors);
			valueProcessors.addAll(getValueProcessors());
			break;
		case none:
		default:
			valueProcessors.addAll(getValueProcessors());
			break;
		}

		return valueProcessors;
	}

	private void log_info(String string) {
		if(isDebug()){
			log.info(string);
		}
	}

	protected String toXml(Node node){
		Transformer transformer;
		try {
			StringWriter writer = new StringWriter();
				transformer = TransformerFactory.newInstance().newTransformer();
			
			transformer.transform(new DOMSource(node), new StreamResult(writer));
			
			return writer.toString();
		} catch (TransformerConfigurationException e) {
			log.error("Unable to conver node to string",e);
		} catch (TransformerFactoryConfigurationError e) {
			log.error("Unable to conver node to string",e);
		} catch (TransformerException e) {
			log.error("Unable to conver node to string",e);
		}
		return null;
	}
}
