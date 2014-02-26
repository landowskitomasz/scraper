package com.tennizoom.scraper.worker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.firefox.FirefoxDriver;
import org.w3c.dom.Document;

import com.tennizoom.scraper.exception.HtmlLoaderException;


public class HtmlLoader {
	

	//WebDriver driver = new FirefoxDriver();
	
	private static Logger log = Logger.getLogger(HtmlLoader.class.getName());
	
	public Document loadCleanHtml(String urlString) throws HtmlLoaderException {
		log.info("Loading html from: " + urlString);
		try {
			
		    //driver.get("http://nolags.pl/browse.php?u=c9FEDF%2Bmgrz1nz2NufwNI17beeuZn0E7XvRzBe48P%2BAa7T%2BnWyrgcUUBeJl4Vm1o7j%2FqOaW0vP99uAcNC0Xv7bD04z4zBlje%2B6dlcCgJdt8W6GnQzsqG8iLdqW%2F%2BwqCvSy%2FP4%2FRiM560iT3a%2BJwdMuBcImg03R3K8A%3D%3D&b=29");
		  //  WebDriverWait wait = new WebDriverWait(driver, 3000);
		 //   wait.until(ExpectedConditions.visibilityOfElementLocated((By.id("Email"))));

		  //  WebElement nexPage = driver.findElement(By.xpath("/html/body/form[@id='form1']/div[3]/div[@id='MainDiv']/div[@id='MainContentHolderDiv']/div[@id='SectionHolderDiv']/span[@id='SectionCentreNav1']/span/div[3]/a[last()-1]"));
		  //  WebElement userid = driver.findElement(By.id("Email"));
		 //   WebElement password = driver.findElement(By.id("Passwd"));
		  //  WebElement loginButton = driver.findElement(By.id("signIn")); 
		  //  userid.sendKeys("landowskitomasz@gmail.com");
		 //   password.sendKeys("j@n!ew!em");
		    //saveDocumentToFile(driver.getPageSource());
		    //nexPage.click();
			
			CleanerProperties props = new CleanerProperties();
			props.setAllowHtmlInsideAttributes(true);
	        props.setAllowMultiWordAttributes(true);
	        props.setRecognizeUnicodeChars(true);
	        props.setOmitComments(true);
	        props.setNamespacesAware(false);
	        
			TagNode tagNode = new HtmlCleaner(props).clean(
			    new URL(urlString)
			);
		
			Document document = new DomSerializer(props, true).createDOM(tagNode);
			
			log.info("Html loaded succesfully.");
			return document;
		} catch (IOException e) {
			throw new HtmlLoaderException("Unable to load html page.",e);
		} catch (ParserConfigurationException e) {
			throw new HtmlLoaderException("Unable to load html page.",e);
		}
	}
	
	private void saveDocumentToFile(String document) {
		
		try {
			
			File file = new File("selenium.html");
			if(!file.exists()){
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file);
			
			fw.write(document);
			fw.close();
		} catch (IOException e) {

		}
	}

}
