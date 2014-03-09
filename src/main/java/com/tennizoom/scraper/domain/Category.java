package com.tennizoom.scraper.domain;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class Category implements Cloneable {

	private static Logger log = Logger.getLogger(Category.class.getName());

	private String name;
	
	private String url;
	
	private String saveDirectory;
	
	private List<DataEntry> dataEntries = new ArrayList<DataEntry>();
	
	private Pagination pagination;

	private DefaultValueProcessors defaultValueProcessors = new DefaultValueProcessors();

	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name="url", required=true)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@XmlAttribute(name="saveTo", required=false)
	public String getSaveDirectory() {
		return saveDirectory;
	}

	public void setSaveDirectory(String saveDirectory) {
		this.saveDirectory = saveDirectory;
	}
	
	@XmlElement(name="dataEntry", nillable=false)
	public List<DataEntry> getDataEntries() {
		return dataEntries;
	}

	public void setDataEntries(List<DataEntry> data) {
		this.dataEntries = data;
	}

	public Map<String, Object> findData(Document document) {
		if(!StringUtils.isEmpty(getSaveDirectory())){
			saveDocumentToFile(document);
		}
		Map<String, Object> entries = new HashMap<String, Object>();
		for(DataEntry entry : getDataEntries()){
			log.info("Looking for '"+ entry.getName() + "' entry values.");
			List<Map<String, Object>> entryValues = entry.findData(document, defaultValueProcessors);
			entries.put(entry.getName(), entryValues);
			if(entryValues.size() == 0){
				log.warn("Not found any " + entry.getName() + " data in category "+ getName() +" !!!");
			}
		}
		
		return entries;
	}

	private void saveDocumentToFile(Document document) {
		File directory = new File(getSaveDirectory());
		if(!directory.exists()){
			directory.mkdirs();
		}
		
		OutputFormat format = new OutputFormat(document); 
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);
		Writer out = new StringWriter();
		XMLSerializer serializer = new XMLSerializer(out, format);

		try {
			serializer.serialize(document);

			File file = new File(directory.getAbsolutePath()+ "/" + getName()+ ".html");
			if(!file.exists()){
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file);
			
			fw.write(out.toString());
			fw.close();
		} catch (IOException e) {

		}
	}
	
	@XmlElement(name="pagination", nillable=true)
	public Pagination getPagination() {
		return pagination;
	}

	public void setPagination(Pagination pagination) {
		this.pagination = pagination;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	public void setDefaultValueProcessors(
			DefaultValueProcessors defaultValueProcessors) {
		this.defaultValueProcessors = defaultValueProcessors;		
	}
	
	public DefaultValueProcessors getDefaultValueProcessors(){
		return defaultValueProcessors;
	}
	
	
}
