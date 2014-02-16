package com.tennizoom.scraper;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;


public class HtmlLoader {
	
	private static Logger log = Logger.getLogger(HtmlLoader.class.getName());
	
	public Document loadCleanHtml(String urlString) {
		log.info("Loading html from: " + urlString);
		try {
			CleanerProperties props = new CleanerProperties();
			props.setAllowHtmlInsideAttributes(true);
	        props.setAllowMultiWordAttributes(true);
	        props.setRecognizeUnicodeChars(true);
	        props.setOmitComments(true);
	        props.setNamespacesAware(false);
	        
			TagNode tagNode = new HtmlCleaner(props).clean(
			    new URL(urlString)
			);
		
			log.info("Html loaded.");
			return new DomSerializer(props, true).createDOM(tagNode);
		} catch (IOException e) {
			log.error("Unable to load html page.",e);
		} catch (ParserConfigurationException e) {
			log.error("Unable to load html page.",e);
		}
		return null;
	}

}
