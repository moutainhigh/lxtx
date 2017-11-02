package com.lxtx.fb.task.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.os.WindowsUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.lxtx.fb.pojo.Languages;
import com.lxtx.fb.pojo.Proxy;
import com.lxtx.fb.pojo.User;

public class WebDriverFactory {
	
	private String chromeDriverPath = "C:\\chromedriver.exe"; 
	private String zipDir = "C:\\facebook\\zips\\";
	private String templateDir = "C:\\facebook\\templates\\";
	
	public WebDriver getChromeWebDriver(User user){
		//设置时区
		CommonUtil.changeTimeZone(user.getCountry());
		
		//设置启动路径
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		
		//杀掉进程
		WindowsUtils.killByName("chromedriver.exe");
		
		ChromeOptions options = new ChromeOptions();
		
		// 创建HashMap类的一个对象  
        Map<String, Object> prefs = new HashMap<String, Object>();  
        // 设置提醒的设置，2表示block  
        prefs.put("profile.default_content_setting_values.notifications", 2);  
  
        options.setExperimentalOption("prefs", prefs);  
	
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized"));
//		options.addArguments("--test-type", "--start-maximized", "--lang="+user.getLanguages().getSetting());
		options.addArguments("--test-type", "--start-maximized", "--lang=en-US", "--user-agent="+user.getUa().getSetting());
		
		if(user.getProxy() != null){
			try {
				options.addExtensions(getProxyZip(user.getProxy()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		ChromeDriver chromeDriver = new ChromeDriver(options);
		chromeDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		return chromeDriver;
	}
	
	private String backgroundJsContent = null;
	
	private File getProxyZip(Proxy proxy) throws Exception{
		
		String zipFileName = getProxyZipName(proxy);
		
		String zipFilePath = zipDir + zipFileName;
		
		File zipFile = new File(zipFilePath);
		
		if(zipFile.exists()){
			return zipFile;
		}
		
		if(!zipFile.getParentFile().exists()){
			FileUtils.forceMkdir(zipFile.getParentFile());
		}
		
		File zipFileDir = new File(zipFilePath.replace(".zip", ""));
		if(!zipFileDir.exists()){
			FileUtils.forceMkdir(zipFileDir);
		}
		
		File manifestFile = new File(templateDir+"manifest.json");
		FileUtils.copyFileToDirectory(manifestFile, zipFileDir);
		
		if(backgroundJsContent == null){
			backgroundJsContent = FileUtils.readFileToString(new File(templateDir+"background.js"), "utf-8");
		}
		
		String content = backgroundJsContent.replace("{proxy_host}", proxy.getIp())
				.replace("{proxy_port}", proxy.getPort1()+"")
				.replace("{username}", proxy.getUserName())
				.replace("{password}", proxy.getUserPass());
		
		File backgroundJsFile = new File(zipFileDir.getAbsolutePath()+File.separator+"background.js");
		FileUtils.writeStringToFile(backgroundJsFile, content, "utf-8");
		
		zip(zipFileDir, zipFile);
		
		return zipFile;
	}
	
	public static void zip(File srcDir, File zipFile) throws Exception{  
        
        CheckedOutputStream cos = null;  
        ZipOutputStream zos = null;                       
        try{  
            cos = new CheckedOutputStream(new FileOutputStream(zipFile), new CRC32());  
            zos = new ZipOutputStream(cos);  
            
            int count, bufferLen = 1024;  
            byte data[] = new byte[bufferLen];  
            
            for(File srcFile : srcDir.listFiles()){
	            ZipEntry entry = new ZipEntry(srcFile.getName());  
	            zos.putNextEntry(entry);  
	            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));  
	            while ((count = bis.read(data, 0, bufferLen)) != -1)   
	            {  
	                zos.write(data, 0, count);  
	            }  
	            bis.close();  
	            zos.closeEntry();  
            }
            zos.flush();  
        }  
        catch (Exception e)   
        {  
            throw e;  
        }  
        finally   
        {             
            try  
            {  
                if (zos != null)  
                {  
                    zos.close();  
                }                 
            }  
            catch (Exception e)  
            {  
                e.printStackTrace();  
            }             
        }  
    }  
	
	
	private String getProxyZipName(Proxy proxy){
		return proxy.getUserName()+"_"+proxy.getUserPass()+"@"+proxy.getIp()+"_"+proxy.getPort1()+".zip";
	}
	
	//ioc
	

	public static void main(String[] args){
		
		WebDriverFactory factory = new WebDriverFactory();
		
		User user = new User();
		
		Languages languages = new Languages();
		languages.setSetting("zh-CN");
		user.setLanguages(languages);
		
		Proxy proxy = new Proxy();
		proxy.setIp("185.158.135.70");
		proxy.setPort1(41540);
		proxy.setUserName("louis");
		proxy.setUserPass("yWYqE6ARu");
		user.setProxy(proxy);
		
		WebDriver driver = factory.getChromeWebDriver(user);
		
		driver.get("http://g.sjtuqypx.com/test.php");
	}

	public void setChromeDriverPath(String chromeDriverPath) {
		this.chromeDriverPath = chromeDriverPath;
	}

	public void setZipDir(String zipDir) {
		this.zipDir = zipDir;
	}

	public void setTemplateDir(String templateDir) {
		this.templateDir = templateDir;
	}
}
