package com.lxtx.fb.task.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class OpSendKeys implements OpFunc{

	public WebDriver driver;
	
	public By by;
	
	public String data;
	
	private boolean quick = false;
	
	public OpSendKeys(){
		
	}
	
	public OpSendKeys(WebDriver driver, By by, String data){
		this.driver = driver;
		this.by = by;
		this.data = data;
	}
	
	public OpSendKeys(WebDriver driver, By by, String data, boolean quick){
		this(driver, by, data);
		this.quick = quick;
	}

	@Override
	public void op() throws Exception {
		try{
			WebElement ele = CommonUtil.scrollAndMoveToElement(driver, by);
			
			String tag = ele.getTagName();
			
			if((tag.equals("input") && "text".equals(ele.getAttribute("type"))) || tag.equals("textarea")){
				ele.click();
				ele.clear();
			}

//			if(quick && (tag.equals("input") && "text".equals(ele.getAttribute("type")) || tag.equals("textarea"))){
//				JavascriptExecutor js = (JavascriptExecutor) driver;
//				
//				if(tag.equals("textarea")){
//					js.executeScript("arguments[0].innerHTML = \"" + data + "\"", ele);
//				}else{
//					js.executeScript("arguments[0].value=\""+data+"\"", ele);
//				}
//			}else 
			if(tag.equals("input") && "file".equals(ele.getAttribute("type"))) {
				ele.sendKeys(data);
			}else {
				int baseSleep = 300;
				if(quick){
					baseSleep = 100;
				}
				for(int i = 0; i < data.length(); i ++){
					ele.sendKeys(data.substring(i,i+1));
					CommonUtil.randomSleep(baseSleep);
				}
			}
		}catch(Exception e){
			CommonUtil.debugErr(e);
		}
	}
	
	
}
