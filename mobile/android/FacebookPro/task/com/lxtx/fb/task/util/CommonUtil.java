package com.lxtx.fb.task.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.lxtx.fb.pojo.Admin;

public class CommonUtil {
	
	private static boolean debug = true;
	
	public static void main(String[] args){
//		List<File> list = files(new File("C:\\Users\\thinkpad\\Desktop\\海外投放\\素材\\西班牙1\\pp\\"), 8);
//		
//		for(File file : list){
//			System.out.println(file.getName());
//		}
		
//		String splits = "aa||bb||cc||dd||ee";
//		
//		List<String> list = randoms(splits, 8);
//		
//		for(String split : list){
//			System.out.println(split);
//		}
		
		System.out.println(getCodeWithPixel("1678814232131724"));
	}

	private static final String sss = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int LEN = sss.length();
	
	public static String identifyCheckCode(WebDriver driver, By by) throws Exception{
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		WebElement element = driver.findElement(By.className("checkCodeImg"));
		Point p = element.getLocation();
		int width = element.getSize().getWidth();
		int higth = element.getSize().getHeight();
//		Rectangle rect = new Rectangle(width, higth);
		BufferedImage img = ImageIO.read(scrFile);
		BufferedImage dest = img.getSubimage(p.getX(), p.getY(), width, higth);
		ImageIO.write(dest, "png", scrFile);
		Thread.sleep(1);
		File fng = new File("c:/validcode/"+randomStr(8)+".png");
		if(fng.exists()){
		    fng.delete();
		}

		FileUtils.copyFile(scrFile, fng);//org.apache.commons.io.FileUtils
		byte[] data = getByteArrFromStream(new FileInputStream(fng));
		
		return RuoKuaiUtil.getValidCodeByImageData(data, "2040");
	}
	
	private static byte[] getByteArrFromStream(InputStream is) {
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        try {
	            byte[] buff = new byte[100];
	            int bytesRead = 0;
	            while((bytesRead = is.read(buff)) > 0) {
	                outputStream.write(buff, 0, bytesRead);
	            }
	            byte[] allBytes = outputStream.toByteArray();
	            is.close();
	            outputStream.close();
	            return allBytes;
	        } catch (IOException e) {
	        } finally {
	            try {
	                is.close();
	                outputStream.close();
	            } catch (IOException e) {
	            }
	        }
	        return null;
	  }
	
	public static String randomStr(int randomNum){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < randomNum; i ++){
			int idx = new Random().nextInt(LEN);
			sb.append(sss.substring(idx, idx+1));
		}
		return sb.toString();
	}
	
	public static List<String> randoms(String splits, int num){
		
		List<String> list = new ArrayList<String>();
		String[] arr = splits.split("\\|\\|");
		
		if(arr.length <= num){
			for(String s : arr){
				list.add(s);
			}
			
			if(arr.length < num){
				list.addAll(randoms(splits, num - arr.length));
			}
		}else{
			List<String> list1 = new ArrayList<String>();
			
			for(String s : arr){
				list1.add(s);
			}
			
			for(int i = 0 ; i < num ; i ++){
				int index = new Random().nextInt(list1.size());
				
				list.add(list1.remove(index));
			}
		}
		
		return list;
	}
	
	public static List<File> files(File dir, int num){
		
		if(dir.exists() && dir.isDirectory()){
			File[] files = dir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File file) {
					return !file.isDirectory();
				}
			});
			List<File> list = new ArrayList<File>();
			
			if(files.length <= num){
				for(File file : files){
					list.add(file);
				}
				
				if(files.length < num){
					list.addAll(files(dir, num - files.length));
				}
			}else{
				List<File> list1 = new ArrayList<File>();
				for(File file : files){
					list1.add(file);
				}
				for(int i = 0 ; i < num ; i ++){
					int random = new Random().nextInt(list1.size());
					
					list.add(list1.remove(random));
				}
			}
			
			return list;
		}
		
		return null;
	}
	
	public static void debugErr(Exception e) throws Exception{
		e.printStackTrace();
		sendSms();
		System.out.println("wait deal...");
		
		if(!debug) {
			throw e;
		}
	}
	
	public static WebElement scrollAndMoveToElement(WebDriver driver,By by) {
        WebElement ele = driver.findElement(by);
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        // roll down and keep the element to the center of browser
        js.executeScript("arguments[0].scrollIntoViewIfNeeded(true);", ele);
        
        Actions action = new Actions(driver);
		action.moveToElement(ele).perform();
		sleep(500+new Random().nextInt(500));
		
		
        
        return ele;
    }
	
	public static void randomSleep(long seconds){
		try {
			Thread.sleep(seconds + new Random().nextInt((int)seconds));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void sleep(long seconds){
		try {
			Thread.sleep(seconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static Admin admin = new Admin();
	
	public static void initAdmin(Admin admin){
		CommonUtil.admin = admin;
	}
	
	/**
	 * 需要补充
	 */
	private static void sendSms() {
		System.out.println("error occur");
		try{
			SMSSender253.batchSend(admin.getPhone());
//			http://m.ninestate.com.cn/index.php/mobile/async/sendSMSMsg?mobiles=18310135821&content=hello123&secret=beijing&time=1212312312
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//1678814232131724
	public static String getCodeWithPixel(String pixel){
		return "fb"+Long.toHexString(Long.parseLong(pixel));
	}
	
	public static int getDay(int off){
		
		Calendar cal = Calendar.getInstance();
		
		cal.add(Calendar.DAY_OF_MONTH, off);
		
		return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
	}
	
	public static String getIpSection(String ip){
		int pos = ip.lastIndexOf(".");
		
		return ip.substring(0, pos);
	}
	
	public static void changeTimeZone(String country) {
		
		String timeZone = getTimeZone(country);
		
		try {
			Process process = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/C", "TZUTIL /s \"" + timeZone + "\"" });
			
//			if(process != null) {
//				process.destroy();
//				process = null;
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static String getTimeZone(String country){
		
		switch(country){
		case "France"://paris
			return "Romance Standard Time";
		
		case "England"://london
			return "GMT Standard Time";
		
		case "Germany"://berlin
			return "W. Europe Standard Time";
			
		case "Belgium"://brussels
			return "Romance Standard Time";
		
		case "Australia"://sydney
			return "AUS Eastern Standard Time";
		}
		
		return "GMT Standard Time";
	}

}
