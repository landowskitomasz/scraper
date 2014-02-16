package com.tennizoom.scraper;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class MapEntryConverter implements Converter {

    public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        return AbstractMap.class.isAssignableFrom(clazz);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

        AbstractMap<?, ?> map = (AbstractMap<?, ?>) value;
        for (Object obj : map.entrySet()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) obj;
            
            if(entry.getValue() instanceof List<?>){
            	for(Object listItem : (List<?>) entry.getValue()){
            		writer.startNode(entry.getKey().toString());
            		if(listItem instanceof Map<?, ?>){
            			marshal(listItem, writer, context);
            		}
                    writer.endNode();
            	}
            }
            else if(entry.getValue() instanceof Map<?, ?>){
            	writer.startNode(entry.getKey().toString());
            	marshal(entry.getValue(), writer, context);
                writer.endNode();
            }
            else{
            	writer.startNode(entry.getKey().toString());
            	writer.setValue(entry.getValue().toString());
                writer.endNode();
            }
        }

    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    	throw new UnsupportedOperationException();
    }

}
