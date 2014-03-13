package com.tennizoom.scraper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class Md5IdGenerationHandlerWraper implements ResultsHandler {
	
	private static Logger log = Logger.getLogger(Md5IdGenerationHandlerWraper.class.getName());
	
	private ResultsHandler handler;

	public Md5IdGenerationHandlerWraper(ResultsHandler handler){
		this.handler = handler;
	}

	@Override
	public void registerExpectedResultsSet(String name, List<String> keys) {
		handler.registerExpectedResultsSet(name, keys);
	}

	@Override
	public void unregisterExpectedResult(String name, String key) {
		handler.unregisterExpectedResult(name, key);
	}

	@Override
	public void hanldeResult(String name, String key,
			List<Map<String, Object>> result) {
		for(Map<String, Object> resultEntry : result){
			if(resultEntry.containsKey("name")){
				String uniquesPhrase = name + resultEntry.get("name");
				String md5Hash = hashMd5(uniquesPhrase);
				resultEntry.put("id", md5Hash);
			}
		}
		handler.hanldeResult(name, key, result);
	}

	private String hashMd5(String uniquesPhrase) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
		
	        md.update(uniquesPhrase.getBytes());
	 
	        byte byteData[] = md.digest();
	 
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
 
        return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error("Unable to hash product name with md5.", e);
			return null;
		}
	}

	@Override
	public void processingStarted(String name, String key) {
		handler.processingStarted(name, key);
	}

}
