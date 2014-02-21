package com.tennizoom.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import com.tennizoom.scraper.domain.Category;

public class TasksStoreImpl implements TasksStore {
	
	private static Logger log = Logger.getLogger(TasksStoreImpl.class.getName());

	private LinkedBlockingQueue<Task> tasksToProcessQueue = new LinkedBlockingQueue<Task>(10);
	
	private LinkedBlockingQueue<Task> suspendedTasksQueue = new LinkedBlockingQueue<Task>();
	
	private List<Task> failedTasks = new ArrayList<Task>();

	private ResultsHandler resultsHandler;
	
	public TasksStoreImpl(ResultsHandler resultsHandler){
		this.resultsHandler = resultsHandler;
	}
	private Semaphore tasksAccessSemaphore = new Semaphore(1);
	
	@Override
	public BlockingQueue<Task> getTasks() {
		try {
			tasksAccessSemaphore.acquire();
			if(tasksToProcessQueue.isEmpty() && !suspendedTasksQueue.isEmpty()){
				try {
					Task task = suspendedTasksQueue.poll();
					if(task != null){
						tasksToProcessQueue.put(task);
					}
				} catch (InterruptedException e) {
					log.error("Unable to add suspended task to the queue.");
				}
			}
			
			tasksAccessSemaphore.release();
			return tasksToProcessQueue;
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void taskComplited(Task task, List<Map<String, Object>> results) {
		resultsHandler.hanldeResult(task.getShopName(), task.getCategory().getName(), results);
	}

	@Override
	public void taskSuspended(Task task) {
		if(task.isProcessingAvailable()){
			task.registerFailure();
			try {
				suspendedTasksQueue.put(task);
			} catch (InterruptedException e) {
				log.error("Unable add task to suspended tasks list.", e);
			}
		} else{
			taskFailed(task);
		}
	}

	@Override
	public void taskFailed(Task task) {
		failedTasks.add(task);
		resultsHandler.unregisterExpectedResult(task.getShopName(), task.getCategory().getName());
		log.error("Task rejected: "+ task.getShopName() + " - "+ task.getCategory().getName());
	}

	@Override
	public void createTasks(List<Category> categories, String shopName) {
		List<Task> tasksToAdd = new ArrayList<Task>();
		List<String> expectedResultKeys = new ArrayList<String>();
		for(Category category : categories){
			Task task = new Task(category);
			task.setShopName(shopName);
			tasksToAdd.add(task);
			expectedResultKeys.add(category.getName());
		}
		resultsHandler.registerExpectedResultsSet(shopName, expectedResultKeys);
		
		for(Task task : tasksToAdd){
			try {
				tasksToProcessQueue.put(task);
			} catch (InterruptedException e) {
				failedTasks.add(task);
				resultsHandler.unregisterExpectedResult(shopName, task.getCategory().getName());
				log.error("Unable to add task to the queue.");
			}
		}
	}

}
