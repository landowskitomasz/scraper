package com.tennizoom.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import org.apache.log4j.Logger;
import com.tennizoom.scraper.config.ScraperConfiguration;
import com.tennizoom.scraper.config.ShopConfig;
import com.tennizoom.scraper.domain.Category;
import com.tennizoom.scraper.domain.Shop;
import com.tennizoom.scraper.worker.ScraperWorker;

public class App 
{
	private static Logger log = Logger.getLogger(App.class.getName());
	
	
	
    public static void main(String[] args )
    { 	
    	String configFilePath = null;
    	if(args.length > 0){
    		configFilePath = args[0];
    	}else{
    		throw new IllegalArgumentException("Configuration file path is required.");
    	}
    	
    	String outputDirectory = "out/";
    	if(args.length > 1){
    		outputDirectory = args[1];
    	}
    	SynchronousQueue<Category> categoriesToScrapp = new SynchronousQueue<Category>();
    	List<ScraperWorker> workers = new ArrayList<ScraperWorker>();
    	for(int i =0; i < 10; ++i){
    		ScraperWorker worker = new ScraperWorker(outputDirectory, categoriesToScrapp); 
    		workers.add(worker);
    		Thread thread = new Thread(worker);
    		thread.start();
    	}
    	
    	log.info("Loading configuration file.");
    	ScraperConfiguration configuration = ScraperConfiguration.getInstance(configFilePath);
    	
    	for(ShopConfig shopConfig : configuration.getShops()){
    		long start = System.currentTimeMillis();
    		
    		log.info("Loading shop configuration.");
    		Shop shop = Shop.getInstance(shopConfig.getPath());
    		for(Category category : shop.getCategories()){
    			log.info("Category added to queue.");
    			try {
					categoriesToScrapp.put(category);
				} catch (InterruptedException e) {
					log.error("Exception while trying to add category to the queue.", e);
				}
    		}
    		 
    		long end = System.currentTimeMillis();
    		log.info("Shop "+shopConfig.getName()+" ("+shop.getCategories().size()+" categories) scraped in "+(end-start) / 1000.0+" s.");
    	}
    }
    
}
