package com.lxtx.fb.task.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.lxtx.fb.pojo.Active;
import com.lxtx.fb.pojo.Ad;
import com.lxtx.fb.pojo.AdSet;
import com.lxtx.fb.pojo.AlipayAccount;
import com.lxtx.fb.pojo.FlagContact;
import com.lxtx.fb.pojo.Page;
import com.lxtx.fb.pojo.User;
import com.lxtx.fb.task.util.CommonUtil;
import com.lxtx.fb.task.util.Constants;
import com.lxtx.fb.task.util.OpClick;
import com.lxtx.fb.task.util.OpData;
import com.lxtx.fb.task.util.OpDataUtil;
import com.lxtx.fb.task.util.OpFunc;
import com.lxtx.fb.task.util.OpSelect;
import com.lxtx.fb.task.util.OpSendKeys;
import com.lxtx.fb.task.util.WebDriverFactory;
import com.lxtx.fb.task.util.WebElementUtil;

public class FbService {
	
	static class ServiceResult{		
		private boolean succ = false;
		private String errCode = "";
		private Object data = null;
		
		public ServiceResult(){
			
		}
		
		public ServiceResult(boolean succ){
			this.succ = succ;
		}
		
		public ServiceResult(boolean succ, String errCode){
			this(succ);
			this.errCode = errCode;
		}
		
		public ServiceResult(boolean succ, String errCode, Object data){
			this(succ, errCode);
			this.data = data;
		}
		
		public void fillException(Exception e){
			this.succ = false;
			this.errCode = e.getMessage();
		}
		
		public boolean isSucc() {
			return succ;
		}
		public void setSucc(boolean succ) {
			this.succ = succ;
		}
		public String getErrCode() {
			return errCode;
		}
		public void setErrCode(String errCode) {
			this.errCode = errCode;
		}
		public Object getData() {
			return data;
		}
		public void setData(Object data) {
			this.data = data;
		}
	}
	
	public ServiceResult service(String methodName, User user, Object ... params){
		
		ServiceResult result = new ServiceResult();
		WebDriver webDriver = init(user);
		
		if(user.getStatus() >= 0){
			
			if(!methodName.equals("login")){
				modifyLanguage(webDriver, user);
			}
			
			switch (methodName) {
			case "login":
				result.succ = true;
				break;
			case "pageActive":
				result = pageActive(webDriver, user, (Active)params[0]);
				break;
			case "setting":
				result = setting(webDriver);
				break;
			case "page":
				result = page(webDriver, user, (Page)params[0], (AlipayAccount)params[1]);
				break;
			case "ad":
				result = createAd(webDriver, user, (AdSet)params[0]);
				break;
			case "checkAd":
				result = checkAd(webDriver, user, (FlagContact)params[0]);
				break;
			case "flagContact":
				result = flagContact(webDriver, user, (FlagContact)params[0]);
				break;
			case "test":
				result = test(webDriver, user);
				break;
			case "addMoney":
				result = addMoney(webDriver, user, (AlipayAccount)params[0], (FlagContact)params[1]);
				break;
			default:
				break;
			}
			
			if(!methodName.equals("login")){
				oriLanguage(webDriver, user);
			}
		}else{
			result.errCode = "user login error";
		}
		
		webDriver.close();
		webDriver.quit();
		System.gc();
		
		return result;
	}
	
	public ServiceResult login(User user) {		
		return service("login", user);
	}
	
	public ServiceResult addMoney(User user, AlipayAccount alipayAccount, FlagContact flagContact){
		return service("addMoney", user, alipayAccount, flagContact);
	}
	
	/**
	 * 发表文章或照片
	 * @param user
	 * @param active
	 */
	public ServiceResult pageActive(User user, Active active) {
		return service("pageActive", user, active);
	}
	
	/**
	 * 修改设置
	 * @param user
	 */
	public ServiceResult setting(User user){
		return service("setting", user);
	}
	
	/**
	 * 设置专页
	 * @param user
	 * @param page
	 */
	public ServiceResult page(User user, Page page, AlipayAccount alipayAccount){
		return service("page", user, page, alipayAccount);
	}
	
	public ServiceResult ad(User user, AdSet adSet){
		return service("ad", user, adSet);
	}
	
	/**
	 * 检测广告权限
	 * @param user
	 */
	public ServiceResult checkAd(User user, FlagContact flagContact){
		return service("checkAd", user, flagContact);
	}
	
	/**
	 * 申诉
	 * @param user
	 */
	public ServiceResult flagContact(User user, FlagContact flagContact){
		return service("flagContact", user, flagContact);
	}
	

	public ServiceResult test(User user) {
		return service("test", user);
	}
	
//-----------------------------------------------------------

	private ServiceResult addMoney(WebDriver driver, User user, AlipayAccount alipayAccount, FlagContact flagContact){
		
		ServiceResult checkResult = checkAd(driver, user, flagContact);
		
		if(checkResult.isSucc()){
			if(user.getStatus() == Constants.USERSTATUS_ADPASS || user.getStatus() == Constants.USERSTATUS_ADDELIVERY){
				return realAddMoney(driver, user, alipayAccount);
			}
			return checkResult;
		}else{
			return checkResult;
		}
	}
	
	private ServiceResult realAddMoney(WebDriver driver, User user, AlipayAccount alipayAccount){
		
		ServiceResult result = new ServiceResult();
		
		try{
			//https://www.facebook.com/ads/manager/account/campaigns/?act=10152802837297564&pid=p1
			String curUrl = driver.getCurrentUrl();
			int pos0 = curUrl.indexOf("act=");
			int pos1 = curUrl.indexOf("&pid");
			
			String param = curUrl.substring(pos0+4, pos1);
			
			String billUrl = "https://www.facebook.com/ads/manager/billing/transactions/?act="+param+"&pid=p2";
			
			driver.get(billUrl);
			
			sleep(2000);
			
			driver.findElement(By.xpath("//a[contains(@href,'/ads/manager/account_settings/account_billing/?act=']")).click();
			
			sleep(5000);
			
			driver.findElement(By.xpath("//button[@data-testid='cm_add_pm_button']")).click();
			
			sleep(3000);
			
			createAd_Pay(driver, alipayAccount, result);
			
		}catch(Exception e){
			e.printStackTrace();
			result.fillException(e);
		}
		
		return result;
	}

	private ServiceResult test(WebDriver driver, User user){
	
		ServiceResult result = new ServiceResult(true);
		
		try{
			//https://www.facebook.com/ads/manager/account/campaigns/?act=285024444&pid=p1
			driver.get("https://www.facebook.com/ads/manager");
			
			sleep(2000);
			
			//https://www.facebook.com/ads/manager/account/campaigns/?act=285024444&pid=p1
			String managerUrl = driver.getCurrentUrl();
			
			if(managerUrl.startsWith("https://www.facebook.com/ads/manager/account/campaigns/?act=")){
			
				String param = managerUrl.replace("https://www.facebook.com/ads/manager/account/campaigns/?act=", "");
				int pos = param.indexOf("&pid");
				
				param = param.substring(0, pos);
				
				String adUrl = "https://www.facebook.com/ads/manager/creation/creation/?act="+param+"&pid=p2";
				
				driver.get(adUrl);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	private ServiceResult page(WebDriver driver, User user, Page page, AlipayAccount alipayAccount){
		
		ServiceResult result = new ServiceResult(true);
		
		try{
			createPage(driver, page, result);
			//保存主页链接
			user.setPageUrl(driver.getCurrentUrl());
			
			design(driver, page, result);
			pageSetting(driver, user, page, result);
			pageSetting1(driver, result);
			
			pagePost(driver, user, alipayAccount, result);
		}catch(Exception e){
			result.fillException(e);
		}
		
		return result;
	}
	
	private ServiceResult setting(WebDriver driver){
		
		ServiceResult result = new ServiceResult(true);
		
		if(sound(driver, result)){
			sms(driver, result);
		}
		
		return result;
	}
	
	private ServiceResult pageActive(WebDriver driver, User user, Active active) {
		
		ServiceResult result = new ServiceResult(true);
		
		if(active.valid()){
			try{
				driver.get(user.getPageUrl());
				sleep(4000);
				
				if(active.getImgPath() != null && active.getImgPath().length() > 0) {
					try {
						driver.findElement(By.xpath("//a[@data-testid='media-attachment-selector']")).click();
					}catch(Exception e) {
		    			CommonUtil.debugErr(e);
		    		}
					sleep(3000);
				
					try {
						driver.findElement(By.xpath("//input[@data-testid='media-attachment-add-photo']")).sendKeys(active.getImgPath());
					}catch(Exception e) {
		    			CommonUtil.debugErr(e);
		    		}
					sleep(10000);
				}
				
				if(active.getText() != null && active.getText().length() > 0) {
					try {
						driver.findElement(By.xpath("//textarea[@data-testid='status-attachment-mentions-input']")).click();
					}catch(Exception e) {
		    			CommonUtil.debugErr(e);
		    		}
					sleep(1000);
					
					try {
						driver.findElement(By.xpath("//div[@data-testid='status-attachment-mentions-input']")).sendKeys(active.getText());
					}catch(Exception e) {
		    			CommonUtil.debugErr(e);
		    		}
					sleep(3000);
				}
				
				try {
					driver.findElement(By.xpath("//button[@data-testid='react-composer-post-button']")).click();
				}catch(Exception e) {
	    			CommonUtil.debugErr(e);
	    		}
			}catch(Exception e){
				e.printStackTrace();
				result.fillException(e);
			}
		}
		
		return result;
	}
	
	private ServiceResult flagContact(WebDriver driver, User user, FlagContact flagContact){
		
		ServiceResult result = new ServiceResult(true);
		
		try{
			//https://www.facebook.com/ads/manager/account/campaigns/?act=285024444&pid=p1
			driver.get("https://www.facebook.com/ads/manager");
			
			sleep(2000);
			
			//https://www.facebook.com/ads/manager/account/campaigns/?act=285024444&pid=p1
			String managerUrl = driver.getCurrentUrl();
			
			if(managerUrl.startsWith("https://www.facebook.com/ads/manager/account/campaigns/?act=")){
			
				String param = managerUrl.replace("https://www.facebook.com/ads/manager/account/campaigns/?act=", "");
				int pos = param.indexOf("&pid");
				
				param = param.substring(0, pos);
				
				String adUrl = "https://www.facebook.com/ads/manager/creation/creation/?act="+param+"&pid=p2";
				
				driver.get(adUrl);
			}
			sleep(5000);
			
			
			WebElement campaignEle = null;
			
			try {
				campaignEle = driver.findElement(By.xpath("//div[@pageid='campaign']"));
			}catch(Exception e) {
    			CommonUtil.debugErr(e);
    		}
			
			String text = campaignEle.getText();
			
			boolean flagged = text.contains("Your ad account has been flagged for policy violations");
			
			if(flagged){
				try {
					driver.findElement(By.xpath("//a[contains(@href,'/help/contact')]")).click();
				}catch(Exception e) {
	    			CommonUtil.debugErr(e);
	    		}
				
				//new window
				String curHandle = driver.getWindowHandle();
				
				Set<String> handles = driver.getWindowHandles();
				Iterator<String> iterator = handles.iterator();
				
				//switch to new window
				WebDriver driverNew = null;
				while(iterator.hasNext()){
					String handle = iterator.next();
					if(!handle.equals(curHandle)){
						driverNew = driver.switchTo().window(handle);
						break;
					}
				}
				
				try {
					driverNew.findElement(By.xpath("//input[@name='is_rep_submitted' and @value='False']/parent::label")).click();
				}catch(Exception e) {
	    			CommonUtil.debugErr(e);
	    		}
				sleep(1000);
				
				try {
					driverNew.findElement(By.xpath("//span[text()='Select Ad Account']/parent::a")).click();
				}catch(Exception e) {
	    			CommonUtil.debugErr(e);
	    		}
				sleep(1000);
				
				try{
					driverNew.findElement(By.xpath("//div[@class='uiContextualLayerPositioner uiLayer']/div[@class='uiContextualLayer uiContextualLayerBelowLeft']/div/div/ul/li[2]/a")).click();
				}catch(Exception e) {
	    			CommonUtil.debugErr(e);
	    		}
				sleep(1000);
				
				try{
					driverNew.findElement(By.xpath("//input[@name='support_form_id']/preceding-sibling::div/textarea")).sendKeys(flagContact.getContact());
				}catch(Exception e) {
	    			CommonUtil.debugErr(e);
	    		}
				
				try{
					driverNew.findElement(By.xpath("//button[@type='submit']")).click();
				}catch(Exception e) {
	    			CommonUtil.debugErr(e);
	    		}
				
				sleep(5000);
				
				try {
					driverNew.close();
				}catch(Exception e) {
	    			CommonUtil.debugErr(e);
	    		}
				
				try{
					driver.switchTo().window(curHandle);
				}catch(Exception e) {
	    			CommonUtil.debugErr(e);
	    		}
			}
		}catch(Exception e){
			e.printStackTrace();
			result.fillException(e);
		}
		
		return result;
	}
	
	private ServiceResult checkAd(WebDriver driver, User user, FlagContact flagContact){
		ServiceResult result = new ServiceResult(true);
		try{
			//https://www.facebook.com/ads/manager/account/campaigns/?act=285024444&pid=p1
			driver.get("https://www.facebook.com/ads/manager");
			
			sleep(2000);
			
			afterAdsManager(driver);
			
			boolean flagged = false;
			boolean flaggedOfManager = false;
			
			//https://www.facebook.com/ads/manager/account/campaigns/?act=285024444&pid=p1
			String managerUrl = driver.getCurrentUrl();
			
			if(!managerUrl.startsWith("https://www.facebook.com/ads/manager/creation")){
				
				//判断是否有flagged提示
				if(driver.findElements(By.xpath("//span[starts-with(text(),'Your ad account has been flagged')]")).size() == 1){
					flagged = true;
					flaggedOfManager = true;
				}else{
					int pos0 = managerUrl.indexOf("?act=");
					int pos1 = managerUrl.indexOf("&pid");
					
					String param = managerUrl.substring(pos0+4, pos1);
					
					String adUrl = "https://www.facebook.com/ads/manager/creation/creation/?act="+param+"&pid=p2";
					
					driver.get(adUrl);
					
					sleep(5000);
				}
			}
			
			if(!flagged){
				String text = "";
				
				try{
					if(driver.findElements(By.xpath("//div[@pageid='campaign']")).size() == 1){
						WebElement campaignEle = driver.findElement(By.xpath("//div[@pageid='campaign']"));
						text = campaignEle.getText();
					}
				}catch(Exception e) {
	    			CommonUtil.debugErr(e);
	    		}
				
				flagged = text.contains("Your ad account has been flagged for policy violations");
			}
			
			if(!flagged){
				//检测广告状态
				result = checkAdStatus(driver, user);
			}else{
				if(user.getStatus() <= Constants.USERSTATUS_AD){
					user.setStatus(Constants.USERSTATUS_ADFLAG);
					user.setAdFlagDay(CommonUtil.getDay(0));
				}
				
				//执行申诉
				if(user.getAdFlagContactDay() < CommonUtil.getDay(0)){
					
					if(flaggedOfManager){
						flagContactAtManagerPage(driver, flagContact);
					}else{
						flagContactAtAdsPage(driver, flagContact);
					}
					
					user.setStatus(Constants.USERSTATUS_ADFLAGCONTACT);
					user.setAdFlagContactDay(CommonUtil.getDay(0));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			result.fillException(e);
		}
		
		return result;
	}
	
	private void flagContactAtManagerPage(WebDriver driver, FlagContact flagContact) throws Exception{
		List<OpData> opDataList = new ArrayList<OpData>();
		
		opDataList.add(new OpData("1", 0, new OpClick(driver, By.xpath("//span[starts-with(text(),'Your ad account has been flagged')]/a")), 2000, ""));
	
		realFlagContact(driver, flagContact);
	
		
	}
	
	private void flagContactAtAdsPage(WebDriver driver, FlagContact flagContact) throws Exception{
		
		OpDataUtil.op(new OpData("1", 0, new OpClick(driver, By.xpath("//a[contains(@href,'/help/contact')]")), 1000, ""));
		
		//new window
		String curHandle = driver.getWindowHandle();
		
		Set<String> handles = driver.getWindowHandles();
		Iterator<String> iterator = handles.iterator();
		
		//switch to new window
		WebDriver driverNew = null;
		while(iterator.hasNext()){
			String handle = iterator.next();
			if(!handle.equals(curHandle)){
				driverNew = driver.switchTo().window(handle);
				break;
			}
		}
		
		realFlagContact(driverNew, flagContact);
		
		try {
			driverNew.close();
		}catch(Exception e) {
			CommonUtil.debugErr(e);
		}
		
		try{
			driver.switchTo().window(curHandle);
		}catch(Exception e) {
			CommonUtil.debugErr(e);
		}
	}
	
	private void realFlagContact(WebDriver driver, FlagContact flagContact) throws Exception{
		List<OpData> opDataList = new ArrayList<OpData>();
		
		opDataList.add(new OpData("1", 0, new OpClick(driver, By.xpath("//input[@name='is_rep_submitted' and @value='False']/parent::label")), 1000, ""));
		opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//span[text()='Select Ad Account']/parent::a")), 1000, "1"));
		opDataList.add(new OpData("3", 0, new OpClick(driver, By.xpath("//div[@class='uiContextualLayerPositioner uiLayer']/div[@class='uiContextualLayer uiContextualLayerBelowLeft']/div/div/ul/li[2]/a")), 1000, "2"));
		opDataList.add(new OpData("4", 0, new OpSendKeys(driver, By.xpath("//input[@name='support_form_id']/preceding-sibling::div/textarea"), flagContact.getContact()), 1000, "3"));
		opDataList.add(new OpData("5", 0, new OpClick(driver, By.xpath("//button[@type='submit']")), 5000, "4"));
		
		OpDataUtil.op(opDataList);
	}
	
	private ServiceResult checkAdStatus(WebDriver driver, User user){
		ServiceResult result = new ServiceResult(true);
		
		try{
			try{
				driver.get("https://www.facebook.com/ads/manager");
			}catch(Exception e){
				CommonUtil.debugErr(e);
			}
			sleep(2000);
			
			try{
				String status1 = driver.findElement(By.xpath("div[@data-testid='FixedDataTableRow']/div/div[2]/div/div[1]/div/div[1]/span")).getText();
				
				if(user.getStatus() <= Constants.USERSTATUS_ADFLAGCONTACT){
					if("Active".equals(status1) || "Not Delivering".equals(status1)){
						user.setStatus(Constants.USERSTATUS_ADPASS);
					}
				}else{
					if(!"Active".equals(status1) && !"Not Delivering".equals(status1)){
						user.setStatus(Constants.USERSTATUS_ADFAIL);
					}
				}
			}catch(Exception e){
				CommonUtil.debugErr(e);
			}
			
//			String status2 = statusEle.findElement(By.xpath("./div[2]/div")).getText();
//			
//			System.out.println("status1:"+status1);
//			System.out.println("status2:"+status2);
			
			
		}catch(Exception e){
			e.printStackTrace();
			result.fillException(e);
		}
		
		return result;
	}
	
	private ServiceResult createAd(final WebDriver driver, User user, AdSet adSet){
		
		ServiceResult result = new ServiceResult(true);
		
		try{
			//创建campaign
			if(createAd_CreateCampaign(driver, adSet, result, 0)){
				//设置币种
				if(createAd_SetUpAccount(driver, user, result)){
					//create ad info
					if(createAd_CreateAdInfo(driver, user, adSet, result)){
						//upload img , set headline and description and linkurl 
						if(createAd_CreateImg(driver, user, adSet, result)){
							//pay
							createAd_Pay(driver, adSet.getAlipayAccount(), result);
							sleep(10000);
							
							//close the dialog
							afterAdsManager(driver);
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			
			result.fillException(e);
		}
		
		return result;
	}
	
	private void afterAdsManager(WebDriver driver){
		try{
			if(driver.findElements(By.xpath("div[@aria-label='Dialog content']")).size() > 0){
				OpDataUtil.op(new OpData("", 0, new OpClick(driver, By.xpath("div[@aria-label='Dialog content']")), 1000, ""));
			}
			
			after_pageSetting(driver);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private boolean createAd_CreateAdInfo(final WebDriver driver, final User user, final AdSet adSet, final ServiceResult result){
		
		try{
			List<OpData> opDataList = new ArrayList<OpData>();
			
			opDataList.add(new OpData("1", 0, new OpFunc() {
				@Override
				public void op() throws Exception {
					createAd_CreatePixel(driver, user, result);
				}
			}, 1000, ""));
			
			opDataList.add(new OpData("2", 0, new OpFunc() {
				
				@Override
				public void op() throws Exception {
					createAd_VerifyPixel(driver, result);
				}
			}, 1000, "1"));
			
			opDataList.add(new OpData("3", 0, new OpFunc() {
				
				@Override
				public void op() throws Exception {
					createAd_CreateAudience(driver, adSet, result);
				}
			}, 1000, ""));
			
			opDataList.add(new OpData("4", 0, new OpFunc() {
				
				@Override
				public void op() throws Exception {
					createAd_CreateBudget(driver, adSet, result);
				}
			}, 1000, ""));
			
			opDataList.add(new OpData("5", 1, new OpClick(driver, By.xpath("//button[@data-testid='continue-button']")), 5000, ""));
			
			OpDataUtil.randomOp(opDataList);
			
//			//create a pixel
//			if(createAd_CreatePixel(driver, user, result)){
//				//verify pixel
//				if(createAd_VerifyPixel(driver, result)){
//					//create audience
//					if(createAd_CreateAudience(driver, adSet, result)){
//						//budget and schedule
//						if(createAd_CreateBudget(driver, adSet, result)){
//							//continue button click
//							try{
//								driver.findElement(By.xpath("//button[@data-testid='continue-button']")).click();
//							}catch(Exception e) {
//				    			CommonUtil.debugErr(e);
//				    		}
//							sleep(5000);
//							
//							return true;
//						}
//					}
//				}
//			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			result.fillException(e);
		}
		
		return false;
	}
	
	private boolean createAd_CreateCampaign(WebDriver driver, AdSet adSet, ServiceResult result, int cutTime){
		try{
//			try{
//				driver.findElement(By.id("pageLoginAnchor")).click();
////				driver.findElement(By.id("pageLoginAnchor")).click();
//			}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
//			sleep(5000);
//			
//			try{
//				WebElement linkEle = driver.findElement(By.xpath("//a[contains(@href,'/campaign/landing.php?placement=tcr')]"));
//				driver.get(linkEle.getAttribute("href"));
//			}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
			
			try{
				//https://www.facebook.com/ads/manager/account/campaigns/?act=285024444&pid=p1
				driver.get("https://www.facebook.com/ads/manager");
				
				sleep(2000);
				
				//https://www.facebook.com/ads/manager/account/campaigns/?act=285024444&pid=p1
				//https://www.facebook.com/ads/manage/powereditor/manage/campaigns?act=257201962
				String managerUrl = driver.getCurrentUrl();
				
				if(managerUrl.startsWith("https://www.facebook.com/ads/manager/account/campaigns/?act=")
						|| managerUrl.startsWith("https://www.facebook.com/ads/manage/powereditor/manage/campaigns?act=")){
				
					String param = managerUrl.replace("https://www.facebook.com/ads/manager/account/campaigns/?act=", "")
							.replace("https://www.facebook.com/ads/manage/powereditor/manage/campaigns?act=", "");
					int pos = param.indexOf("&pid");
					if(pos > 0){
						param = param.substring(0, pos);
					}
					String adUrl = "https://www.facebook.com/ads/manager/creation/creation/?act="+param+"&pid=p2";
					
					driver.get(adUrl);
				}
			}catch(Exception e){
				CommonUtil.debugErr(e);
			}
			
			sleep(4000);
			
			try{
				if(driver.findElement(By.xpath("//body")).getText().contains("Start Over")){
					driver.findElement(By.xpath("//button[@data-testid='saved-state-start-over-button']")).click();
				}
				
				sleep(2000);
			}catch(Exception e){
				CommonUtil.debugErr(e);
			}
			
//			try{
//				driver.findElement(By.id("AdsCFObjectiveSelectorItemLabel-CONVERSIONS")).click();
//			}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
//			sleep(1000);
			OpDataUtil.op(new OpData("", 0, new OpClick(driver, By.id("AdsCFObjectiveSelectorItemLabel-CONVERSIONS")), 1000, ""));
			
//			try{
//				driver.findElement(By.xpath("//textarea[contains(text(),'Conversions')]")).clear();
//			}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
			
//			try{
//				WebElement buttonEle = driver.findElement(By.xpath("//button[@data-testid='continue-button']/div/div"));
//				
//				//not equals,mean the language setting not take effect
//				if(!buttonEle.getText().equals("Set Up Ad Account")){
//					if(cutTime <= 2){
//						return createAd_CreateCampaign(driver, adSet, result, cutTime + 1);
//					}else{
//						result.fillException(new Exception("language effect error"));
//						return false;
//					}
//				}
//				
//			}catch(Exception e){
//				CommonUtil.debugErr(e);
//			}
			
//			try{
//				String day = CommonUtil.getDay(0)%10000 + "";
//				driver.findElement(By.xpath("//textarea[contains(text(),'Conversions')]")).sendKeys(adSet.getName()+day);
//			}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
//			try{
//				WebElement buttonEle = driver.findElement(By.xpath("//button[@data-testid='continue-button']")); 
//				
//				
//				
//				buttonEle.click();
//			}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
//			sleep(5000);
			
			String day = CommonUtil.getDay(0)%10000 + "";
			OpDataUtil.op(new OpData("", 0, new OpSendKeys(driver, By.xpath("//textarea[contains(text(),'Conversions')]"), adSet.getCountry()+"_"+CommonUtil.randomStr(4)+"_"+day), 1000, ""));
			OpDataUtil.op(new OpData("", 0, new OpClick(driver, By.xpath("//button[@data-testid='continue-button']")), 5000, ""));
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			result.fillException(e);
		}
		
		return false;
	}
	
	private void getPix(WebDriver driver, User user) throws Exception{
		try {
			//Pixel ID: 1678814232131724
//			String pixelStr = driver.findElement(By.xpath("//div[@data-testid='ads-pixel-tracking']/div[2]/div[2]/div/div[1]/div[2]/div[2]")).getText();
			
			String pixelStr = driver.findElement(By.xpath("//div[substring(text(),string-length(text()) - 7) = \"'s Pixel\"]/following-sibling::div[1]")).getText();
			
			int pos = pixelStr.indexOf("ID:");
			
			String pixel = pixelStr.substring(pos+3).replace(" ", "");
			
			user.setPixel(pixel);
		}catch(Exception e) {
			CommonUtil.debugErr(e);
		}
	}
	
	private boolean createAd_CreateImg(final WebDriver driver, final User user, final AdSet adSet, final ServiceResult result){
		
		try{
			getPix(driver, user);
			
//			try{
//				driver.findElement(By.xpath("//div[@data-testid='message-field']")).sendKeys(adSet.getText());
//			}catch(Exception e){
//				CommonUtil.debugErr(e);
//			}
//			sleep(1000);
			
//			if(createAd_CreateImg_AddButton(driver, adSet, result)){
//				if(createAd_CreateImg_UploadImg(driver, user, adSet, result)){
				
					List<OpData> opDataList = new ArrayList<OpData>();
					
					opDataList.add(new OpData("0", 0, new OpSendKeys(driver, By.xpath("//div[@data-testid='message-field']"), adSet.getText()), 2000, ""));
					opDataList.add(new OpData("1", 0, new OpClick(driver, By.xpath("//div[@data-testid='ads-endcard-checkbox']/div")), 1000, ""));
					opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//button[@data-testid='SUIAbstractMenu/button']")), 1000, ""));
					opDataList.add(new OpData("3", 0, new OpClick(driver, By.xpath("//div[@data-testid='SUISelectorOption/container']/div[contains(text(),'Shop Now')]/parent::div")), 1000, "2"));
					opDataList.add(new OpData("4", 0, new OpFunc() {
						@Override
						public void op() throws Exception {
							createAd_CreateImg_UploadImg(driver, user, adSet, result);
						}
					}, 2000, ""));
					opDataList.add(new OpData("5", 1, new OpClick(driver, By.xpath("//button[@data-testid='place-order-button-WIZARD_FOOTER']")), 15000, "3"));
					
					OpDataUtil.randomOp(opDataList);
					
//					//see-more-website-url-field
//					try{
//						WebElement cardEle = driver.findElement(By.xpath("//div[@data-testid='ads-endcard-checkbox']/div"));
//						String enable = cardEle.getAttribute("aria-checked");
//						if(enable.equals("true")) {
//							cardEle.click();
//						}
////						driver.findElement(By.xpath("//input[@data-testid='see-more-website-url-field']")).sendKeys(adSet.getSeeMoreUrl());
//					}catch(Exception e) {
//		    			CommonUtil.debugErr(e);
//		    		}
//					sleep(1000);
					
//					//call to action
//					try{
//						driver.findElement(By.xpath("//button[@data-testid='SUIAbstractMenu/button']")).click();
//					}catch(Exception e) {
//		    			CommonUtil.debugErr(e);
//		    		}
//					sleep(1000);
					
//					try{
//						driver.findElement(By.xpath("//div[@data-testid='SUISelectorOption/container']/div[contains(text(),'Book Now')]/parent::div")).click();
//					}catch(Exception e) {
//		    			CommonUtil.debugErr(e);
//		    		}
//					sleep(1000);
					
					
//					//submit
//					try{
//						driver.findElement(By.xpath("//button[@data-testid='place-order-button-WIZARD_FOOTER']")).click();
//					}catch(Exception e) {
//		    			CommonUtil.debugErr(e);
//		    		}
//					sleep(15000);
					
					
					return true;
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
			result.fillException(e);
		}
		
		return false;
	}
	
//	private static boolean createAd_CreateImg_AddButton(WebDriver driver, AdSet adSet, ServiceResult result) {
//		try {
//			int size = adSet.getNum();
//			
//			if(size == 0){
//				size = adSet.getAdList().size();
//				adSet.setNum(size);
//			}
//			
//			if(size > 3){
//				try {
//					WebElement addEle = driver.findElement(By.xpath("//table/tbody/tr/td[2]/button/div/span[contains(text(),'Add')]/parent::div/parent::button"));
//					sleep(3000);
//					for(int i = 0; i < size - 3; i ++){
//						addEle.click();
//						sleep(3000);
//					}
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
//			}
//			
//			return true;
//		}catch(Exception e) {
//			e.printStackTrace();
//			result.fillException(e);
//		}
//		
//		return false;
//	}
	
	private static boolean createAd_CreateImg_UploadImg(WebDriver driver, User user, AdSet adSet, ServiceResult result) {
		try {
			int size = adSet.getNum();
			
			String domain = adSet.getDomain();
			String picDir = adSet.getPicDir();
			String headLines = adSet.getHeadLines();
			
			List<File> picFiles = new ArrayList<File>();
			
			if(picDir != null){
				picFiles = CommonUtil.files(new File(picDir), size);
			}
			
			for(int i = 0; i < size; i ++){
				Ad ad = null;
				
				if(domain != null && picDir != null){
					ad = new Ad();
					ad.setImgPath(picFiles.get(i).getAbsolutePath());
					String prefix = CommonUtil.getCodeWithPixel(user.getPixel());
					ad.setLinkUrl("http://"+prefix+"."+domain+"/"+prefix+"/");
					
					List<String> ll = CommonUtil.randoms(headLines, 2);
					ad.setHeadLine(ll.get(0));
					ad.setDescription(ll.get(1));
					
				}else{
					ad = adSet.getAdList().get(i);
				}
				
				List<OpData> opDataList = new ArrayList<OpData>();
				
				if(i >= 3){
					opDataList.add(new OpData("0", -1, new OpClick(driver, By.xpath("//table/tbody/tr/td[2]/button/div/span[contains(text(),'Add')]/parent::div/parent::button")), 2000, ""));
				}
				opDataList.add(new OpData("1", -1, new OpClick(driver, By.xpath("//table/tbody/tr/td[1]/div/div["+(i+1)+"]/div[@data-testid='ads-draggable-indexed-tab-bar-item']")), 1000, "0"));
				opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//button[@data-testid='image-select-button']")), 1000, ""));
				opDataList.add(new OpData("3", 0, new OpSendKeys(driver, By.name("fileToUpload"), ad.getImgPath()), 10000, "2"));
				opDataList.add(new OpData("4", 0 , new OpClick(driver, By.xpath("//button[@data-testid='image-dialog-close-button']")),5000, "3"));
				
				opDataList.add(new OpData("5", 0, new OpSendKeys(driver, By.xpath("//textarea[@data-testid='headline_field']"), ad.getHeadLine()), 1000, ""));
				
				opDataList.add(new OpData("6", 0, new OpSendKeys(driver, By.xpath("//div[contains(text(),'Describe why people should visit your site')]/parent::div/textarea"), ad.getDescription()), 1000, ""));
				
				if(i == 0){
					opDataList.add(new OpData("7", 0, new OpSendKeys(driver, By.xpath("//*[@data-testid='website-url-field']"), ad.getLinkUrl()), 1000, ""));
//					opDataList.add(new OpData("8", 0, new OpClick(driver, By.xpath("//div[@title='Ex: http://www.example.com/page']/parent::div/parent::div/div[2]/div/div/div/div/ul/li[1]")), 3000, "7"));
				}
				
				OpDataUtil.randomOp(opDataList);
				
//				try{
//					driver.findElement(By.xpath("//table/tbody/tr/td[1]/div/div["+(i+1)+"]/div[@data-testid='ads-draggable-indexed-tab-bar-item']")).click();
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
//				sleep(1000);
//				
//				//img button
//				try{
//					driver.findElement(By.xpath("//button[@data-testid='image-select-button']")).click();
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
//				sleep(1000);
				
//				try{
//					driver.findElement(By.name("fileToUpload")).sendKeys(ad.getImgPath());
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
//				sleep(10000);
//				
//				try{
//					driver.findElement(By.xpath("//button[@data-testid='image-dialog-close-button']")).click();
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
//				sleep(5000);
				
//				//headline
//				try{
//					driver.findElement(By.xpath("//textarea[@data-testid='headline_field']")).sendKeys(ad.getHeadLine());
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
//				sleep(1000);
				
//				//description
//				try{
//					driver.findElement(By.xpath("//div[contains(text(),'Describe why people should visit your site')]/parent::div/textarea")).sendKeys(ad.getDescription());
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
//				sleep(1000);
				
				//
//				if(i == 0){
//					setLinkUrl(driver, ad);
//				}
			}
			
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			result.fillException(e);
		}
		
		return false;
	}
	
//	private static void setLinkUrl(WebDriver driver, Ad ad) {
//		try {
//			
//			//linkUrl 
//			try {
////				driver.findElement(By.xpath("//input[@data-testid='website-url-field']")).click();
////				driver.findElement(By.xpath("//input[@data-testid='website-url-field']")).sendKeys("h");
////				driver.findElement(By.xpath("//input[@data-testid='website-url-field']")).clear();
////				sleep(1000);
//				driver.findElement(By.xpath("//input[@data-testid='website-url-field']")).sendKeys(ad.getLinkUrl());
//			}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
//			
//			sleep(2000);
//			
//			try {
//				driver.findElement(By.xpath("//div[@title='Ex: http://www.example.com/page']/parent::div/parent::div/div[2]/div/div/div/div/ul/li[1]")).click();
//			}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
//			sleep(3000);
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	private void test11(WebDriver driver)
	{
		System.out.print("1110");
		
		try {
			List<WebElement> eles = driver.findElements(By.xpath("//div[contains(@class,'uiContextualLayerPositioner')]"));
			
			System.out.println("******");
			for(WebElement ele : eles) {
				WebElementUtil.print(ele);
			}
			System.out.println("******");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("222");
	}
	//select a peyment method, choose alipay
	private void createAd_Pay(WebDriver driver, AlipayAccount alipayAccount, ServiceResult result) throws Exception{
		
		String curUrl = driver.getCurrentUrl();
		
		if(!curUrl.startsWith("https://www.facebook.com/ads/manager/account/ads")){
			try{
				List<OpData> opDataList = new ArrayList<OpData>();
				
				if(!driver.findElement(By.xpath("//span[text()='Show payment methods for:']/following-sibling::span/div/div/a/span[1]")).getText().equals("China")){
					opDataList.add(new OpData("-2", -1, new OpClick(driver, By.xpath("//span[text()='Show payment methods for:']/following-sibling::span/div/div/a")), 2000, ""));
					opDataList.add(new OpData("-1", 0, new OpClick(driver, By.xpath("//span[text()='China']/parent::span/parent::a")), 3000, "-2"));
				}
				
				opDataList.add(new OpData("1", 0, new OpClick(driver, By.xpath("//input[@id='adyenalipay_CN_input']/parent::label/parent::div")), 1000, ""));
				opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//div[@id='XPaymentDialogFooter']/table/tbody/tr/td[1]/div/div[2]/button")), 5000, "1"));
				opDataList.add(new OpData("3", 0, new OpClick(driver, By.id("AdsPaymentDisclaimerAcceptButton")), 2000, "2"));
				opDataList.add(new OpData("4", 0, new OpSendKeys(driver, By.id("amount-entered"), "￥3.00 CNY"), 2000, "3"));
				opDataList.add(new OpData("5", 0, new OpClick(driver, By.id("AdsPaymentsPrepaidAmountButton")), 5000, "4"));
				opDataList.add(new OpData("6", 0, new OpClick(driver, By.xpath("//div[@id='XPaymentDialogFooter']/a[2]")), 15000, "5"));
				
				OpDataUtil.op(opDataList);
				
//				try{
//					driver.findElement(By.xpath("//input[@id='adyenalipay_CN_input']/parent::label/parent::div")).click();	
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
				
//				try{
//					driver.findElement(By.xpath("//div[@id='XPaymentDialogFooter']/table/tbody/tr/td[1]/div/div[2]/button")).click();
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
//				
//				sleep(5000);
				
//				//add money, spend id on ads
//				try{
//					driver.findElement(By.id("AdsPaymentDisclaimerAcceptButton")).click();
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
//				
//				sleep(2000);
				
//				/**
//				 * add money to your balance
//				 */
//				try{
//					driver.findElement(By.id("amount-entered")).clear();
//					driver.findElement(By.id("amount-entered")).sendKeys("￥3.00 CNY");
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
				
//				//review payment a
//				try{
//					driver.findElement(By.id("AdsPaymentsPrepaidAmountButton")).click();
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
//				
//				sleep(5000);
				
//				//make payment a
//				try{
//					driver.findElement(By.xpath("//div[@id='XPaymentDialogFooter']/a[2]")).click();
//				}catch(Exception e) {
//	    			CommonUtil.debugErr(e);
//	    		}
//				
//				sleep(15000);
				
				//continue
				WebDriver frame = null;
				try {
					frame = driver.switchTo().frame("partner_iframe");
					frame.findElement(By.xpath("//span[@id='submit-button']/*/label/input[@type='submit']")).click();
					sleep(6000);
				
				}catch(Exception e) {
					CommonUtil.debugErr(e);
				}
	
				//alipay
				createAd_Pay_Alipay(driver, alipayAccount, result);
				try {
					driver.findElement(By.xpath("//div[@id='XPaymentDialogFooter']/a")).click();
				}catch(Exception e) {
					CommonUtil.debugErr(e);
				}
				
				sleep(10000);
					
//					return true;
//				}
			}catch(Exception e){
				CommonUtil.debugErr(e);
//				result.fillException(e);
			}
			
//			return false;
		}
//		
//		return true;
	}
	
	/**
	 * 进入alipay支付
	 * @param driver
	 */
	private void createAd_Pay_Alipay(WebDriver driver, AlipayAccount alipayAccount, ServiceResult result) throws Exception{
		//当前窗口的句柄
		String curHandle = driver.getWindowHandle();
		
//		try{
			WebDriver alipayDriver = null;
			
			Set<String> handles = driver.getWindowHandles();
			
			Iterator<String> iterator = handles.iterator();
			while(iterator.hasNext()){
				String handle = iterator.next();
				
				if(!handle.equals(curHandle)){
					alipayDriver = driver.switchTo().window(handle);
					break;
				}
			}
			
			if(alipayDriver != null){
				final WebDriver falipayDriver = alipayDriver;
				
				List<OpData> opDataList = new ArrayList<OpData>();
				
				//choose pc pay
				opDataList.add(new OpData("0", 0, new OpClick(alipayDriver, By.xpath("//a[@seed='J_tip_qr-switchTipBtn']")), 1000, ""));
				//set loginId
				opDataList.add(new OpData("1", 0, new OpSendKeys(alipayDriver, By.name("loginId"), alipayAccount.getLoginId()), 1000, "0"));
				//set passwd
				opDataList.add(new OpData("2", 0, new OpSendKeys(alipayDriver, By.name("payPasswd_rsainput"), alipayAccount.getPayPasswd()), 1000, "1"));
				
				opDataList.add(new OpData("3", 0, new OpFunc() {
					@Override
					public void op() throws Exception {
						if(falipayDriver.findElements(By.name("checkCode")).size() > 0){
							
							String checkCode = CommonUtil.identifyCheckCode(falipayDriver, By.name("checkCode"));
							
							OpDataUtil.op(new OpData("0", 0, new OpSendKeys(falipayDriver, By.name("checkCode"), checkCode), 1000, ""));
							
//							CommonUtil.debugErr(new Exception("need img check, do it by manual"));
						}
					}
				}, 1000, ""));
				
				//click
				opDataList.add(new OpData("4", 0, new OpClick(alipayDriver, By.xpath("//a[@id='J_newBtn']/parent::div")), 5000, "3"));
				//check 
				
				opDataList.add(new OpData("5", 0, new OpFunc() {
					
					@Override
					public void op() throws Exception {
						String curUrl = falipayDriver.getCurrentUrl();
						
						//提交没有生效
						if(curUrl.startsWith("https://excashier.alipay.com/standard/auth.htm?auth_order_id=")){
//							OpDataUtil.op(new OpData("1", 0, new OpClick(falipayDriver, By.xpath("//a[@id='J_newBtn']/parent::div")), 10000, ""));
							CommonUtil.debugErr(new Exception("commit error, need confirm by manual"));
						}else if(curUrl.startsWith("报错页面url")){
							CommonUtil.debugErr(new Exception("alipay error, need operate by manual"));
						}
					}
				}, 10000, "4"));
				
				//re passwd
				opDataList.add(new OpData("6", 0, new OpSendKeys(alipayDriver, By.id("payPassword_rsainput"), alipayAccount.getPayPasswd()), 1000, "5"));
				//commit
				opDataList.add(new OpData("7", 0, new OpClick(alipayDriver, By.id("J_authSubmit")), 10000, "6"));
				//check
				final WebDriver driver1 = driver;
				opDataList.add(new OpData("8", 0, new OpFunc() {
					
					@Override
					public void op() throws Exception {
						WebElement checkFrameEle = driver1.findElement(By.xpath("//iframe[contains(@src,'/standard/step2SecProdCheck.htm')]"));
						
						if(checkFrameEle != null) {
							CommonUtil.debugErr(new Exception("need sms check, do it by manual"));
						}
					}
				}, 1000, "7"));
				
				OpDataUtil.op(opDataList);
				
//				//choose pc pay
//				try{
//					alipayDriver.findElement(By.xpath("//a[@seed='J_tip_qr-switchTipBtn']")).click();
//				}catch(Exception e) {
//					CommonUtil.debugErr(e);
//				}
				
//				//set loginId
//				try{
//					alipayDriver.findElement(By.name("loginId")).sendKeys(alipayAccount.getLoginId());
//				}catch(Exception e) {
//					CommonUtil.debugErr(e);
//				}
//				sleep(1000);
				
//				try{
//					alipayDriver.findElement(By.name("payPasswd_rsainput")).sendKeys(alipayAccount.getPayPasswd());
//				}catch(Exception e) {
//					CommonUtil.debugErr(e);
//				}
//				
//				sleep(1000);
				
//				try{
////					alipayDriver.findElement(By.xpath("//a[@seed='NewQr_tAccountSubmit']")).click();
////					alipayDriver.findElement(By.id("J_newBtn")).click();
//					alipayDriver.findElement(By.xpath("//a[@id='J_newBtn']/parent::div")).click();
//				}catch(Exception e) {
//					CommonUtil.debugErr(e);
//				}
//				
//				sleep(10000);
				
//				try{
//					String curUrl = alipayDriver.getCurrentUrl();
//					
//					//提交没有生效
//					if(curUrl.startsWith("https://excashier.alipay.com/standard/auth.htm?auth_order_id=")){
//						alipayDriver.findElement(By.xpath("//a[@id='J_newBtn']/parent::div")).click();
//						
//						sleep(10000);
//					}
//				}catch(Exception e){
//					CommonUtil.debugErr(e);
//				}
				
//				//confirm paypasswd and commit
//				try{
//					alipayDriver.findElement(By.id("payPassword_rsainput")).sendKeys(alipayAccount.getPayPasswd());
//				}catch(Exception e) {
//					CommonUtil.debugErr(e);
//				}
				
//				try{
//					alipayDriver.findElement(By.id("J_authSubmit")).click();
//				}catch(Exception e) {
//					CommonUtil.debugErr(e);
//				}
//				
//				sleep(10000);
				
//				//此处会出现扫码或者验证码，怎么处理？
//				//先堵塞，后边加自动处理
//				try {
//					WebElement checkFrameEle = driver.findElement(By.xpath("//iframe[contains(@src,'/standard/step2SecProdCheck.htm')]"));
//					
//					if(checkFrameEle != null) {
//						CommonUtil.debugErr(new Exception("need sms check"));
//					}
//				}catch(Exception e) {
//					e.printStackTrace();
//				}
				
//				return true;
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			result.fillException(e);
//		}finally{
//			//切回原窗口
			driver = driver.switchTo().window(curHandle);
		}
//		
//		return false;
	}
	
	private boolean createAd_VerifyPixel(WebDriver driver, ServiceResult result){
		try{
			List<OpData> opDataList = new ArrayList<OpData>();
			
			opDataList.add(new OpData("1", 0, new OpSendKeys(driver, By.xpath("//input[@placeholder='Please select a conversion event.']"), "Purchase"), 1000, ""));
			opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//div[contains(@class,'uiContextualLayerPositioner')]/div/div/div/div/div/ul/span/li[1]")), 1000, "1"));
			
			OpDataUtil.op(opDataList);
			
//			try{
//				driver.findElement(By.xpath("//input[@placeholder='Please select a conversion event.']")).clear();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			
//			try{
//				driver.findElement(By.xpath("//input[@placeholder='Please select a conversion event.']")).sendKeys("Purchase");
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
//			try{
//				driver.findElement(By.xpath("//div[contains(@class,'uiContextualLayerPositioner')]/div/div/div/div/div/ul/span/li[1]")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			result.fillException(e);
		}
		
		return false;
	}
	
	private boolean createAd_CreateBudget(final WebDriver driver, AdSet adSet, ServiceResult result){
		try {
			List<OpData> opDataList = new ArrayList<OpData>();
			opDataList.add(new OpData("1", -1, new OpSendKeys(driver, By.xpath("//div[@data-testid='campaign-budget']/div/div[2]/label/input"), "￥660.00" ), 2000, ""));
			opDataList.add(new OpData("2", 0, new OpFunc() {
				
				@Override
				public void op() throws Exception {
					WebElement moreEle = driver.findElement(By.xpath("//a[@data-testid='campaign-delivery-advanced-more-link']"));
					
					if(moreEle.getText().contains("Show Advanced Options")) {
						moreEle.click();
						sleep(2000);
					}
				}
			}, 0, "1"));
			opDataList.add(new OpData("3", 0, new OpClick(driver, By.xpath("//div[@data-testid='campaign-conversion-window']/span/div/a")), 2000, "2"));
			opDataList.add(new OpData("4", 0, new OpClick(driver, By.xpath("//div[contains(text(),'7 days click or 1 day view')]/parent::a")), 1000, "3"));
			
			OpDataUtil.op(opDataList);
			
//			//budget
//			try{
//				WebElement budgetEle = driver.findElement(By.xpath("//div[@data-testid='campaign-budget']/div/div[2]/label/input")); 
//				budgetEle.clear();
//				budgetEle.sendKeys("￥660.00");
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			sleep(2000);
			
//			//advanced options
//			try {
//				WebElement moreEle = driver.findElement(By.xpath("//a[@data-testid='campaign-delivery-advanced-more-link']"));
//				
//				if(moreEle.getText().contains("Show Advanced Options")) {
//					moreEle.click();
//					sleep(2000);				
//				}
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
//			try {
//				driver.findElement(By.xpath("//div[@data-testid='campaign-conversion-window']/span/div/a")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			sleep(3000);
			
//			try{
//				driver.findElement(By.xpath("//div[contains(text(),'7 days click or 1 day view')]/parent::a")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			result.fillException(e);
		}
		
		return false;		
	}
	
	private boolean createAd_CreateAudience(final WebDriver driver, final AdSet adSet, final ServiceResult result){
		try{
			List<OpData> opDataList = new ArrayList<OpData>();
			
			opDataList.add(new OpData("1", 0, new OpFunc() {
				
				@Override
				public void op() throws Exception {
					createAd_CreateAudience_SetCountry(driver, adSet, result);
				}
			}, 1000, ""));
			
			opDataList.add(new OpData("2", 0, new OpFunc() {
				
				@Override
				public void op() throws Exception {
					createAd_CreateAudience_SetAgeAndGender(driver, adSet, result);
				}
			}, 1000, ""));
			
			opDataList.add(new OpData("3", 0, new OpFunc() {
				
				@Override
				public void op() throws Exception {
					createAd_CreateAudience_SetInterest(driver, adSet, result);
				}
			}, 1000, ""));
			
			opDataList.add(new OpData("4", 1, new OpFunc() {
				
				@Override
				public void op() throws Exception {
					createAd_CreateAudience_Commit(driver, adSet, result);
				}
			}, 1000, ""));
			
			OpDataUtil.randomOp(opDataList);

			return true;
			
//			//set country
//			if(createAd_CreateAudience_SetCountry(driver, adSet, result)){
//				//set age
//				if(createAd_CreateAudience_SetAgeAndGender(driver, adSet, result)){
//					//set gender
////					if(createAd_CreateAudience_SetGender(driver, adSet, result)){
//						//set interest
//						if(createAd_CreateAudience_SetInterest(driver, adSet, result)){
//							//commot and save
//							return createAd_CreateAudience_Commit(driver, adSet, result);
//						}
////					}
//				}
//			}
			
		}catch(Exception e){
			e.printStackTrace();
			result.fillException(e);
		}
		
		return false;
	}
	
	private void createAd_CreateAudience_Commit(WebDriver driver, AdSet adSet, ServiceResult result) throws Exception{
//		try{
			List<OpData> opDataList = new ArrayList<OpData>();
			
			//commit
			opDataList.add(new OpData("1", -1, new OpClick(driver, By.xpath("//div[contains(text(),'Save This Audience')]/parent::div/parent::button")), 1000, ""));
			
			//save
			String audienceName = adSet.getCountry()+"_"+adSet.getMinAge()+"_"+adSet.getMaxAge()+"_"+adSet.getGender()+"_"+CommonUtil.randomStr(4);
			opDataList.add(new OpData("2", 0, new OpSendKeys(driver, By.xpath("//div[contains(text(),'Audience Name')]/parent::h4/parent::div/parent::div/div[2]/div/div/div/div[1]/textarea"), audienceName), 2000, "1"));
			
			//sava
			opDataList.add(new OpData("3", 0, new OpClick(driver, By.xpath("//div[text()='Save']/parent::div/parent::button")), 5000, "2"));
			
			OpDataUtil.op(opDataList);
			
//			//commit
//			try{
//				driver.findElement(By.xpath("//div[contains(text(),'Save This Audience')]/parent::div/parent::button")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
//			//save
//			String audienceName = adSet.getCountry()+"_"+adSet.getMinAge()+"_"+adSet.getMaxAge()+"_"+adSet.getGender();
//			
//			try {
//				WebElement audienceEle = driver.findElement(By.xpath("//div[contains(text(),'Audience Name')]/parent::h4/parent::div/parent::div/div[2]/div/div/div/div[1]/textarea"));
//				audienceEle.clear();
//				audienceEle.sendKeys(audienceName);
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
//			try{
//				driver.findElement(By.xpath("//div[text()='Save']/parent::div/parent::button")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//		
//			sleep(5000);
			
//			return true;
//		}catch(Exception e){
//			e.printStackTrace();
//			result.fillException(e);
//		}
//		
//		return false;
	}
	
	private void createAd_CreateAudience_SetCountry(final WebDriver driver, AdSet adSet, ServiceResult result) throws Exception{
//		try{
			sleep(3000);
			
			List<OpData> opDataList = new ArrayList<OpData>();
			
			opDataList.add(new OpData("0", -1, new OpFunc() {
				
				@Override
				public void op() throws Exception {
					try{
						while(driver.findElements(By.xpath("//button[@data-testid='geo_remove_target_location'][1]")).size() > 0) {
							//clear the default country
							driver.findElement(By.xpath("//button[@data-testid='geo_remove_target_location'][1]")).click();
							sleep(3000);
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}, 1000, ""));
			opDataList.add(new OpData("1", 0, new OpClick(driver, By.xpath("//a[contains(text(), 'Add Bulk Locations...')]")), 1000, ""));
			opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//span[contains(text(), 'Choose one...')]/parent::a")), 1000, "1"));
			opDataList.add(new OpData("3", 0, new OpClick(driver, By.xpath("//span[contains(text(), 'Country')]/parent::span/parent::a")), 1000, "2"));
			opDataList.add(new OpData("4", 0, new OpSendKeys(driver, By.xpath("//div[contains(text(), 'Add Bulk Locations')]/parent::div/div[2]/div/div[1]/div/div/div/div[3]/div/div/div/div[1]/textarea"),adSet.getCountry()), 1000, "3"));
			opDataList.add(new OpData("5", 0, new OpClick(driver, By.xpath("//div[contains(text(), 'Match locations')]/parent::div/parent::button")), 1000, "4"));
			opDataList.add(new OpData("6", 0, new OpClick(driver, By.xpath("//div[contains(text(), 'Add 1 Matched location')]/parent::div/parent::button")), 2000, "5"));
			
			OpDataUtil.op(opDataList);
			
//			try{
//				driver.findElement(By.xpath("//a[contains(text(), 'Add Bulk Locations...')]")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			sleep(1000);
			
//			try{
//				driver.findElement(By.xpath("//span[contains(text(), 'Choose one...')]/parent::a")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
//			try{
//				driver.findElement(By.xpath("//span[contains(text(), 'Country')]/parent::span/parent::a")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
//			try{
//				driver.findElement(By.xpath("//div[contains(text(), 'Add Bulk Locations')]/parent::div/div[2]/div/div[1]/div/div/div/div[3]/div/div/div/div[1]/textarea")).sendKeys(adSet.getCountry());
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
//			try{
//				driver.findElement(By.xpath("//div[contains(text(), 'Match locations')]/parent::div/parent::button")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			sleep(1000);
			
			//多个国家为 Add * Matched locations
//			try{
//				driver.findElement(By.xpath("//div[contains(text(), 'Add 1 Matched location')]/parent::div/parent::button")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			sleep(1000);
			
//			return true;
//		}catch(Exception e){
//			e.printStackTrace();
//			result.fillException(e);
//		}
//		
//		return false;
	}
	
	private void createAd_CreateAudience_SetAgeAndGender(WebDriver driver, AdSet adSet, ServiceResult result) throws Exception{
//		try{
			List<OpData> opDataList = new ArrayList<OpData>();
			
			opDataList.add(new OpData("1", 0, new OpClick(driver, By.xpath("//div[@id='bulk_edit_age_context']/div/div[2]/div/div/div/div[1]/button")), 1000, ""));
			opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//div[@id='bulk_edit_age_context']/parent::div/div[2]/div/div/div/div/div/div[1]/div/div/ul/li/div/div[contains(text(),'"+adSet.getMinAge1()+"')]")), 1000, "1"));
			
			opDataList.add(new OpData("3", 0, new OpClick(driver, By.xpath("//div[@id='bulk_edit_age_context']/div/div[2]/div/div/div/div[2]/button")), 1000, ""));
			opDataList.add(new OpData("4", 0, new OpClick(driver, By.xpath("//div[@id='bulk_edit_age_context']/parent::div/div[2]/div/div/div/div/div/div[1]/div/div/ul/li/div/div[contains(text(),'"+adSet.getMaxAge1()+"')]")), 1000, "3"));
			
			opDataList.add(new OpData("5", 0, new OpClick(driver, By.xpath("//div[@id='bulk_edit_gender_context']/div/div[2]/div/div/div/button["+(3 - adSet.getGender())+"]")), 1000, ""));
			
			OpDataUtil.randomOp(opDataList);
			
//			//min age
//			try{
//				driver.findElement(By.xpath("//div[@id='bulk_edit_age_context']/div/div[2]/div/div/div/div[1]/button")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			
//			try{
//				driver.findElement(By.xpath("//div[@id='bulk_edit_age_context']/parent::div/div[2]/div/div/div/div/div/div[1]/div/div/ul/li/div/div[contains(text(),'"+adSet.getMinAge()+"')]")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			
//			sleep(500);
			
//			//max age
//			try{
//				driver.findElement(By.xpath("//div[@id='bulk_edit_age_context']/div/div[2]/div/div/div/div[2]/button")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			
//			try{
//				driver.findElement(By.xpath("//div[@id='bulk_edit_age_context']/parent::div/div[2]/div/div/div/div/div/div[1]/div/div/ul/li/div/div[contains(text(),'"+adSet.getMaxAge()+"')]")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			
//			sleep(500);
			
//			return true;
//		}catch(Exception e){
//			e.printStackTrace();
//			result.fillException(e);			
//		}
//		
//		return false;
	}
	
//	private boolean createAd_CreateAudience_SetGender(WebDriver driver, AdSet adSet, ServiceResult result){
//		try{
//			int idx = 3 - adSet.getGender();
//			
//			try{
//				driver.findElement(By.xpath("//div[@id='bulk_edit_gender_context']/div/div[2]/div/div/div/button["+idx+"]")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			
//			sleep(1000);
//			
//			return true;
//		}catch(Exception e){
//			e.printStackTrace();
//			result.fillException(e);
//		}
//		
//		return false;
//	}
	
	private void createAd_CreateAudience_SetInterest(WebDriver driver, AdSet adSet, ServiceResult result) throws Exception{
//		try {
			String interests = adSet.getInterests();
			
			if(interests != null && interests.length() > 0){
				
				int random = 20 + new Random().nextInt(7);
				
				List<String> arr = CommonUtil.randoms(interests, random);
				
				List<OpData> opDataList = new ArrayList<OpData>();
				By by = By.xpath("//input[@placeholder='Add demographics, interests or behaviors']");
				By by1 = By.xpath("//div[@data-testid='targeting_flexible_targeting_entries_view']/ul/div/div/div/li[1]");
				
				for(int i = 0; i < arr.size(); i ++){
					String interest = arr.get(i);
					
					opDataList.add(new OpData(""+(i*2), 0, new OpSendKeys(driver, by, interest), 1000, ""));
					opDataList.add(new OpData(""+(i * 2 + 1), 0, new OpClick(driver, by1), 1000, ""+(i * 2)));
				}
				
				OpDataUtil.op(opDataList);
				

//				WebElement interestEle = driver.findElement(By.xpath("//input[@placeholder='Add demographics, interests or behaviors']"));
//				
//				for(String interest : arr){
//					
//					try{
//						interestEle.clear();
//						sleep(500);
//						interestEle.sendKeys(interest);
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//
//					sleep(1000);
//					
//					try{
//						driver.findElement(By.xpath("//div[@data-testid='targeting_flexible_targeting_entries_view']/ul/div/div/div/li[1]")).click();
//					}catch(Exception e) {
////						CommonUtil.debugErr(e);
//						e.printStackTrace();
//					}
//					
//					sleep(500);
//				}
			}
			
//			return true;
//		}catch(Exception e) {
//			e.printStackTrace();
//			
//			result.fillException(e);
//		}
//		
//		return false;
	}
	
	private boolean createAd_CreatePixel(WebDriver driver, User user, ServiceResult result){
		try{
			
			if(driver.findElement(By.id("globalContainer")).getText().contains("Create a Pixel")){
			
				List<OpData> opDataList = new ArrayList<OpData>();
				
				opDataList.add(new OpData("1", -1, new OpClick(driver, By.xpath("//button[contains(text(), 'Create a Pixel')]")), 3000, ""));
				opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//div[contains(text(),'Create')]/parent::div/parent::button")), 10000, "1"));
				
				OpDataUtil.op(opDataList);
				
				List<WebElement> eles = driver.findElements(By.xpath("//button/div/div[text()='Set Up Now']"));
				eles = new ArrayList<WebElement>();
				
				if(eles.size() > 0){
					OpDataUtil.op(new OpData("", 0, new OpClick(driver, By.xpath("//button/div/div[text()='Set Up Now']/parent::div/parent::button/parent::a/following-sibling::button")), 2000, ""));
				}else{
					opDataList = new ArrayList<OpData>();
					
					if(new Random().nextInt(2) == 0){
						opDataList.add(new OpData("3", 0, new OpClick(driver, By.xpath("//div[contains(text(),'Continue What I Was Doing')]/parent::div/parent::div")), 3000, "2"));
					}else{
						opDataList.add(new OpData("4", 0, new OpClick(driver, By.xpath("//div[contains(text(),'Set up the Pixel Now')]/parent::div/parent::div")), 3000, "2"));
						opDataList.add(new OpData("5", 0, new OpClick(driver, By.xpath("//div[contains(text(),'Manually Install the Code Yourself')]/parent::div/parent::div")), 3000, "4"));
						opDataList.add(new OpData("6", 0, new OpClick(driver, By.xpath("//div[text()= 'Email Instructions']/parent::div/parent::button/following-sibling::button")), 2000, "5"));
						opDataList.add(new OpData("7", 0, new OpClick(driver, By.xpath("//span[contains(text(), 'Purchase')]/parent::div/div")), 1000, "6"));
						opDataList.add(new OpData("8", 0, new OpClick(driver, By.xpath("//span[contains(text(), 'Add to Cart')]/parent::div/div")), 1000, "7"));
						opDataList.add(new OpData("9", 1, new OpClick(driver, By.xpath("//div[contains(text(), 'Done')]/parent::div/parent::button")), 10000, "8"));
					}
					
					OpDataUtil.op(opDataList);
				}
				
//				try{
//					driver.findElement(By.xpath("//button[contains(text(), 'Create a Pixel')]")).click();
//				}catch(Exception e) {
//					CommonUtil.debugErr(e);
//				}
//				sleep(3000); 
				
//				try{
//					driver.findElement(By.xpath("//div[contains(text(),'Create')]/parent::div/parent::button")).click();
//				}catch(Exception e) {
//					CommonUtil.debugErr(e);
//				}
//				sleep(10000);
				
//				boolean continued = new Random().nextInt(2) == 0 ? true : false;
//				
//				if(continued){
//					try{
//						driver.findElement(By.xpath("//div[contains(text(),'Continue What I Was Doing')]/parent::div/parent::div")).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					sleep(3000);
//				}else{
//					try{
//						driver.findElement(By.xpath("//div[contains(text(),'Set up the Pixel Now')]/parent::div/parent::div")).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					sleep(3000);
//					
//					try{
//						driver.findElement(By.xpath("//div[contains(text(),'Manually Install the Code Yourself')]/parent::div/parent::div")).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					sleep(3000);
//					
//					//此处有点击问题，目标没有命中
//					try{
//						By by = By.xpath("//div[text()= 'Email Instructions']/parent::div/parent::button/following-sibling::button");
//						CommonUtil.scrollToElement(driver, by);
//						driver.findElement(by).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					sleep(2000);
//					
//					try{
//						driver.findElement(By.xpath("//span[contains(text(), 'Purchase')]/parent::div/div")).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					sleep(1000);
//					
//					try{
//						driver.findElement(By.xpath("//span[contains(text(), 'Add to Cart')]/parent::div/div")).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					sleep(1000);
//					
//					try{
//						driver.findElement(By.xpath("//div[contains(text(), 'Done')]/parent::div/parent::button")).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					sleep(10000);
//				}
			}
			
			return true;
		}catch(Exception e){
			e.printStackTrace();
			result.fillException(e);
		}
			
		return false;
		
	}
	
	private boolean createAd_SetUpAccount(WebDriver driver, User user, ServiceResult result){
		try{
			WebElement globalContainerEle = driver.findElement(By.id("globalContainer"));
			if(globalContainerEle.getText().contains("Set Up Ad Account")){
				try{
//					try{
//						driver.findElement(By.xpath("//div[@data-testid='country-selector']")).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					
//					try{
//						driver.findElement(By.xpath("//a[@data-testid='country-selector-CN']")).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					sleep(2000);
					
					List<OpData> opDataList = new ArrayList<OpData>();
					
					opDataList.add(new OpData("1", -1, new OpClick(driver, By.xpath("//div[@data-testid='country-selector']")), 1000, ""));
					opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//a[@data-testid='country-selector-CN']")), 2000, ""));
					
//					opDataList.add(new OpData("3", -1, new OpClick(driver, By.xpath("//div[@data-testid='currency-selector']")), 1000, ""));
//					opDataList.add(new OpData("4", 0, new OpClick(driver, By.xpath("//a[@data-testid='currency-selector-CNY']")), 1000,"1"));
					
					opDataList.add(new OpData("5", 1, new OpClick(driver, By.xpath("//button[@data-testid='continue-button']")), 15000, ""));
					
					OpDataUtil.op(opDataList);
					
//					try{
//						driver.findElement(By.xpath("//div[@data-testid='currency-selector']")).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					
//					try{
//						driver.findElement(By.xpath("//a[@data-testid='currency-selector-CNY']")).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					sleep(500);
					
//					try{
//						driver.findElement(By.xpath("//button[@data-testid='continue-button']")).click();
//					}catch(Exception e) {
//						CommonUtil.debugErr(e);
//					}
//					sleep(5000);
					
					return true;
				}catch(Exception e){
					e.printStackTrace();
					result.fillException(e);
				}
				
				return false;
			}else{
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
			result.fillException(e);
		}	
		
		return false;
		
	}
	
	
	public WebDriver init(User user){
		WebDriver webDriver = webDriverFactory.getChromeWebDriver(user);
		
		login(user, webDriver);
		
		return webDriver;
	}
	
	//修改语言为en-us
	private void modifyLanguage(WebDriver driver, User user){
		try {
			try {
				if(!driver.getCurrentUrl().startsWith("https://www.facebook.com/settings?tab=language")){
					driver.get("https://www.facebook.com/settings?tab=language");
				}
				
				String oldLanguage = null;
				
				oldLanguage = driver.findElement(By.xpath("//div[@id='SettingsPage_Content']/ul/li[1]/div/div/ul/li/a/span[3]/div[2]/div/strong")).getText();
				
				if(!oldLanguage.startsWith("English (US)")){
					user.setLanguage(oldLanguage);
					loginService.updateLanguage(user);
					
					List<OpData> opDataList = new ArrayList<OpData>();
					opDataList.add(new OpData("1", -1, new OpClick(driver, By.xpath("//div[@id='SettingsPage_Content']/ul/li[1]/div/div/ul/li/a")),1000, ""));
					opDataList.add(new OpData("2", 0, new OpSelect(driver, By.name("new_language"), 2, "English (US)"), 1000, "1"));
					opDataList.add(new OpData("3", 1, new OpClick(driver, By.xpath("//select[@name='new_language']/parent::div/parent::div/parent::div/parent::div/parent::div/parent::div/div[2]/div/label[1]/input")), 10000, "2"));
					
					OpDataUtil.op(opDataList);
					
//					driver.findElement(By.xpath("//div[@id='SettingsPage_Content']/ul/li[1]/div/div/ul/li/a")).click();
//					
//					Select sel = new Select(driver.findElement(By.name("new_language")));
//					sel.selectByVisibleText("English (US)");
//					
//					
//					driver.findElement(By.xpath("//select[@name='new_language']/parent::div/parent::div/parent::div/parent::div/parent::div/parent::div/div[2]/div/label[1]/input")).click();
//					
//					sleep(10000);
				}
			}catch(Exception e) {
				CommonUtil.debugErr(e);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void oriLanguage(WebDriver driver, User user){
		try {
			if(user.getLanguage() != null && user.getLanguage().length() > 0 && !user.getLanguage().startsWith("English (US)")){
//				try {
//					driver.get("https://www.facebook.com/settings?tab=language");
//					
//					List<OpData> opDataList = new ArrayList<OpData>();
//					opDataList.add(new OpData("1", -1, new OpClick(driver, By.xpath("//div[@id='SettingsPage_Content']/ul/li[1]/div/div/ul/li/a")),1000, ""));
//					opDataList.add(new OpData("2", 0, new OpSelect(driver, By.name("new_language"), 2, user.getLanguage()), 1000, "1"));
//					opDataList.add(new OpData("3", 1, new OpClick(driver, By.xpath("//select[@name='new_language']/parent::div/parent::div/parent::div/parent::div/parent::div/parent::div/div[2]/div/label[1]/input")), 2000, "2"));
//					
//					OpDataUtil.op(opDataList);
//					
////					driver.findElement(By.xpath("//div[@id='SettingsPage_Content']/ul/li[1]/div/div/ul/li/a")).click();
////					
////					Select sel = new Select(driver.findElement(By.name("new_language")));
////					sel.selectByVisibleText(user.getLanguage());
////					
////					driver.findElement(By.xpath("//select[@name='new_language']/parent::div/parent::div/parent::div/parent::div/parent::div/parent::div/div[2]/div/label[1]/input")).click();
//				}catch(Exception e) {
//					CommonUtil.debugErr(e);
//				}
				try{
					driver.get("https://www.facebook.com");
					
					List<OpData> opDataList = new ArrayList<OpData>();
					
					opDataList.add(new OpData("1", 0, new OpClick(driver, By.xpath("//div[@id='pagelet_rhc_footer']/div/div[1]/div/div[1]/div/a[1]")), 1000, ""));
					 
					opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//button[contains(text(),'Change Language')]")), 2000, "1"));
					
					OpDataUtil.op(opDataList);
				}catch(Exception e){
					CommonUtil.debugErr(e);
				}
			}else {
				System.out.println("no need ori language");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//创建主页
    private void createPage(WebDriver driver, Page page, ServiceResult result) throws Exception{
		driver.get("https://www.facebook.com/pages/create/?ref_type=logout_gear");
//		try {
			List<OpData> opDataList = new ArrayList<OpData>();
			
			opDataList.add(new OpData("1", -1, new OpClick(driver, By.id("community")), 2000, ""));
			opDataList.add(new OpData("2", 0, new OpSendKeys(driver, By.id("community_form_page_name"), getPageName(page.getName())+" "+(1+new Random().nextInt(5))), 2000, "1"));
			opDataList.add(new OpData("3", 0, new OpClick(driver, By.xpath("//form[@id='community_form']/button")), 10000, "2"));
			
			OpDataUtil.op(opDataList);
			
//			try{
//				driver.findElement(By.id("community")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			sleep(2000);
			
//			try{
//				driver.findElement(By.id("community_form_page_name")).sendKeys(getPageName(page.getName())+" "+(1+new Random().nextInt(5)));//Football-Club
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
//			try{
//				driver.findElement(By.xpath("//form[@id='community_form']/button")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			
//			sleep(10000);
			
			createPage_jump(driver);
			
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.fillException(e);
//		}
//
//		return false;
	}
    
    /**
     * 获取专页名称
     * @param name
     * @return
     */
    private String getPageName(String name){
    	
    	String[] arr = name.split(",");
    	
    	if(arr.length == 1){
    		return name;
    	}else{
    		return arr[new Random().nextInt(arr.length)];
    	}
    }
    
    /**
     * 创建主页的时候跳过头像和封面设置浮层
     * @param driver
     */
    private void createPage_jump(WebDriver driver){

		try{
			String curUrl = driver.getCurrentUrl();
			
			if(curUrl.startsWith("https://www.facebook.com/pages/create/get_started/?step=profile_picture")) {
				List<OpData> opDataList = new ArrayList<OpData>();
				
				opDataList.add(new OpData("1", -1, new OpClick(driver, By.xpath("//a[contains(@href,'/pages/create/get_started/save_current_step/?step=profile_picture')]")), 3000, ""));
				opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//a[contains(@href,'/pages/create/get_started/save_current_step/?step=cover_photo')]")), 8000, "1"));
				
				OpDataUtil.op(opDataList);
				
//				try{
//					driver.findElement(By.xpath("//a[contains(@href,'/pages/create/get_started/save_current_step/?step=profile_picture')]")).click();
//				}catch(Exception e) {
//					CommonUtil.debugErr(e);
//				}
//				
//				sleep(3000);
				
//				try{
//					driver.findElement(By.xpath("//a[contains(@href,'/pages/create/get_started/save_current_step/?step=cover_photo')]")).click();
//				}catch(Exception e) {
//					CommonUtil.debugErr(e);
//				}
//				
//				sleep(8000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
    }
    
    //设计主页
    private void design(WebDriver driver, Page page, ServiceResult result) throws Exception{
//    	try {
    		after(driver);
    		after_pageSetting(driver);
    		
    		List<OpData> opDataList = new ArrayList<OpData>();
    		
    		opDataList.add(new OpData("1", -1, new OpClick(driver, By.xpath("//div[@data-testid='profile_picture_edit_menu']")), 1000, ""));
    		opDataList.add(new OpData("2", 0, new OpSendKeys(driver, By.xpath("//input[@data-testid='profile_picture_upload_menu_item']"), getPicPath(page.getPic1())), 5000, "1"));
    		opDataList.add(new OpData("3", 0, new OpClick(driver, By.xpath("//button[@data-testid='profilePicSaveButton']")), 15000, "2"));
    		
    		opDataList.add(new OpData("4", 0, new OpClick(driver, By.xpath("//div[@data-testid='cover_photo_edit_menu']")), 1000, "3"));
    		opDataList.add(new OpData("5", 0, new OpSendKeys(driver, By.name("simple_video_uploader"), getPicPath(page.getPic2())), 15000, "4"));
    		opDataList.add(new OpData("6", 0, new OpClick(driver, By.xpath("//button[@data-testid='cover_photo_save_button']")), 5000, "5"));
    		
    		OpDataUtil.op(opDataList);
    		
//    		//设置头像图片
//    		try {
//    			driver.findElement(By.xpath("//div[@data-testid='profile_picture_edit_menu']")).click();
//    		}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
//        	sleep(1000);
        	
//        	try {
//        		driver.findElement(By.xpath("//input[@data-testid='profile_picture_upload_menu_item']")).sendKeys(getPicPath(page.getPic1()));//"C:\\Users\\Administrator\\Desktop\\data\\psu.jpg"
//        	}catch(Exception e) {
//        		CommonUtil.debugErr(e);
//        	}
//        	sleep(5000);
        	
//        	try {
//        		driver.findElement(By.xpath("//button[@data-testid='profilePicSaveButton']")).click();
//        	}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
//        	sleep(15000);
        	
//        	//设置主页背景
//        	try {
//        		driver.findElement(By.xpath("//div[@data-testid='cover_photo_edit_menu']")).click();
//        	}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
//        	sleep(500);
        	
//        	try {
//        		driver.findElement(By.name("simple_video_uploader")).sendKeys(getPicPath(page.getPic2()));//"C:\\Users\\Administrator\\Desktop\\data\\psu.jpg"
//        	}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
//        	
//        	sleep(15000);
//        	try {
//        		driver.findElement(By.xpath("//button[@data-testid='cover_photo_save_button']")).click();
//        	}catch(Exception e) {
//    			CommonUtil.debugErr(e);
//    		}
//        	
//        	sleep(5000);
        	
    		//设置按钮链接
//        	driver.findElement(By.xpath("//button[@color='fbblue']")).click();
//        	sleep(2000);
//        	driver.findElement(By.xpath("//input[@placeholder='Add a website link']")).sendKeys("www.facebook.com");
//        	driver.findElement(By.xpath("//input[@placeholder='Add a website link']/parent::div/parent::div/parent::div/parent::div/parent::div/div[3]/div/div/div/div[2]/div/button[2]")).click();;
//        	
//        	sleep(10000);
//        	driver.findElement(By.xpath("//a[contains(@href,'ref=pages_manager')]")).click();
        	
//        	return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.fillException(e);
//		}
//    	
//    	return false;
    }
    
    /**
     *	获取专页图片地址，如果为目录则在目录下选择任意一张 
     * @param picPath
     * @return
     */
    private String getPicPath(String picPath){
    	try{
    		File file = new File(picPath);
    		
    		if(file.exists() && file.isDirectory()){
    			File[] files = file.listFiles(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".jpg") || name.endsWith(".png");
					}
				});
    			
    			if(files.length > 0){
    				return files[new Random().nextInt(files.length)].getAbsolutePath();
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return picPath;
    }
    
    //主页设置
    private void pageSetting(WebDriver driver, User user, Page page, ServiceResult result) throws Exception{
//    	try {
	    	driver.get(driver.getCurrentUrl()+"settings/?tab=settings&ref=pages_manager");

	    	after_pageSetting(driver);
	    	
	    	List<OpData> opDataList = new ArrayList<OpData>();
	    	
	    	opDataList.add(new OpData("1", 0, new OpClick(driver, By.xpath("//a[contains(@href,'section=posting_ability')]")), 2000, ""));
	    	opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//label[@for='disallow_posts_to_page_radio']")), 1000, "1"));
	    	opDataList.add(new OpData("3", 0, new OpClick(driver, By.xpath("//label[@for='disallow_posts_to_page_radio']/parent::div/parent::div/parent::div/parent::div/div/div/label[1]")), 5000, "2"));
	    	
	    	opDataList.add(new OpData("4", 0, new OpClick(driver, By.xpath("//a[contains(@href,'section=page_moderation')]")), 2000, ""));
	    	String words = "";
	    	if(user.getFilterWords() != null){
	    		words = user.getFilterWords().getWords();
	    	}
	    	opDataList.add(new OpData("5", 0, new OpSendKeys(driver, By.name("page_moderation_custom_blacklist"), words), 1000, "4"));
	    	opDataList.add(new OpData("6", 0, new OpClick(driver, By.xpath("//textarea[@name='page_moderation_custom_blacklist']/parent::div/parent::div/div[2]/div/label[1]")), 5000, "5"));
	    	
	    	opDataList.add(new OpData("7", 0, new OpClick(driver, By.xpath("//a[contains(@href,'section=profanity_filter')]")), 1000, ""));
	    	opDataList.add(new OpData("8", 0, new OpSelect(driver, By.name("page_moderation_profanity_blacklist"), 2, "Strong"), 1000, "7"));
	    	opDataList.add(new OpData("9", 0, new OpClick(driver, By.xpath("//input[@name='old_page_moderation_profanity_blacklist']/parent::div/parent::div/div[2]/div/label[1]")), 5000, "8"));
	    	
	    	OpDataUtil.randomOp(opDataList);
	    	
//	    	try{
//	    		driver.findElement(By.xpath("//a[contains(@href,'section=posting_ability')]")).click();
//	    	}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//	    	sleep(2000);
	    	
//	    	try {
//		    	WebElement postDisableEle = driver.findElement(By.xpath("//label[@for='disallow_posts_to_page_radio']")); 
//		    	postDisableEle.click();
//	    	}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
	    	
//    		try{
//    			driver.findElement(By.xpath("//label[@for='disallow_posts_to_page_radio']/parent::div/parent::div/parent::div/parent::div/div/div/label[1]")).click();
//    		}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//	    	
//	    	sleep(5000);
	    	
//	    	try{
//	    		driver.findElement(By.xpath("//a[contains(@href,'section=page_moderation')]")).click();
//	    	}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//	    	sleep(2000);
	    	
//	    	String words = "";
//	    	if(user.getFilterWords() != null){
//	    		words = user.getFilterWords().getWords();
//	    	}
	    	
//	    	try{
//	    		driver.findElement(By.name("page_moderation_custom_blacklist")).sendKeys(words);
//	    	}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
	    	
//	    	try{
//	    		driver.findElement(By.xpath("//textarea[@name='page_moderation_custom_blacklist']/parent::div/parent::div/div[2]/div/label[1]")).click();
//	    	}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//	    	sleep(5000);
	    	
//	    	try{
//	    		driver.findElement(By.xpath("//a[contains(@href,'section=profanity_filter')]")).click();
//	    	}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//	    	sleep(2000);
	    	
//	    	try {
//		    	Select sel = new Select(driver.findElement(By.name("page_moderation_profanity_blacklist")));
//		    	sel.selectByVisibleText("Strong");
//	    	}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
	    	
//	    	try{
//	    		driver.findElement(By.xpath("//input[@name='old_page_moderation_profanity_blacklist']/parent::div/parent::div/div[2]/div/label[1]")).click();
//	    	}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//	    	sleep(5000);
	    	
//	    	return true;
//    	} catch (Exception e) {
//			e.printStackTrace();
//			result.fillException(e);
//		}
//    	
//    	return false;
    }
	
    //专页速推
    private void pagePost(final WebDriver driver, final User user, final AlipayAccount alipayAccount, final ServiceResult result) throws Exception{
    	
    	driver.get(user.getPageUrl());
		
		sleep(10000);
    	
    	List<OpData> opDataList = new ArrayList<OpData>(); 
    	
    	opDataList.add(new OpData("1", -1, new OpClick(driver, By.xpath("//button[@data-testid='boosted_post_button_with_id']")), 2000, ""));
    	opDataList.add(new OpData("2", 0, new OpClick(driver, By.xpath("//div[text()='PAYMENT']/parent::div/following-sibling::div/div[2]/div/div/a")), 2000, ""));
    	opDataList.add(new OpData("3", 0, new OpClick(driver, By.xpath("//div[text()='Chinese Yuan']/parent::span/parent::span/parent::a")), 2000, ""));
    	opDataList.add(new OpData("4", 0, new OpSendKeys(driver, By.xpath("//input[@data-testid='budget_selector']"), "￥42.00 CNY"), 2000, ""));
    	opDataList.add(new OpData("5", 0, new OpClick(driver, By.xpath("//button[contains(text(),'7 days')]")), 1000, ""));
    	opDataList.add(new OpData("6", 0, new OpClick(driver, By.xpath("//button[@data-testid='primary_button']")), 5000, ""));
    	opDataList.add(new OpData("7", 0, new OpFunc() {			
			@Override
			public void op() throws Exception {
				createAd_Pay(driver, alipayAccount, result);
			}
		}, 5000, ""));
    	opDataList.add(new OpData("8", 1, new OpClick(driver, By.xpath("//button[contains(text(),'Close')]")), 5000, ""));

    	OpDataUtil.op(opDataList);
    }
    
    private void pageSetting1(WebDriver driver, ServiceResult result) throws Exception{
//    	try{
    		try{
    			driver.findElement(By.xpath("//a[contains(@href,'tab=notifications&ref=page_edit')]")).click();
    		}catch(Exception e) {
				CommonUtil.debugErr(e);
			}
    		sleep(2000);
    		
    		//设置li节点个数
    		int liNum = 4;
    		try{
    			if(driver.findElements(By.xpath("//div[@id='pagelet_dupe_page_report']/div/ul/li")).size() == 5) {
    				liNum = 5;
    			};
    		}catch(Exception e) {
				CommonUtil.debugErr(e);
			}
    		
    		try {
	    		WebElement ele0 = driver.findElement(By.xpath("//div[@id='pagelet_dupe_page_report']/div/ul/li["+(liNum-3)+"]/div/div[1]/div[2]/ul/li[3]/label/input")); 
	    		if(!ele0.isSelected()){
		    		ele0.click(); 
		    		//
		    		driver.findElement(By.xpath("//div[contains(text(),'You may miss important Page updates and activity if you turn off all notifications')]/parent::div/div[3]/div/div/div[2]/div/a[2]")).click();
		    		sleep(2000);
	    		}
    		}catch(Exception e) {
				CommonUtil.debugErr(e);
			}
    		
    		OpDataUtil.op(new OpData("", 0, new OpClick(driver, By.xpath("//div[@id='pagelet_dupe_page_report']/div/ul/li["+(liNum-2)+"]/div/div/div[2]/ul/li[2]/label/input")), 1000, ""));
    		OpDataUtil.op(new OpData("", 0, new OpClick(driver, By.xpath("//div[@id='pagelet_dupe_page_report']/div/ul/li["+(liNum-1)+"]/div/div/div[2]/ul/li[2]/label/input")), 1000, ""));
    		OpDataUtil.op(new OpData("", 0, new OpClick(driver, By.xpath("//div[@id='pagelet_dupe_page_report']/div/ul/li["+liNum+"]/div/div/div[2]/ul/li[2]/label/input")), 1000, ""));
    		
//    		try{
//    			driver.findElement(By.xpath("//div[@id='pagelet_dupe_page_report']/div/ul/li["+(liNum-2)+"]/div/div/div[2]/ul/li[2]/label/input")).click();
//    		}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//    		sleep(1000);
    		
//    		try{
//    			driver.findElement(By.xpath("//div[@id='pagelet_dupe_page_report']/div/ul/li["+(liNum-1)+"]/div/div/div[2]/ul/li[2]/label/input")).click();
//    		}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//    		sleep(1000);
    		
//    		try{
//    			driver.findElement(By.xpath("//div[@id='pagelet_dupe_page_report']/div/ul/li["+liNum+"]/div/div/div[2]/ul/li[2]/label/input")).click();
//    		}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//    		sleep(1000);
    		
//    		return true;
//    	}catch(Exception e){
//    		e.printStackTrace();
//    		result.fillException(e);
//    	}
//    	
//    	return false;
    }
    
	
	//关闭声音通知
    private boolean sound(WebDriver webDriver, ServiceResult result) {
    	webDriver.get("https://www.facebook.com/settings?tab=notifications&section=on_facebook&view");
//    	after();
        try {
			List<OpData> opDataList = new ArrayList<OpData>();
        	
			opDataList.add(new OpData("1", 0, new OpClick(webDriver, By.xpath("//input[@name='notif_sound_enabled']/parent::div")), 1000, ""));
        	opDataList.add(new OpData("2", 1, new OpClick(webDriver, By.xpath("//div[@id='globalContainer']/div[3]/div/div/div/ul/li[2]")), 1000, "1"));
        	
        	opDataList.add(new OpData("3", 0, new OpClick(webDriver, By.xpath("//input[@name='chat_sound_enabled']/parent::div")), 1000, "2"));
        	opDataList.add(new OpData("4", 0 , new OpClick(webDriver, By.xpath("//div[@id='globalContainer']/div[4]/div/div/div/ul/li[2]")), 2000, "3"));
        	
        	OpDataUtil.op(opDataList);
        	
//        	try{
//        		webDriver.findElement(By.xpath("//input[@name='notif_sound_enabled']/parent::div")).click();
//        	}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			sleep(1000);
//			//new div for 3
//			try{
//				webDriver.findElement(By.xpath("//div[@id='globalContainer']/div[3]/div/div/div/ul/li[2]")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
//			try{
//				webDriver.findElement(By.xpath("//input[@name='chat_sound_enabled']/parent::div")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
//			sleep(1000);
//			//new div for 4
//			try{
//				webDriver.findElement(By.xpath("//div[@id='globalContainer']/div[4]/div/div/div/ul/li[2]")).click();
//			}catch(Exception e) {
//				CommonUtil.debugErr(e);
//			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			result.fillException(e);
		}
        
        return false;
	}
    
    //关闭短信通知
    private boolean sms(WebDriver webDriver, ServiceResult result) {
    	webDriver.get("https://www.facebook.com/settings?tab=notifications&section=sms&view");
//    	after();
        try {
        	try {
	        	WebElement divEle = webDriver.findElement(By.xpath("//div[@title='Text message']"));
	        	
	        	if(!divEle.getText().contains("To get these notifications, you need to")) {
		        	boolean changed = false;
		        	
		        	WebElement ele_notif_enabled_0 = webDriver.findElement(By.xpath("//input[@name='notif_enabled' and @value='0']"));
		        	
		        	if(!ele_notif_enabled_0.isSelected()){
		        		ele_notif_enabled_0.click();
		        		changed = true;
		        	}
		        	
		        	WebElement ele_sms_status_wall_post_comment = webDriver.findElement(By.name("notif[sms][sms_status_wall_post_comment]")); 
		        	if(ele_sms_status_wall_post_comment.isSelected()){
		        		ele_sms_status_wall_post_comment.click();
		        		changed = true;
		        	}
		        	
		        	WebElement ele_sms_friend_request_and_conf = webDriver.findElement(By.name("notif[sms][sms_friend_request_and_conf]"));
		        	if(ele_sms_friend_request_and_conf.isSelected()){
		        		ele_sms_friend_request_and_conf.click();
		        		changed = true;
		        	}
		        	
		        	WebElement ele_sms_all_other_notifications = webDriver.findElement(By.name("notif[sms][sms_all_other_notifications]"));
		        	if(ele_sms_all_other_notifications.isSelected()){
		        		ele_sms_all_other_notifications.click();
		        		changed = true;
		        	}
		        	
		        	if(changed){
		        		webDriver.findElement(By.xpath("//form[@action='/ajax/settings/notifications/medium/sms']/div/div[2]/div/label[1]/input")).click();
		        	}
	        	}
        	}catch(Exception e) {
				CommonUtil.debugErr(e);
			}
        	return true;
		} catch (Exception e) {
			e.printStackTrace();
			
			result.fillException(e);
		}
        
        return false;
	}
	
	//提示窗口点击以后再说
    private void after(WebDriver webDriver) {
    	try {
    		if(webDriver.findElements(By.className("layerCancel")).size() > 0){
    			webDriver.findElement(By.className("layerCancel")).click();
    		}
    	}catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void after_pageSetting(WebDriver driver) {
    	try {
    		if(driver.findElements(By.xpath("//button[@title='Remove' and text()='Remove']")).size() > 0){
    			driver.findElement(By.xpath("//button[@title='Remove' and text()='Remove']")).click();
    			sleep(1000);
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
	private boolean login(User user, WebDriver webDriver){
		
		boolean ret = false;
		
		if(user.isCookieUpdate() && user.getCookies() != null && user.getCookies().length() > 0) {
			ret = loginByCookie(user, webDriver);
		}else{
			ret = loginByPass(user, webDriver);
		}
		
		if(ret){
			loginService.saveCookies(user, webDriver.manage().getCookies());
		}
		
		return ret;
	}
	
	private boolean loginByPass(User user, WebDriver webDriver){
		try {
			webDriver.get("https://www.facebook.com/settings?tab=language");
            sleep(5000);
            
            List<OpData> opDataList = new ArrayList<OpData>();
        	
        	opDataList.add(new OpData("1", 0, new OpSendKeys(webDriver, By.id("email"), user.getUserName()), 1000, ""));
        	opDataList.add(new OpData("2", 0, new OpSendKeys(webDriver, By.id("pass"), user.getUserPass()), 1000, ""));
        	opDataList.add(new OpData("3", 1, new OpClick(webDriver, By.id("loginbutton")), 5000, ""));
        	
        	OpDataUtil.randomOp(opDataList);
        	
//            CommonUtil.sendKeys(webDriver, By.id("email"), user.getUserName());
//            CommonUtil.sendKeys(webDriver, By.id("pass"), user.getUserPass());
//            CommonUtil.click(webDriver, By.id("loginbutton"));
//            
//            sleep(5000);
            
            String curUrl = webDriver.getCurrentUrl(); 
            if(curUrl.startsWith("https://www.facebook.com/settings")) {
            	return true;
            }else {
            	if(curUrl.startsWith("https://www.facebook.com/checkpoint")) {
            		user.setStatus(Constants.USERSTATUS_LOGINCHECKED);
            	}else {
            		user.setStatus(Constants.USERSTATUS_INVALIDPASS);
            	}
            	
            	return false;
            }
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean loginByCookie(User user, WebDriver webDriver){
		webDriver.get("https://www.facebook.com");
    	
		if(user.getCookies() != null && user.getCookies().length() > 0) {
	    	try{
	    	    JSONArray ja = new JSONArray(user.getCookies());
	    	    
	            for (int i = 0; i < ja.length(); i++) {
	            	JSONObject jo = ja.getJSONObject(i);
	            	String name=jo.getString("name");
	                String value=jo.getString("value");
	                String domain=jo.getString("domain");
	                String path=jo.getString("path");

	                double expirationDate = jo.getDouble("expirationDate");
	                Date expiry = new Date((long)(expirationDate * 1000));
	                
	                boolean isSecure = jo.getBoolean("secure");
	                boolean httpOnly = jo.getBoolean("httpOnly");
	                
	                Cookie ck=new Cookie(name,value,domain,path,expiry,isSecure,httpOnly);
	                
	                webDriver.manage().addCookie(ck);
	                
	            }
	            
	            webDriver.get("https://www.facebook.com/settings?tab=language");
	            sleep(10000);
	            
	            String curUrl = webDriver.getCurrentUrl(); 
	            
	            if(curUrl.startsWith("https://www.facebook.com/settings")) {
	            	return true;
	            }else if(curUrl.startsWith("https://www.facebook.com/checkpoint")) {
	            	user.setStatus(Constants.USERSTATUS_LOGINCHECKED);
	            	return false;
	            }
	            if(curUrl.startsWith("https://www.facebook.com/login")) {
	            	
	            	List<OpData> opDataList = new ArrayList<OpData>();
	            	
	            	opDataList.add(new OpData("1", 0, new OpSendKeys(webDriver, By.id("email"), user.getUserName()), 1000, ""));
	            	opDataList.add(new OpData("2", 0, new OpSendKeys(webDriver, By.id("pass"), user.getUserPass()), 1000, ""));
	            	opDataList.add(new OpData("3", 1, new OpClick(webDriver, By.id("loginbutton")), 10000, ""));
	            	
	            	OpDataUtil.randomOp(opDataList);
	            	
//	            	CommonUtil.clear(webDriver, By.id("email"));
//	            	CommonUtil.sendKeys(webDriver, By.id("email"), user.getUserName());
//	            	CommonUtil.sendKeys(webDriver, By.id("pass"), user.getUserPass());
//	            	CommonUtil.click(webDriver, By.id("loginbutton"));
//		            sleep(10000);
		            
		            curUrl = webDriver.getCurrentUrl();
		            
		            if(curUrl.startsWith("https://www.facebook.com/login")) {
		            	user.setStatus(Constants.USERSTATUS_INVALIDPASS);
		            	return false;
		            }else if(curUrl.startsWith("https://www.facebook.com/checkpoint")) {
		            	user.setStatus(Constants.USERSTATUS_LOGINCHECKED);
		            	return false;
		            }
		            
		            return true;
	            }else {
	            	user.setStatus(Constants.USERSTATUS_INVALIDPASS);
	            }
	            
	    	}catch(Exception e){
	    	    e.printStackTrace();
	    	}
		}
    	
    	return false;
	}
	
	private static void sleep(long millis){
		try{
			Thread.sleep(millis * 3);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	//ioc
	private WebDriverFactory webDriverFactory;

	private LoginService loginService;
	
	public void setWebDriverFactory(WebDriverFactory webDriverFactory) {
		this.webDriverFactory = webDriverFactory;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}
	
	

}
