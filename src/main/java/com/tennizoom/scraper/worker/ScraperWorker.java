package com.tennizoom.scraper.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.tennizoom.scraper.HtmlLoader;
import com.tennizoom.scraper.Task;
import com.tennizoom.scraper.domain.Category;
import com.tennizoom.scraper.domain.DataEntry;

public class ScraperWorker implements Runnable {
	
	private static Logger log = Logger.getLogger(ScraperWorker.class.getName());
	
	private HtmlLoader loader;
	
	private LinkedBlockingQueue<Task> categoriesToProcess;
	
	public ScraperWorker(LinkedBlockingQueue<Task> categoriesToProcess){
		this.loader = new HtmlLoader();
		this.categoriesToProcess = categoriesToProcess;
		log.info("Worker created.");
	}
	
	@Override
	public void run() {
		log.info("Worker started.");
		try{
			for(Task task = categoriesToProcess.poll(3, TimeUnit.SECONDS); task != null; task = categoriesToProcess.poll()){
				log.info("Categry to scrap found.");
				Category category = task.getCategory();
				Document document = loader.loadCleanHtml(category.getUrl());
				Map<String, Object> data = category.findData(document);
				
				for(DataEntry dataEntry: category.getDataEntries()){
					for(Map<String, Object> entryValue : ((List<Map<String, Object>>)data.get(dataEntry.getName()))){
						task.putResult(entryValue);
					}
				}
			}
		}
		catch(InterruptedException e){
			log.error("Exception while waiting for next category to scrapp.", e);
		}
		log.info("No categories to scrap, closing worker.");
	}

}
