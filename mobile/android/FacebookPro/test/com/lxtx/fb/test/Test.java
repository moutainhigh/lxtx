package com.lxtx.fb.test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.os.WindowsUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.lxtx.fb.task.util.CommonUtil;
import com.lxtx.fb.task.util.OpData;
import com.lxtx.fb.task.util.OpDataUtil;
import com.lxtx.fb.task.util.OpSendKeys;
import com.lxtx.fb.task.util.RobotUtil;
import com.lxtx.fb.task.util.WebElementUtil;

public class Test{

	private static ChromeDriver  driver = null;
	
	public static void main(String[] args) throws Exception{
		
		init();
		
//		driver.get("http://www.cyjd1300.com:9020/pay/manage/test.jsp");
		
//		testIframe();
		
//		testMouse1();
		
		testEles();
		
//		testTextarea();
	}
	
	private static void testTextarea() throws Exception{
		
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\textarea.html");
		String data = "test";
//		WebElement ele = driver.findElement(By.id("textarea"));
//		
//		JavascriptExecutor js = (JavascriptExecutor) driver;
//		
//		js.executeScript("arguments[0].innerHTML = \"" + data + "\"", ele);
		
		OpDataUtil.op(new OpData("", 0, new OpSendKeys(driver, By.id("textarea"), data, true), 1000, ""));
	}
	
	private static void testEles() throws Exception{
		
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\mouse1.html");
		
//		List<WebElement> eles = driver.findElements(By.xpath("//button/div/div[text()='Continue']"));
		List<WebElement> eles = driver.findElements(By.name("test"));
		
		System.out.println(eles.size());
	}
	
	private static void testMouse1() throws Exception{
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\mouse1.html");
		
		WebElement div1 = driver.findElement(By.id("div2"));
		
		Point point = div1.getLocation();
		
		System.out.println("x:"+point.x+","+point.getX()+" ; y:"+point.y+","+point.getY());
		
		Dimension size = div1.getSize();
		System.out.println(size.width+","+size.height);
		
//		Rectangle rect = div1.getRect();		
//		System.out.println(rect.x+","+rect.y+";"+rect.width+","+rect.height);
		
		Thread.sleep(5000);
		
		Actions action = new Actions(driver);
		JavascriptExecutor js = (JavascriptExecutor) driver;
        // roll down and keep the element to the center of browser
        long x = (Long)js.executeScript("return window.screenLeft?window.screenLeft: window.screenX");
        long x1 = (Long)js.executeScript("return document.body.scrollLeft");
//        driver.switchTo().alert().accept();
//        Thread.sleep(1000);
		long y = (Long)js.executeScript("return window.screenTop?window.screenTop: window.screenY");
		long y1 = (Long)js.executeScript("return document.body.scrollTop");
		
		System.out.println("x:"+x+","+x1+";y:"+y+","+y1);
		
		
		
		
////		driver.switchTo().alert().accept();
		Point[] p = new Point[]{
			new Point(10, 10),
			new Point(10, 110),
			new Point(10, 210),
			new Point(30, 30),
			new Point(30, 130),
			new Point(30, 230),
			new Point(50, 50),
			new Point(50, 150),
			new Point(50, 250),
		};

		for(int i = 0; i < p.length ; i ++){
//			RobotUtil.move(null, new Point((int)(1.25*p[i].x+1),(int)(1.25*p[i].y+132)));
//			action.moveByOffset(p[i].x, p[i].y);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
////		action.perform();
		
	}
	
	private static void testMouse(){
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\mouse.html");
		
		CommonUtil.sleep(2000);
		
		By by_a = By.id("a");

		By by_b = By.id("b");
		
		By by_c = By.id("c");
		
		By by_d = By.id("d");
		
		for(int i = 0; i < 4;  i++){
//			CommonUtil.click(driver, by_a);
			driver.findElement(by_a).click();
			
			CommonUtil.sleep(1000);
		
//			CommonUtil.sendKeys(driver, by_d, "abc");
			driver.findElement(by_d).sendKeys("abc");
			
			CommonUtil.sleep(1000);
			
//			CommonUtil.click(driver, by_b);
			driver.findElement(by_b).click();
			
			CommonUtil.sleep(1000);
			
//			CommonUtil.sendKeys(driver, by_c, "efg");
			driver.findElement(by_c).sendKeys("efg");

			CommonUtil.sleep(1000);
			
		}
		
		
	}
	
	private static void init(){
		System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");

		WindowsUtils.killByName("chromeDriver.exe");
		
		ChromeOptions options = new ChromeOptions();

		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized"));
		options.addArguments("--test-type", "--start-maximized", "--lang=en-US", "--user-agent=Mozilla/5.0 (iPod; U; CPU iPhone OS 2_1 like Mac OS X; ja-jp) AppleWebKit/525.18.1 (KHTML, like Gecko) Version/3.1.1 Mobile/5F137 Safari/525.20");
		
		
		driver = new ChromeDriver(options);
		
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);//寻找元素等待时间上限

	}
	
	private static void test11() throws Exception{
	
		int i = 12;
		
		System.out.println("start : "+ i);
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\ad_"+i+".html");
		
		String pixelStr = driver.findElement(By.xpath("//div[substring(text(),string-length(text()) - 7) = \"'s Pixel\"]/following-sibling::div[1]")).getText();
		
		System.out.println(pixelStr);
		
		int pos = pixelStr.indexOf("ID:");
		
		String pixel = pixelStr.substring(pos+3).replace(" ", "");
		
		System.out.println(pixel);
			
		
	}
	
	private static void test10() throws Exception{
		
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\ad_10.html");
		
		driver.findElement(By.className("ab")).click();
		
	}
	
	private static void testProxy() throws Exception{
		
		driver.get("http://www.ip138.com");
		
		
		
	}
	
	private static void testIframe() throws Exception{
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\iframe_0.html");
		
		String curHandle = driver.getWindowHandle();
		
		WebDriver frame = driver.switchTo().frame("iframe");
		
		frame.findElement(By.id("open")).click();
		
		Set<String> handles = driver.getWindowHandles();
		Iterator<String> iterator = handles.iterator();
		
		WebDriver driver1 = null;
		while(iterator.hasNext()){
			String handle = iterator.next();
			if(!handle.equals(curHandle)){
				driver1 = driver.switchTo().window(handle);
				break;
			}
		}
		
		driver1.findElement(By.id("close")).click();
		driver.switchTo().window(curHandle).switchTo().frame("iframe");
		driver.findElement(By.id("refresh")).click();
	}
	
	private static void test0() throws Exception{
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\bulk_edit_age_context.html");
		
		String xpath = "//div[@id='bulk_edit_age_context']/parent::div/div[2]/div/div/div/div/div/div[1]/div/div/ul/li/div/div[contains(text(),'20')]";
		
//		xpath = "//div[@id='bulk_edit_age_context']/parent::div/div[2]/div/div/div/div/div/div[1]/div/div/ul/li[1]/div[contains(text(),'13')]";
		
		WebElement ele = driver.findElement(By.xpath(xpath));
		
//		ele = ele.findElement(By.xpath("//div[text()='20']/parent::div/parent::li"));

		String text = ele.getText();
		
		System.out.println("text:"+text+";");
		
	}
	
	private static void test8(){
		
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\ad_8.html");
		
		WebElement ele = driver.findElement(By.xpath("//div[@pageid='campaign']"));
		
		String text = ele.getText();
		
		System.out.println(text.contains("Your ad account has been flagged for policy violations"));
	}
	
	
	private static void testA(){
		
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\a.html");
		driver.findElement(By.xpath("//a")).click();
		
		String curWindow = driver.getWindowHandle();
		Set<String> handles = driver.getWindowHandles();
		
		Iterator<String> it = handles.iterator();
		while(it.hasNext()){
			String handle = it.next();
			
			if(!handle.equals(curWindow)){
				ChromeDriver driver1 = (ChromeDriver)driver.switchTo().window(handle);
				
				System.out.println("driver1:"+driver1.getCurrentUrl());
				
				driver1.close();
				
				break;
			}
		}
				
		driver = (ChromeDriver)driver.switchTo().window(curWindow);
		
		System.out.println("driver:"+driver.getCurrentUrl());
		
	}
	
	private static void test9(){
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\ad_9.html");
			
		driver.findElement(By.xpath("//input[@name='support_form_id']/preceding-sibling::div/textarea")).sendKeys("adasad");
	}
	
	private static void test7(){
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\ad_7.html");
		
		WebElement ele = driver.findElement(By.xpath("//form"));
		
		WebElementUtil.print(ele);
	}
	
	private static void test6(){
		
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\ad_6.html");
		WebElement ele = driver.findElement(By.xpath("//div[contains(text(), 'Add Bulk Locations')]/parent::div"));///div[2]/div/div[1]/div/div/div/div[3]/div/div/div/div[1]/textarea"));
		
//		System.out.println(ele.toString());
		WebElementUtil.print(ele);
	}
	
	private static void test5() throws Exception {
		
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\ad_5.html");
		
		String xpath = "//div[@id='buttonBar']/span[@id='submit-button']/*/label/input[@type='submit']";
		
		WebElement ele = driver.findElement(By.xpath(xpath));
		
//		System.out.println("text:"+ele.getText());
//		System.out.println("tagName:"+ele.getTagName());
		ele.click();
//		ele.submit();
	}
	
	private static void test4() throws Exception{
		
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\ad_4.html");
		
		String xpath = "//div[@data-testid='SUISelectorOption/container']/div[contains(text(),'Book Now')]/parent::div";
		
		WebElement ele = driver.findElement(By.xpath(xpath));
		
		System.out.println(ele.getText());
		
		ele.click();
		
	}
	
	private static void test3() throws Exception{
		
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\ad_3.html");
		
		String xpath = "//div[contains(text(),'Describe why people should visit your site')]/parent::div/textarea";
		
		WebElement ele = driver.findElement(By.xpath(xpath));
		
		ele.sendKeys("test");
		
	}
	
	private static void test2() throws Exception{
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\ad_2.html");
		
		String xpath = "//table/tbody/tr/td[1]/div/div[1]/div[@data-testid='ads-draggable-indexed-tab-bar-item']";
		
		WebElement ele = driver.findElement(By.xpath(xpath));
		
		String text = ele.getText();
		
		System.out.println("text:"+text+";");
	}
	
	private static void test1() throws Exception{
		
		driver.get("C:\\Users\\thinkpad\\Desktop\\海外投放\\html\\test1.html");
		
		String xpath = "//table/tbody/tr/td[2]/button/div/span[contains(text(),'aa')]/parent::div/parent::button";
		
		WebElement ele = driver.findElement(By.xpath(xpath));
		
		String text = ele.getText();
		
		System.out.println("text:"+text+";");
		
		
	}
}
