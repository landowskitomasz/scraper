package com.tennizoom.scraper.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.tennizoom.scraper.config.ValueProcessorConfig;

public class DefaultValueProcessors {

	private List<DefaultValueProcessorField> fields = new ArrayList<DefaultValueProcessorField>();

	@XmlElement(name="field", nillable=false)
	public List<DefaultValueProcessorField> getFields() {
		return fields ;
	}

	public void setFields(List<DefaultValueProcessorField> fields) {
		this.fields = fields;
	}

	public List<ValueProcessorConfig> getFieldValueProcessors(String name) {
		for(DefaultValueProcessorField field : fields){
			if(field.getName().equals(name)){
				return field.getValueProcessors();
			}
		}
		return new ArrayList<ValueProcessorConfig>();
	}
}
