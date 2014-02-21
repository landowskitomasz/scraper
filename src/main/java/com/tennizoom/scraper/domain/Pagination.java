package com.tennizoom.scraper.domain;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Pagination {
	
	private static Logger log = Logger.getLogger(Pagination.class.getName());

	private String xPath;
	
	private String regex;
	
	@XmlAttribute(name="xPath", required=true)
	public String getxPath() {
		return xPath;
	}

	public void setxPath(String xPath) {
		this.xPath = xPath;
	}
	
	@XmlAttribute(name="regex", required=true)
	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	public Set<String> findPages(Document document){
		Set<String> pages = new HashSet<String>();
		log.info("Looking for new pages.");
		try {
			XPath xPath = new DOMXPath(getxPath());
			List<?> pageNodes = xPath.selectNodes(document);
			for(Object node : pageNodes){
				String value;
				if (node instanceof Node) {
					value = ((Node) node).getTextContent();
				} else {
					value = String.valueOf(node);
				}
				value = StringEscapeUtils.unescapeHtml(value);
				value = value.trim();
				if(!value.startsWith("http")){
					try {
						URL url = new URL(getRegex());
						int index = getRegex().indexOf("//");
						String shopUrl = getRegex().substring(0, index+2);
						value = shopUrl + url.getHost() + value;
					} catch (MalformedURLException e) {
						log.warn("Unable to get host from regex.");
					}
					
				}
				if(!StringUtils.isEmpty(value) && value.matches(getRegex())){
					pages.add(value);
				}
				else{
					log.warn("Page url "+value+" is empty or not matches regex: " + getRegex());
				}
			}
		} catch (PatternSyntaxException e){
			log.error("Regex to check page link incorrect.",e);
		} catch (JaxenException e) {
			log.error("Unable to find pages in document.",e);
		}
		log.info("Found "+ pages.size()+" page links in file.");
		return pages;
	}
}
