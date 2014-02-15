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

import com.tennizoom.scraper.App;

@XmlRootElement(name="scraperConfiguration")
public class ScraperConfiguration {
	
	private static Logger log = Logger.getLogger(ScraperConfiguration.class.getName());
	
	private List<ScraperConfigurationEntry> configurationEntries = new ArrayList<ScraperConfigurationEntry>();

	public static ScraperConfiguration getInstance(String path){
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

			JAXBContext context = JAXBContext.newInstance(new Class[] {ScraperConfiguration.class});
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
	
	@XmlElement(name="scraperConfigurationEntry", nillable=false)
	public List<ScraperConfigurationEntry> getConfigurationEntries() {
		return configurationEntries;
	}

	public void setConfigurationEntries(List<ScraperConfigurationEntry> configurationEntries) {
		this.configurationEntries = configurationEntries;
	}
}
