package com.tennizoom.scraper.domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.tennizoom.scraper.App;

@XmlRootElement(name="shop")
public class Shop {
	
	private static Logger log = Logger.getLogger(Shop.class.getName());
	
	public static Shop getInstance(String path){
		InputStream is = null;
		try {
			if(App.DEBUG){
				ClassLoader classloader = Thread.currentThread().getContextClassLoader();
				is = classloader.getResourceAsStream(path);
			}
			else{
				is = new FileInputStream(path);
			}
			Reader configReader = new InputStreamReader(is); 

			JAXBContext context = JAXBContext.newInstance(new Class[] {Shop.class, Category.class, DataEntry.class, DataField.class});
			Unmarshaller unmarshaller = context.createUnmarshaller();

			Shop xPathFilterConfiguration  = (Shop) unmarshaller.unmarshal(configReader);
			return xPathFilterConfiguration;

		} catch (JAXBException e) {
			log.error("Unable to load configuration.", e);
		} catch (FileNotFoundException e) {
			log.error("Unable to load configuration.", e);
		}
		finally{
			IOUtils.closeQuietly(is);
		}
		return null;
	}
	
	private List<Category> categories = new ArrayList<Category>();
	
	@XmlElement(name="category", nillable=true)
	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
}
