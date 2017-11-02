package com.lxtx.fb.task.util;

import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class OpJsClick implements OpFunc{

	public WebDriver driver;
	
	public By by;
	
	public long sleep;
	
	public OpJsClick(){
		
	}
	
	public OpJsClick(WebDriver driver, By by, long sleep){
		this.driver = driver;
		this.by = by;
		this.sleep = sleep;
	}
	
	@Override
	public void op() throws Exception {
		try{
			WebElement ele = driver.findElement(by);
			
			JavascriptExecutor js = (JavascriptExecutor) driver;
	        // roll down and keep the element to the center of browser
			//scrollIntoView
	        js.executeScript("arguments[0].scrollIntoViewIfNeeded(true);", ele);
			
	        Actions action = new Actions(driver);
			action.moveToElement(ele).perform();
			CommonUtil.sleep(500+new Random().nextInt(500));
			
			JavascriptExecutor exec = (JavascriptExecutor) driver;
			exec.executeScript("arguments[0].click()", ele);
		}catch(Exception e){
			CommonUtil.debugErr(e);
		}
	}

}
