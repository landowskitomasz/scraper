package com.tennizoom.scraper.worker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
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
	
	private LinkedBlockingQueue<Task> tasksQueue;
	
	public ScraperWorker(LinkedBlockingQueue<Task> categoriesToProcess){
		this.loader = new HtmlLoader();
		this.tasksQueue = categoriesToProcess;
		log.info("Worker created.");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		log.info("Worker started.");
		try{
			for(Task task = tasksQueue.poll(3, TimeUnit.SECONDS); task != null; task = tasksQueue.poll()){
				try{
					task.registerProcessing();
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
				catch(Exception e){
					if(task.isProcessingAvailable()){
						log.error("Unable to complite task (category "+task.getCategory().getName()+"). Task added to queue again.", e);
						tasksQueue.put(task);
					}
					else{
						log.error("Task (category "+task.getCategory().getName()+") processed 3 times with filure. Task dismissed.", e);
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
