package com.tennizoom.scraper.processor;

import java.util.List;

import com.tennizoom.scraper.config.ValueProcessorOption;

public class ReplaceValueProcessor implements ValueProcessorExecutor{
	
	private static final String PATTERN_OPTION = "pattern";

	private static final String REPLACEMENT_OPTION = "replacement";

	private String pattern;
	
	private String replacement;
	
	public ReplaceValueProcessor(List<ValueProcessorOption> options){
		for(ValueProcessorOption option : options){
			if(ReplaceValueProcessor.PATTERN_OPTION.equals(option.getName())){
				pattern = option.getValue();
			}
			else if(ReplaceValueProcessor.REPLACEMENT_OPTION.equals(option.getName())){
				replacement = option.getValue();
			}
		}
		if(pattern == null){
			throw new IllegalArgumentException("Processor requires pattern value.");
		}
		if(replacement == null){
			throw new IllegalArgumentException("Processor requires replacement value.");
		}
	}

	@Override
	public String process(String value) {
		if(value == null){
			return value;
		}
		
		return replaceAll(value, pattern, replacement);
	}
	
	private String replaceAll(String source, String pattern, String replacement) {
        
        StringBuffer sb = new StringBuffer();
        int index;
        int patIndex = 0;
        while ((index = source.indexOf(pattern, patIndex)) != -1) {
            sb.append(source.substring(patIndex, index));
            sb.append(replacement);
            patIndex = index + pattern.length();
        }
        sb.append(source.substring(patIndex));
        return sb.toString();
    }

}
