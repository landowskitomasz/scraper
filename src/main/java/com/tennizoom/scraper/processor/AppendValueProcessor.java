package com.tennizoom.scraper.processor;

import java.util.List;

import com.tennizoom.scraper.config.ValueProcessorOption;

public class AppendValueProcessor implements ValueProcessorExecutor{

	private static final String SUFFIX_DIRECTION = "suffix";
	
	private static final String PREFIX_DIRECTION = "prefix";
	
	private static final String DIRECTION_OPTION = "direction";
	
	private static final String VALUE_OPTION = "value";
	
	private String direction = PREFIX_DIRECTION;
	
	private String appendix;
	
	public AppendValueProcessor(List<ValueProcessorOption> options) {
		
		for(ValueProcessorOption option : options){
			if(VALUE_OPTION.equals(option.getName())){
				appendix = option.getValue();
			}
			else if(DIRECTION_OPTION.equals(option.getName())){
				direction = option.getValue();
				if(!PREFIX_DIRECTION.equals(direction) && !SUFFIX_DIRECTION.equals(direction)){
					throw new IllegalStateException("Append value processor direction option can have 'prefix' or 'surffix' values only. Default value is 'prefix'.");
				}
			}
		}
		if(appendix == null){
			throw new IllegalArgumentException("Append value processor requires 'value' option.");
		}
	}

	@Override
	public String process(String value) {
		if(PREFIX_DIRECTION.equals(direction)){
			return appendix + value;
		}
		else{
			return value + appendix;
		}
	}
}
