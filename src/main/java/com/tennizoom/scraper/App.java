package com.tennizoom.scraper;

import java.util.Map;

import org.w3c.dom.Document;

import com.tennizoom.scraper.config.Category;
import com.tennizoom.scraper.config.ScraperConfiguration;
import com.tennizoom.scraper.config.Shop;
import com.thoughtworks.xstream.XStream;

public class App 
{
	public static final boolean DEBUG = true;
	
    public static void main( String[] args )
    { 	
    	String configFilePath;
    	if(args.length > 0){
         configFilePath = args[0];
    	} else{
    		configFilePath = "com/tennizoom/scraper/config.xml";
    	}
    	
    	ScraperConfiguration configuration = ScraperConfiguration.getInstance(configFilePath);
    	HtmlLoader loader = new HtmlLoader();
    	
    	for(Shop shop : configuration.getShops()){
    		for(Category category : shop.getCategories()){
    			Document document = loader.loadCleanHtml(category.getUrl());
    			Map<String, Object> data = category.findData(document);
    			XStream magicApi = new XStream();
    	        magicApi.registerConverter(new MapEntryConverter());
    	        magicApi.alias("products", Map.class);
    	        String xml = magicApi.toXML(data);
    	        System.out.println(xml);
    		}
    	}
    }
}
