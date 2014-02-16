package com.tennizoom.scraper.processor;

import org.apache.commons.lang.StringEscapeUtils;

public class HtmlUnescapeValueProcessor implements ValueProcessorExecutor {

	@Override
	public String process(String value) {
		return StringEscapeUtils.unescapeHtml(value);
	}

}
