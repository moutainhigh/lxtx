package com.lxtx.fb.task.util;

import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public class OpSelect implements OpFunc{

	public WebDriver driver;
	
	public By by;
	
	public int type;
	
	public String value;
	
	public OpSelect(){
		
	}
	
	public OpSelect(WebDriver driver, By by, int type, String value){
		this.driver = driver;
		this.by = by;
		this.type = type;
		this.value = value;
	}
	
	@Override
	public void op() throws Exception {
		WebElement ele = CommonUtil.scrollAndMoveToElement(driver, by);
		
		Select select = new Select(ele);
		
		switch(type){
		case 0://index
			select.selectByIndex(Integer.parseInt(value));
			break;
		case 1:
			select.selectByValue(value);
			break;
		case 2:
			select.selectByVisibleText(value);
			break;
		}
	}	
}
