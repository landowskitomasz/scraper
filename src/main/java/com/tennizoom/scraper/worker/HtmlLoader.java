package com.tennizoom.scraper.worker;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

import com.tennizoom.scraper.exception.HtmlLoaderException;


public class HtmlLoader {
	
	private static Logger log = Logger.getLogger(HtmlLoader.class.getName());
	
	public Document loadCleanHtml(String urlString) throws HtmlLoaderException {
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
		
			Document document = new DomSerializer(props, true).createDOM(tagNode);
			
			log.info("Html loaded succesfully.");
			return document;
		} catch (IOException e) {
			throw new HtmlLoaderException("Unable to load html page.",e);
		} catch (ParserConfigurationException e) {
			throw new HtmlLoaderException("Unable to load html page.",e);
		}
	}

}
