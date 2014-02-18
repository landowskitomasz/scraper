package com.tennizoom.scraper;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.apache.log4j.Logger;

import com.tennizoom.scraper.domain.Category;

public class Task {
	
	private static Logger log = Logger.getLogger(Task.class.getName());
	
	private Category category;
	
	private LinkedBlockingQueue<Map<String, Object>> resutsPipe;
	
	public Task(Category category, LinkedBlockingQueue<Map<String, Object>> resutsPipe){
		this.category = category;
		this.resutsPipe = resutsPipe;
	}
	
	public Category getCategory(){
		return this.category;
	}
	
	public void putResult(Map<String, Object> result){
		try {
			resutsPipe.put(result);
		} catch (InterruptedException e) {
			log.error("Can't put result to the pipe.", e);
		}
	}
}
