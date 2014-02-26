package com.tennizoom.scraper.worker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.tennizoom.scraper.Task;
import com.tennizoom.scraper.TasksStore;
import com.tennizoom.scraper.domain.Category;
import com.tennizoom.scraper.domain.DataEntry;
import com.tennizoom.scraper.exception.HtmlLoaderException;

public class ScraperWorker implements Runnable {
	
	private static Logger log = Logger.getLogger(ScraperWorker.class.getName());
	
	private HtmlLoader loader = new HtmlLoader();
	
	private TasksStore tasksStore;
	
	public ScraperWorker(TasksStore tasksStore){
		this.tasksStore = tasksStore;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		log.info("Worker started.");
		try{
			for(Task task = tasksStore.getTask(); task != null; task = tasksStore.getTask()){
					
				Category category = task.getCategory();
				
				Set<String> scrapedPages = new HashSet<String>();
				Queue<String> pagesToScrap = new LinkedList<String>();
				
				pagesToScrap.add(category.getUrl());
				try{
					List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
					
					while(!pagesToScrap.isEmpty()){
						String url = pagesToScrap.poll();
						
						scrapedPages.add(url);
						Document document = loader.loadCleanHtml(url);
						
						if(category.getPagination() != null){
							Set<String> newPages = category.getPagination().findPages(document);
							for(String newPage : newPages ){
								if(!scrapedPages.contains(newPage) && !pagesToScrap.contains(newPage)){
									pagesToScrap.add(newPage);
								}
							}
						}
						Map<String, Object> data = category.findData(document);
						for(DataEntry dataEntry: category.getDataEntries()){
							results.addAll((List<Map<String, Object>>)data.get(dataEntry.getName()));
						}
					}
					tasksStore.taskComplited(task, results);
				} catch(HtmlLoaderException e) {
					tasksStore.taskSuspended(task);
					log.error("Error while processing task for category " + category.getName() + ".", e);
				} catch(Exception e) {
					tasksStore.taskFailed(task);
					log.error("Error while processing task for category " + category.getName() + " task rejected.", e);
				}
			}
		}catch(InterruptedException e){
			log.error("Unable to get task to process.",e);
		}
		log.info("Closing worker.");
	}

}
