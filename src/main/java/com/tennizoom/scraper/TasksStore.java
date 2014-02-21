package com.tennizoom.scraper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.tennizoom.scraper.domain.Category;

public interface TasksStore {

	void taskComplited(Task task, List<Map<String, Object>> results);
	
	void taskSuspended(Task task);
	
	void taskFailed(Task task);
	
	void createTasks(List<Category> categories, String shopName);

	BlockingQueue<Task> getTasks();
}
