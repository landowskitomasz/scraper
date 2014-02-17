package com.tennizoom.scraper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.tennizoom.scraper.config.ScraperConfiguration;
import com.tennizoom.scraper.config.ShopConfig;
import com.tennizoom.scraper.domain.Category;
import com.tennizoom.scraper.domain.Shop;
import com.thoughtworks.xstream.XStream;

public class App 
{
	private static Logger log = Logger.getLogger(App.class.getName());
	
	public static final boolean DEBUG = true;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	
    public static void main(String[] args )
    { 	
    	String configFilePath = "com/tennizoom/scraper/config.xml";
    	if(args.length > 0){
         configFilePath = args[0];
    	} 
    	
    	String outputDirectory = "out/";
    	if(args.length > 1){
    		outputDirectory = args[1];
    	}
    	
    	log.info("Loading configuration file.");
    	ScraperConfiguration configuration = ScraperConfiguration.getInstance(configFilePath);
    	HtmlLoader loader = new HtmlLoader();
    	
    	for(ShopConfig shopConfig : configuration.getShops()){
    		long start = System.currentTimeMillis();
    		int count = 0;
    		log.info("Loading shop configuration.");
    		Shop shop = Shop.getInstance(shopConfig.getPath());
    		for(Category category : shop.getCategories()){
    			Document document = loader.loadCleanHtml(category.getUrl());
    			Map<String, Object> data = category.findData(document);
    			if(((List<?>)data.get("product")).size()==0){
    				log.warn("Not found any data in category.                             !!!");
    				//throw new IllegalStateException("Not found any data in category.");
    			}
    			
    			XStream magicApi = new XStream();
    	        magicApi.registerConverter(new MapEntryConverter());
    	        magicApi.alias("products", Map.class);
    	        String xml = magicApi.toXML(data);
    	        save(outputDirectory+ shopConfig.getName()+ "_"+category.getName()+"_"+dateFormat.format(new Date())+".xml", xml);
    	        ++count;
    		}
    		 
    		long end = System.currentTimeMillis();
    		log.info("Shop "+shopConfig.getName()+" ("+count+" categories) scraped in "+(end-start) / 1000.0+" s.");
    	}
    }
    
    private static void save(String fileName, String xml){
    	FileOutputStream fop = null;
		File file;
		try {
 
			file = new File(fileName);
			fop = new FileOutputStream(file);
 
			if (!file.exists()) {
				file.createNewFile();
			}
 
			byte[] contentInBytes = xml.getBytes();
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
			log.info(file.getAbsolutePath() + " saved.");
		} catch (IOException e) {
			log.error("Unable to save file.", e);
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				log.error("Unable to close file.", e);
			}
		}
    }
}
