package com.tennizoom.scraper.config;

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

@XmlRootElement(name="scraperConfiguration")
public class ScraperConfiguration {
	
	private static Logger log = Logger.getLogger(ScraperConfiguration.class.getName());
	
	private List<ShopConfig> shops = new ArrayList<ShopConfig>();
	
	private String outputDirectory;
	
	private int threadsNumber = 1;

	public static ScraperConfiguration getInstance(String path){
		InputStream is = null;
		try {
			is = new FileInputStream(path);
			
			Reader configReader = new InputStreamReader(is); 

			JAXBContext context = JAXBContext.newInstance(new Class[] {ScraperConfiguration.class, ShopConfig.class});
			Unmarshaller unmarshaller = context.createUnmarshaller();

			ScraperConfiguration xPathFilterConfiguration  = (ScraperConfiguration) unmarshaller.unmarshal(configReader);
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
	
	@XmlElement(name="shopConfiguration", nillable=false)
	public List<ShopConfig> getShops() {
		return shops;
	}

	public void setShops(List<ShopConfig> shops) {
		this.shops = shops;
	}
	
	@XmlElement(name="outputDirectory")
	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	@XmlElement(name="threadsNumber")
	public int getThreadsNumber() {
		return threadsNumber;
	}

	public void setThreadsNumber(int threadsNumber) {
		this.threadsNumber = threadsNumber;
	}
}
