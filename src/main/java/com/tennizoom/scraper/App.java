package com.tennizoom.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import com.tennizoom.scraper.config.ScraperConfiguration;
import com.tennizoom.scraper.config.ShopConfig;
import com.tennizoom.scraper.domain.Category;
import com.tennizoom.scraper.domain.Shop;
import com.tennizoom.scraper.worker.ScraperWorker;

public class App 
{
	private static Logger log = Logger.getLogger(App.class.getName());
	
	private LinkedBlockingQueue<Task> tasksQueue = new LinkedBlockingQueue<Task>(10);
	
    public static void main(String[] args )
    { 	
    	String configFilePath = null;
    	if(args.length > 0){
    		configFilePath = args[0];
    	}else{
    		throw new IllegalArgumentException("Configuration file path is required.");
    	}
    	
    	App app = new App();
    	app.start(configFilePath);
    }

	private void start(String configFilePath) {
		log.info("Loading configuration file.");
    	ScraperConfiguration configuration = ScraperConfiguration.getInstance(configFilePath);
    	
    	List<ScraperWorker> workers = new ArrayList<ScraperWorker>();
    	for(int i =0; i < configuration.getThreadsNumber(); ++i){
    		ScraperWorker worker = new ScraperWorker(tasksQueue); 
    		workers.add(worker);
    		Thread thread = new Thread(worker);
    		thread.start();
    	}
    	
    	String outputDirectory = "out/";
    	if(configuration.getOutputDirectory() != null){
    		outputDirectory = configuration.getOutputDirectory();
    	}
    	
    	for(ShopConfig shopConfig : configuration.getShops()){
    		log.info("Loading shop configuration.");
    		Shop shop = Shop.getInstance(shopConfig.getPath());
    		
    		XmlExport xmlExport = new XmlExport(outputDirectory, shopConfig.getName());
    		Thread thread = new Thread(xmlExport);
    		thread.start();
    		for(Category category : shop.getCategories()){
    			Task task = new Task(category, xmlExport.getResultPipe());
    			log.info("Task created for category " + category.getName());
    			
    			try {
					tasksQueue.put(task);
				} catch (InterruptedException e) {
					log.error("Exception while trying to add category to the queue.", e);
				}
    		}
    	}
	}
    
}
