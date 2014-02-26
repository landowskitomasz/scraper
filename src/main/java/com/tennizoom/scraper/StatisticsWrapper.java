package com.tennizoom.scraper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

public class StatisticsWrapper implements ResultsHandler{

	private static Logger log = Logger.getLogger(StatisticsWrapper.class.getName());

	private Map<String, List<Category>> statistics = new HashMap<String, List<Category>>();
	
	private ResultsHandler handler;
	
	public enum Status {
		SUCCESS, FAILED, IN_PROGRESS, TO_PERFORM
	}
	
	public StatisticsWrapper(ResultsHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void registerExpectedResultsSet(String name, List<String> keys) {
		
		List<Category> categories = new ArrayList<StatisticsWrapper.Category>();
		for(String key : keys){
			Category category = new Category();
			category.setName(key);
			categories.add(category);
		}
		
		statistics.put(name, categories);
		handler.registerExpectedResultsSet(name, keys);
	}

	@Override
	public void unregisterExpectedResult(String name, String key) {
		List<Category> categories = statistics.get(name);
		Category category = getByKey(key, categories);
		category.setEnd(System.currentTimeMillis());
		category.setStatus(Status.FAILED);
		handler.unregisterExpectedResult(name, key);
	}

	private Category getByKey(String key, List<Category> categories) {
		for(Category category: categories){
			if(category.getName().equals(key)){
				return category;
			}
		}
		throw new IllegalArgumentException("Category not registered.");

	}


	@Override
	public void processingStarted(String name, String key) {
		List<Category> categories = statistics.get(name);
		Category category = getByKey(key, categories);
		category.setStart(System.currentTimeMillis());
		category.setStatus(Status.IN_PROGRESS);
		handler.processingStarted(name, key);
	}
	
	@Override
	public void hanldeResult(String name, String key,
			List<Map<String, Object>> result) {

		handler.hanldeResult(name, key, result);
		List<Category> categories = statistics.get(name);
		Category category = getByKey(key, categories);
		category.setEnd(System.currentTimeMillis());
		category.setStatus(Status.SUCCESS);
		
		printIfAllFinished();
	}

	private void printIfAllFinished() {
		for(String name : statistics.keySet()){
			for(Category category : statistics.get(name)){
				if(category.getStatus() == Status.TO_PERFORM || category.getStatus() == Status.IN_PROGRESS){
					
					return;
				}
			}
		}
		StringBuilder builder = new StringBuilder("SCRAPER STATISTIC: ").append("\n");
		for(String name : statistics.keySet()){
			builder.append("Shop: " ).append(name).append(":\n");
			int succeed = 0;
			int failed = 0;
			for(Category category : statistics.get(name)){
				builder.append("\t").append(category.getName()).append(" ");
				for(int i = (category.getName().length() + category.getStatus().name().length()); i < 60; ++i){
					builder.append('.');
				}
				builder.append(" ").append(category.getStatus().name()).append(" ")
				.append(((category.getEnd() - category.getStart())/ 1000.0)).append(" s\n");
				if(category.getStatus() == Status.SUCCESS){
					++succeed;
				} else {
					++failed;
				}
			}
			builder.append("Processed ").append(succeed + failed).append(" categories: ")
			.append(succeed).append(" succeed, ").append(failed).append(" failed.\n");
		}
		log.info(builder.toString());
	}

	private class Category{

		private Status status = Status.TO_PERFORM;
		
		private long start;
		
		private long end;
		
		private String name;

		public Status getStatus() {
			return status;
		}

		public void setStatus(Status status) {
			this.status = status;
		}

		public long getStart() {
			return start;
		}

		public void setStart(long start) {
			this.start = start;
		}

		public long getEnd() {
			return end;
		}

		public void setEnd(long end) {
			this.end = end;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
}
