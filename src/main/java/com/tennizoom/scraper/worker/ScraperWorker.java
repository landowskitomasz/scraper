package com.tennizoom.scraper.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.tennizoom.scraper.HtmlLoader;
import com.tennizoom.scraper.MapEntryConverter;
import com.tennizoom.scraper.domain.Category;
import com.thoughtworks.xstream.XStream;

public class ScraperWorker implements Runnable {
	
	private static Logger log = Logger.getLogger(ScraperWorker.class.getName());

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	
	private HtmlLoader loader;
	
	private String outputDirectory;
	
	private SynchronousQueue<Category> categoriesToProcess;
	
	public ScraperWorker(String outputDirectory, SynchronousQueue<Category> categoriesToProcess){
		this.loader = new HtmlLoader();
		this.outputDirectory = outputDirectory;
		this.categoriesToProcess = categoriesToProcess;
		log.info("Worker created.");
	}
	
	@Override
	public void run() {
		log.info("Worker started.");
		try{
			for(Category category = categoriesToProcess.poll(30, TimeUnit.SECONDS); category != null; category = categoriesToProcess.poll()){
				log.info("Categry to scrap found.");
				Document document = loader.loadCleanHtml(category.getUrl());
				Map<String, Object> data = category.findData(document);
				XStream magicApi = new XStream();
		        magicApi.registerConverter(new MapEntryConverter());
		        magicApi.alias("products", Map.class);
		        String xml = magicApi.toXML(data);
		        save(outputDirectory+category.getName()+"_"+dateFormat.format(new Date())+".xml", xml);
			}
		}
		catch(InterruptedException e){
			log.error("Exception while waiting for next category to scrapp.", e);
		}
		log.info("No categories to scrap, closing worker.");
	}
	
	private static void save(String fileName, String xml){
    	FileOutputStream fop = null;
		File file;
		try {
 
			file = new File(fileName);
			fop = new FileOutputStream(file);
 
			if (!file.exists()) {
				file.createNewFile();
			}
 
			byte[] contentInBytes = xml.getBytes();
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
			log.info(file.getAbsolutePath() + " saved.");
		} catch (IOException e) {
			log.error("Unable to save file.", e);
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				log.error("Unable to close file.", e);
			}
		}
    }

}
