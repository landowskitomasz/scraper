package com.tennizoom.scraper;

import java.util.Map;

public interface Export {

	void open(String name);

	void write(Map<String, Object> record);
	
	void close();
}
