package com.tennizoom.scraper.processor;

import java.util.List;

import com.tennizoom.scraper.config.ValueProcessorOption;

public class RegexCleanerValueProcessor implements ValueProcessorExecutor {

	private String regex;
	
	public RegexCleanerValueProcessor(List<ValueProcessorOption> options) {
		
		for(ValueProcessorOption option : options){
			if(RegexCleanerValueProcessor.REGEX_OPTION.equals(option.getName())){
				regex = option.getValue();
				break;
			}
		}
		if(regex == null){
			throw new IllegalArgumentException("Regex cleaner requires regex option with not null value.");
		}
	}

	public static final String REGEX_OPTION = "regex";

	@Override
	public String process(String value) {
		if(value == null){
			return value;
		}
		
		return value.replaceAll(regex, "");
	}

	
}
