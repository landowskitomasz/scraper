package com.tennizoom.scraper;

import java.util.List;
import java.util.Map;

import com.tennizoom.scraper.domain.Category;

public interface TasksStore {

	void taskComplited(Task task, List<Map<String, Object>> results);
	
	void taskSuspended(Task task);
	
	void taskFailed(Task task);
	
	void createTasks(List<Category> categories, String shopName);

	Task getTask() throws InterruptedException;
}
