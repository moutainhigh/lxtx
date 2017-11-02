package com.lxtx.fb.task.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.lxtx.fb.pojo.Active;
import com.lxtx.fb.pojo.Ad;
import com.lxtx.fb.pojo.AdSet;
import com.lxtx.fb.pojo.AlipayAccount;
import com.lxtx.fb.pojo.FilterWords;
import com.lxtx.fb.pojo.FlagContact;
import com.lxtx.fb.pojo.Languages;
import com.lxtx.fb.pojo.Page;
import com.lxtx.fb.pojo.Proxy;
import com.lxtx.fb.pojo.User;
import com.lxtx.fb.task.service.FbService;
import com.lxtx.fb.task.util.WebDriverFactory;

public class Test {

	private static User user = null;
	private static Page page = null;
	private static AdSet adSet = null;
	private static Active active = null;
	private static WebDriverFactory factory = null;
	private static FbService fbService = null;
	private static AlipayAccount alipayAccount = null;
	
	public static void main(String[] args){
		
		initUser();
		initPage();
		initAdSet();
		initFbService();
		initActive();
		initAlipayAccount();
		
//		testSetting();
//		testPage();
//		testAd();
//		testLogin();
//		testFlagContact();
//		testPageActive();
		
		testProxyCheck();
		
	}
	
	private static void testProxyCheck(){
		
		WebDriver driver = factory.getChromeWebDriver(user);
		
		driver.get("http://g.sjtuqypx.com/test.php");
		
		boolean succ = driver.findElement(By.xpath("//body")).getText().contains(user.getProxy().getIp());
		
		System.out.println(succ);
	}
	
	private static void testPageActive() {
		fbService.pageActive(user, active);
	}

	private static void testLogin() {
		fbService.login(user);
	}
	
	private static void testFlagContact() {
		FlagContact flagContact = new FlagContact();
		
		flagContact.setContact(getFlagContactReason());
		
		fbService.flagContact(user, flagContact);
	}
	
	private static void testAdStatus() {
		fbService.checkAd(user);
	}
	
	private static void testAd() {
		fbService.ad(user, adSet);
	}
	
	private static void testPage() {
		fbService.page(user, page, alipayAccount);
	}
	
	private static void testSetting(){
		
		fbService.setting(user);
	}
	
	private static void initFbService(){
		initWebDriverFactory();
		
		fbService = new FbService();
		fbService.setWebDriverFactory(factory);
	}

	private static String[] flagContactReasons = new String[] {
			"I want to advertise on Facebook，Why is it restricted? This is not reasonable. Please reply as soon as possible. Thank you!",
			"I want to advertise, why limit me, please resume as soon as possible, thank you!",
			"Why is that? What did I do wrong? want to advertise on Facebook，Why is it restricted? This is not reasonable.Please help me recover as soon as possible, thank you!"
		};
	
	private static String getFlagContactReason() {
		return flagContactReasons[new Random().nextInt(flagContactReasons.length)];
	}
	
	private static void initWebDriverFactory(){
		factory = new WebDriverFactory();
		factory.setChromeDriverPath("D:/chromedriver.exe");
		factory.setTemplateDir("C:/Users/thinkpad/Desktop/海外投放/templates/");
		factory.setZipDir("C:/Users/thinkpad/Desktop/海外投放/chrome-proxy-extensions/");
	}
	
	private static void initAlipayAccount(){
		alipayAccount = new AlipayAccount();
		alipayAccount.setLoginId("lc34@sina.com");
		alipayAccount.setPayPasswd("991399");
	}
	
	private static void initAdSet(){
		
		adSet = new AdSet(); 
		
		adSet.setCountry("Spain");
		adSet.setGender(1);
		adSet.setInterests("Neymar,Lionel Messi,Milan AC,Mesut Ozil");
		adSet.setMaxAge(30);
		adSet.setMinAge(18);
		adSet.setName("SP_Conversions_jewery_1001");
		adSet.setShowName("Sp_18_30_M");
		
		AlipayAccount alipayAccount = new AlipayAccount();
		alipayAccount.setLoginId("lc34@sina.com");
		alipayAccount.setPayPasswd("991399");
		adSet.setAlipayAccount(alipayAccount);
		
		List<Ad> adList = new ArrayList<Ad>();
		for(int i = 1; i <= 8; i ++){
			Ad ad = new Ad();
			ad.setDescription("description"+i);
			ad.setHeadLine("headLine"+i);
			ad.setImgPath("C:\\facebook\\adset\\1\\"+i+".jpg");
			ad.setLinkUrl("http://www.futbolsp.com/?c="+i);
			
			adList.add(ad);
		}
		
		adSet.setAdList(adList);
		adSet.setSeeMoreUrl("http://www.futbolsp.com/?c=0");
	}
	
	private static void initPage(){
		
		page = new Page();
		
		page.setCountry("Spain");
		
		FilterWords filterWords = new FilterWords();
		filterWords.setCountry("Spain");
		filterWords.setWords("test,invalid,unlawful");    
		page.setFilterWords(filterWords);
		
		page.setLink("http://www.futbolsp.com");
		page.setName("Football-fans");
		page.setPic1("C:\\facebook\\page\\1\\pic1.jpg");
		page.setPic2("C:\\facebook\\page\\1\\pic2.jpg");
		
	}
	
	private static void initUser(){
		user = new User();
		
		user.setLanguage("");
		
		Languages languages = new Languages();
		languages.setSetting("en-US");
		user.setLanguages(languages);
		
//		user.setPixel("31233213123");
		
		user.setUserName("cornelia_gross@gmx.de");
		user.setUserPass("tdica444");
		user.setPageUrl("https://www.facebook.com/Football-fans-369636573479128/");
	
//		pinkthomas@web.de	93.127.151.91
//		user.setCookies("[{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'fr','path':'/','secure':false,'session':true,'storeId':'0','value':'0djQPSWFNoBra8XHA.AWUntgnfE6osZmsIEp-GAdP4U5k.BZjAgu.4F.AAA.0.0.BZjAhE.AWXcs-Hb','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'xs','path':'/','secure':false,'session':true,'storeId':'0','value':'171%3ACX4WceNt_MF8PA%3A2%3A1502349380%3A3743%3A13621','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'c_user','path':'/','secure':false,'session':true,'storeId':'0','value':'1693944513','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'datr','path':'/','secure':false,'session':true,'storeId':'0','value':'OwiMWdd3Km5J4nG6KBb0EFfG','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'sb','path':'/','secure':false,'session':true,'storeId':'0','value':'RAiMWXoTkO1dWlK23aiojR9X','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'pl','path':'/','secure':false,'session':true,'storeId':'0','value':'n','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'lu','path':'/','secure':false,'session':true,'storeId':'0','value':'gA','id':0}]");
//		fam.ryll@web.de 93.127.151.91
//		user.setCookies("[{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'fr','path':'/','secure':false,'session':true,'storeId':'0','value':'0ZGwqLpEsztW7YQTJ.AWWFUrsF1W5cED02keAGQ4rPjD8.BZgNsp.X3.AAA.0.0.BZgNtC.AWUtlHxE','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'xs','path':'/','secure':false,'session':true,'storeId':'0','value':'105%3A3B-8TOw7qZcK9g%3A2%3A1501616962%3A12800%3A13611','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'c_user','path':'/','secure':false,'session':true,'storeId':'0','value':'100006031813135','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'datr','path':'/','secure':false,'session':true,'storeId':'0','value':'NNuAWaeUGDZWOi7uh_n_JDNF','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'sb','path':'/','secure':false,'session':true,'storeId':'0','value':'QtuAWSHoHUoI01y4vdSwImH6','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'pl','path':'/','secure':false,'session':true,'storeId':'0','value':'n','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'lu','path':'/','secure':false,'session':true,'storeId':'0','value':'gA','id':0}]");
//		fischerandreas-1@t-online.de 93.127.151.106
//		user.setCookies("[{'domain':'.facebook.com','expirationDate':1507651777,'hostOnly':false,'httpOnly':true,'name':'fr','path':'/','secure':false,'session':true,'storeId':'0','value':'0o3nZtP0xrj4UFhyK.AWWsMbMu_nh4aeQ3TXQl5r-cj0s.BZgeL4.3y.AAA.0.0.BZgeMK.AWVKenZP','id':0},{'domain':'.facebook.com','expirationDate':1507651777,'hostOnly':false,'httpOnly':true,'name':'xs','path':'/','secure':false,'session':true,'storeId':'0','value':'120%3AI5H6As2NdKC3Nw%3A2%3A1501684490%3A14615%3A13605','id':0},{'domain':'.facebook.com','expirationDate':1507651777,'hostOnly':false,'httpOnly':true,'name':'c_user','path':'/','secure':false,'session':true,'storeId':'0','value':'100002419158277','id':0},{'domain':'.facebook.com','expirationDate':1507651777,'hostOnly':false,'httpOnly':true,'name':'datr','path':'/','secure':false,'session':true,'storeId':'0','value':'AOOBWeP8Viwvg7V6yTb15lqU','id':0},{'domain':'.facebook.com','expirationDate':1507651777,'hostOnly':false,'httpOnly':true,'name':'sb','path':'/','secure':false,'session':true,'storeId':'0','value':'CuOBWasXImbNp4v53mK_-bxX','id':0},{'domain':'.facebook.com','expirationDate':1507651777,'hostOnly':false,'httpOnly':true,'name':'pl','path':'/','secure':false,'session':true,'storeId':'0','value':'n','id':0},{'domain':'.facebook.com','expirationDate':1507651777,'hostOnly':false,'httpOnly':true,'name':'lu','path':'/','secure':false,'session':true,'storeId':'0','value':'gA','id':0}]");
//		cornelia_gross@gmx.de  93.127.151.106
		user.setCookies("[{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'fr','path':'/','secure':false,'session':true,'storeId':'0','value':'0h8HsZKX1ih1oABtK.AWWNxqqhqVXpkpQUW7eepx4lgbA.BZjCrf.QD.AAA.0.0.BZjCru.AWVEVXTG','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'xs','path':'/','secure':false,'session':true,'storeId':'0','value':'184%3AG7s6HzyycZmKbw%3A2%3A1502358254%3A12464%3A13595','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'c_user','path':'/','secure':false,'session':true,'storeId':'0','value':'100002477210966','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'datr','path':'/','secure':false,'session':true,'storeId':'0','value':'5CqMWYftPa6mFIkDp2MZ4EPG','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'sb','path':'/','secure':false,'session':true,'storeId':'0','value':'7iqMWWnh02_LNcJPKeDm2wXG','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'pl','path':'/','secure':false,'session':true,'storeId':'0','value':'n','id':0},{'domain':'.facebook.com','expirationDate':1507648698,'hostOnly':false,'httpOnly':true,'name':'lu','path':'/','secure':false,'session':true,'storeId':'0','value':'gA','id':0}]");
		
		Proxy proxy = new Proxy();
		proxy.setIp("185.173.34.130");
		proxy.setPort1(50054);
		proxy.setUserName("leoliu");
		proxy.setUserPass("ydAtuXysU");
		user.setProxy(proxy);
	}
	
	private static void initActive() {
		active = new Active();
//		active.setImgPath("C:\\\\facebook\\\\page\\\\1\\\\pic1.jpg");
		active.setText("Every day preserve a quite and happy mind");
	}
	
}
