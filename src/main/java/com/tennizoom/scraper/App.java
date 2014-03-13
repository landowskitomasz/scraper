package com.tennizoom.scraper;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.tennizoom.scraper.config.ScraperConfiguration;
import com.tennizoom.scraper.config.ShopConfig;
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
    	
    	App app = new App();
    	app.start(configFilePath);
    }

	private void start(String configFilePath) {
		log.info("Loading configuration file.");
    	ScraperConfiguration configuration = ScraperConfiguration.getInstance(configFilePath);
    	String outputDirectory = "out/";
    	if(configuration.getOutputDirectory() != null){
    		outputDirectory = configuration.getOutputDirectory();
    	}
    	
    	ResultsHandler resultsHandler = new Md5IdGenerationHandlerWraper(
    			new StatisticsWrapper(
    					new ResultsHandlerImpl(outputDirectory)));
    	TasksStore tasksStore = new TasksStoreImpl(resultsHandler);
    	
    	List<ScraperWorker> workers = new ArrayList<ScraperWorker>();
    	for(int i =0; i < configuration.getThreadsNumber(); ++i){
    		ScraperWorker worker = new ScraperWorker(tasksStore); 
    		workers.add(worker);
    		Thread thread = new Thread(worker);
    		thread.setName("Scraper worker " + i);
    		thread.start();
    	}
    	
    	for(ShopConfig shopConfig : configuration.getShops()){
    		log.info("Loading shop configuration.");
    		Shop shop = Shop.getInstance(shopConfig.getPath());
    		
    		tasksStore.createTasks(shop.getCategories(), shopConfig.getName());
    	}
	}
    
}
