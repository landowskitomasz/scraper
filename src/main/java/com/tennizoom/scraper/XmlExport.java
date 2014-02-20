package com.tennizoom.scraper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

public class XmlExport implements Runnable {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	
	private static Logger log = Logger.getLogger(XmlExport.class.getName());
	
	private String outputDirectory;
	
	private String name;
	
	private LinkedBlockingQueue<Map<String, Object>> resutsPipe = new LinkedBlockingQueue<Map<String, Object>>(30);
	
	public XmlExport(String outputDirectory, String name) {
		this.outputDirectory = outputDirectory;
		File outDir = new File(outputDirectory);
		if(!outDir.exists()){
			outDir.mkdirs();
		}
		this.name = name;
	}

	public LinkedBlockingQueue<Map<String, Object>> getResultPipe() {
		return resutsPipe;
	}

	@Override
	public void run() {
		XStream magicApi = new XStream();
	    magicApi.registerConverter(new MapEntryConverter());
	    magicApi.alias("product", Map.class);
	    FileOutputStream fop = null;
		File file;
		try {
			log.info("Opening output file.");
			
			file = new File(outputDirectory + name +"-"+dateFormat.format(new Date())+".xml");
			if(!file.exists()) {
				file.createNewFile();
			}
			fop = new FileOutputStream(file);
 
			if (!file.exists()) {
				file.createNewFile();
			}
	
			fop.write("<products>\n".getBytes());
			try{
				for(Map<String, Object> result = resutsPipe.poll(30, TimeUnit.SECONDS); result != null; result = resutsPipe.poll(30, TimeUnit.SECONDS)){
					String xml = magicApi.toXML(result);
					fop.write((xml+"\n").getBytes());
				}
			}catch (InterruptedException e){
				log.error("Unable to retrive result.", e);
			}
			
			fop.write("</products>".getBytes());
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
