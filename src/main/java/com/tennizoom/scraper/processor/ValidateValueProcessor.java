package com.tennizoom.scraper.processor;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.tennizoom.scraper.ValidateException;
import com.tennizoom.scraper.config.ValueProcessorOption;

public class ValidateValueProcessor  implements ValueProcessorExecutor{

	private String regex;
	
	public ValidateValueProcessor(List<ValueProcessorOption> options) {
		
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
	@Override
	public String process(String value) {
		if(StringUtils.isEmpty(value)){
			return value;
		}
		if(!value.matches(regex)){
			throw new ValidateException("Value '"+value+"' is invalid.");
		}
		return value;
	}

}
