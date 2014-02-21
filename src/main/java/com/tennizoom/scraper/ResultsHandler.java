package com.tennizoom.scraper;

import java.util.List;
import java.util.Map;

public interface ResultsHandler {

	void registerExpectedResultsSet(String name, List<String> keys);
	
	void unregisterExpectedResult(String name, String key);
	
	void hanldeResult(String name, String key, List<Map<String, Object>> result);
}
