package com.tennizoom.scraper.processor;

import java.util.List;

import com.tennizoom.scraper.config.ValueProcessor;

public class ValueProcessorHelper {

	public static String callProcessors(List<ValueProcessor> processors, String value){
		String result = value;
		for(ValueProcessor processor : processors){
			ValueProcessorExecutor valueProcessorExecutor;
			switch(processor.getProcessorType()){
				case regexClean:
					valueProcessorExecutor = new RegexCleanerValueProcessor(processor.getOptions());
					break;
				case replace:
					valueProcessorExecutor = new ReplaceValueProcessor(processor.getOptions());
					break;
				case htmlUnescape:
					valueProcessorExecutor = new HtmlUnescapeValueProcessor();
					break;
				case validate:
					valueProcessorExecutor = new ValidateValueProcessor(processor.getOptions());
					break;
				default:
					throw new IllegalStateException("Unknown processor: " +processor.getProcessorType());
			}
			
			result = valueProcessorExecutor.process(result);
		}
		
		return result;
	}
	
}
