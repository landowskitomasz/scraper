package com.tennizoom.scraper;

import com.tennizoom.scraper.config.ScraperConfiguration;


/**
 * Hello world!
 *
 */
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
    	
    }
}
