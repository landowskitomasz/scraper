package com.tennizoom.scraper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

public class ResultsHandlerImpl implements ResultsHandler{
	
	private static Logger log = Logger.getLogger(ResultsHandlerImpl.class.getName());
	
	private String outputDirectory;

	public ResultsHandlerImpl(String outputDirectory){
		this.outputDirectory = outputDirectory;
	}

	private Map<String, ExportWrapper> exporters = new HashMap<String, ResultsHandlerImpl.ExportWrapper>();
	
	@Override
	public void registerExpectedResultsSet(String name, List<String> keys) {
		ExportWrapper exporter = new ExportWrapper(new XmlExport(outputDirectory, name));
		for(String key : keys){
			exporter.add(key);
		}
		exporters.put(name, exporter);
	}

	@Override
	public void unregisterExpectedResult(String name, String key) {
		ExportWrapper exporter = exporters.get(name);
		exporter.remove(key);
		log.warn("Waiting for results form "+ name + " -" + key +" disabled.");
	}

	private Semaphore writeAccessSemaphore = new Semaphore(1);
	
	@Override
	public void hanldeResult(String name, String key, List<Map<String, Object>> result) {
		try {
			writeAccessSemaphore.acquire();
		
			ExportWrapper exporter = exporters.get(name);
			if(!exporter.isInited()){
				exporter.open(name);
			}
			exporter.remove(key);
			for(Map<String, Object> record : result){
				exporter.write(record);
			}
			if(exporter.allResultsCompleted()){
				exporter.close();
			}
			writeAccessSemaphore.release();
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private class ExportWrapper implements Export{
		
		ExportWrapper(XmlExport export){
			this.export = export;
		}
		
		private boolean exportInited = false;

		private XmlExport export;
		
		private List<String> keys = new ArrayList<String>();
		
	    void add(String key){
			keys.add(key);
		}
		
		void remove(String category){
			keys.remove(category);
		}
		
		
		boolean isInited(){
			return exportInited;
		}
		
		boolean allResultsCompleted(){
			return keys.isEmpty();
		}

		@Override
		public void open(String name) {
			exportInited = true;
			Thread thread = new Thread(export);
			thread.setName(name + " file writer.");
			thread.start();
		}

		@Override
		public void write(Map<String, Object> record) {
			if(record == null){
				return;
			}
			try {
				export.getResultPipe().put(record);
			} catch (InterruptedException e) {
				log.error("Unable to write result record.", e);
			}
		}

		@Override
		public void close() {
			export.setAllResultsCompleted();
		}
	}

	@Override
	public void processingStarted(String name, String key) {
		// TODO Auto-generated method stub
		
	}

}
