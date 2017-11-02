package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;
import com.jxt.pay.handler.BlackMobileHandler;
import com.jxt.pay.handler.MobileInfoHandler;
import com.jxt.pay.handler.PhoneNoRegionHandler;
import com.jxt.pay.helper.BlackMobileHelper;
import com.jxt.pay.pojo.MobileInfo;
import com.jxt.pay.pojo.PhoneNoRegion;

/**
 * 对接手游世纪手机钱包
 * @author leoliu
 *
 */
public class SjdyJsoneDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(SjdyJsoneDynamicService.class);
	
	private static final String FIRSTREQUESTMODEL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><gameId>{gameId}</gameId><imei>{imei}</imei><imsi>{imsi}</imsi><mobile>{mobile}</mobile><ip>{ip}</ip><extData>{extData}</extData></request>";
	private static final String SECONDREQUESTMODEL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><code>{code}</code><linkId>{linkId}</linkId></request>";
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String TYPE = "sjdySjqb";
	
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
		
		String url = map.get("url");
		String xml = null;
		
		if(url != null && url.length() > 0){
				
			String imsi = StringUtils.defaultString(map.get("imsi"));
			String imei = StringUtils.defaultString(map.get("imei"));
			String gameId = map.get("gameId");
			String mobile = StringUtils.defaultString(map.get("mobile"));
			String ip = map.get(Constants.IPPARAM);
//			String province = map.get("province");
			String channel = map.get("channel");
			
//			String provinceName = getProvinceName(province);
			
			String script = FIRSTREQUESTMODEL.replace("{imsi}", imsi);
			script = script.replace("{imei}", imei);
			script = script.replace("{gameId}", gameId);
			script = script.replace("{mobile}", mobile);
			script = script.replace("{ip}", ip);
			script = script.replace("{extData}",channel);
//			script = script.replace("{province}",provinceName);
		
			logger.info("firstDynamic : "+script);
			
			String responseXml = new PostData().PostData(script.getBytes(), url);
			
			xml = parseFirst(responseXml);
			
		}
		
		return xml;
	}

	private static Map<String, String> provinceMap = new HashMap<String, String>();
	
	static{
		provinceMap.put("8611","北京");
		provinceMap.put("8612","天津");
		provinceMap.put("8613","河北");
		provinceMap.put("8614","山西");
		provinceMap.put("8615","内蒙古");
		provinceMap.put("8621","辽宁");
		provinceMap.put("8622","吉林");
		provinceMap.put("8623","黑龙江");
		provinceMap.put("8631","上海");
		provinceMap.put("8632","江苏");
		provinceMap.put("8633","浙江");
		provinceMap.put("8634","安徽");
		provinceMap.put("8635","福建");
		provinceMap.put("8636","江西");
		provinceMap.put("8637","山东");
		provinceMap.put("8641","河南");
		provinceMap.put("8642","湖北");
		provinceMap.put("8643","湖南");
		provinceMap.put("8644","广东");
		provinceMap.put("8645","广西");
		provinceMap.put("8646","海南");
		provinceMap.put("8650","重庆");
		provinceMap.put("8651","四川");
		provinceMap.put("8652","贵州");
		provinceMap.put("8653","云南");
		provinceMap.put("8654","西藏");
		provinceMap.put("8661","陕西");
		provinceMap.put("8662","甘肃");
		provinceMap.put("8663","青海");
		provinceMap.put("8664","宁夏");
		provinceMap.put("8665","新疆");		
	}
	
	private String getProvinceName(String province){
		try {
			return URLEncoder.encode(provinceMap.get(province), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private String parseFirst(String responseXml){
		logger.info("parseFirst : "+responseXml);
		if(responseXml != null && responseXml.length() > 0){
			String state = SingleXmlUtil.getNodeValue(responseXml, "state");
			
			StringBuffer sb = new StringBuffer();
			
			if("YZM".equals(state) || "0".equals(state)){
				
				String linkId = SingleXmlUtil.getNodeValue(responseXml, "linkId");
				
				Sets sets = new Sets();
				sets.setKey("linkId");
				sets.setValue(linkId);
				
				Sets sets1 = new Sets();
				sets1.setKey("state");
				sets1.setValue(state);
				
				String spNumber1 = SingleXmlUtil.getNodeValue(responseXml, "spNumber1");
				String moContent1 = SingleXmlUtil.getNodeValue(responseXml, "moContent1");
				
				Sms sms = new Sms();
				sms.setSmsDest(spNumber1);
				sms.setSmsContent(moContent1);
				sms.setSuccessTimeOut(2);
				
				sb.append(XstreamHelper.toXml(sets)).append(XstreamHelper.toXml(sets1)).append(XstreamHelper.toXml(sms));
				
				return sb.toString();
			}else{
				return DynamicUtils.parseError(state);
			}
		}
		
		return null;
	}
	
	private String secondDynamic(Map<String,String> map){
		String xml = null;
		String url = map.get("url");
		
		String code = map.get("code");
		String linkId = map.get("linkId");
		
		if(url != null && url.length() > 0){
						
			String script = SECONDREQUESTMODEL.replace("{code}", code).replace("{linkId}", linkId);
			
			logger.info("secondDynamic : "+script);
			
			String responseXml =  new PostData().PostData(script.getBytes(), url);
		
			xml = parseSecond(map,responseXml);
		}
		
		return xml;
	}
		
	private String parseSecond(Map<String,String> map,String responseXml){
		
		logger.info("parse second : "+responseXml);
		
		if(responseXml != null && responseXml.length() > 0){
			String state = SingleXmlUtil.getNodeValue(responseXml, "state");
			
			if("0".equals(state)){//等待
				Sets sets = new Sets();
				sets.setKey("succ");
				sets.setValue("1");
				return XstreamHelper.toXml(sets).toString();
			}else{
				return DynamicUtils.parseError(state);
			}
		}
		
		return null;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	public static void main(String[] args){

		
		String url = "http://121.52.208.188:3001/umpay";
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("url",url);
		map.put("theNo","1");
		map.put("gameId","15756");
		map.put("imei","867451025555753");
		map.put("imsi","460028619944129");
		map.put(Constants.IPPARAM,"221.217.182.122");
//		map.put("province","8632");
		map.put("mobile","15861991830");
		map.put("channel","13B101a012377");
		map.put("type", "sjdySjqb");
		
		String result = new SjdyJsoneDynamicService().dynamic(map);
		
		System.out.println("result : "+result);
		
//		System.out.println(validCitys.indexOf("861601".substring(0,4)));
	}
}
