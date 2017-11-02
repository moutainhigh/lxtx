package com.lxtx.fb.task.util;

import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class OpClick implements OpFunc{

	public WebDriver driver;
	
	public By by;
	
	public OpClick(){
		
	}
	
	public OpClick(WebDriver driver, By by){
		this.driver = driver;
		this.by = by;
	}
	
	@Override
	public void op() throws Exception {
		try{
			WebElement ele = CommonUtil.scrollAndMoveToElement(driver, by);
			
			ele.click();
		}catch(Exception e){
			CommonUtil.debugErr(e);
		}
	}

}
