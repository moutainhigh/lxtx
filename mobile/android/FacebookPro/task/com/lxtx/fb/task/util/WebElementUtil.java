package com.lxtx.fb.task.util;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WebElementUtil {

	public static void print(WebElement ele){
		
		printStart(ele);
		try{
			List<WebElement> eles = ele.findElements(By.xpath("./*"));
		
			if(eles != null && eles.size() > 0){
				for(WebElement subEle : eles){
					print(subEle);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		printEnd(ele);
	}
	
	private static void printStart(WebElement ele){
		System.out.println("<"+ele.getTagName()+">");
System.out.println(getContent(ele));
		
	}
	
	private static String getContent(WebElement ele){
		String tagName = ele.getTagName();
		
		if("input".equals(tagName)){
			String type = ele.getAttribute("type");

			if("text,hidden".contains(type)){
				return ele.getAttribute("value");
			}
		}
		
		return ele.getText();
	}
	private static void printEnd(WebElement ele){
		System.out.println("</"+ele.getTagName()+">");
	}
}
