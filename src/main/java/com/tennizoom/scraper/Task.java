package com.tennizoom.scraper;

import com.tennizoom.scraper.domain.Category;

public class Task {
	
	//private static Logger log = Logger.getLogger(Task.class.getName());
	
	private String shopName;
	
	private Category category;
	
	//private LinkedBlockingQueue<Map<String, Object>> resutsPipe;
	
	private int failureLimit = 3;
	
	public Task(Category category/*, LinkedBlockingQueue<Map<String, Object>> resutsPipe*/){
		this.category = category;
		//this.resutsPipe = resutsPipe;
	}
	
	public Category getCategory(){
		return this.category;
	}
	
	//public void putResult(Map<String, Object> result){
		//try {
		//	resutsPipe.put(result);
		//} catch (InterruptedException e) {
		//	log.error("Can't put result to the pipe.", e);
		//}
	//}
	
	public boolean isProcessingAvailable(){
		return failureLimit > 0;
	}
	
	public void registerFailure(){
		--failureLimit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		return true;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	
}
