package com.jxt.pay.appclient.service.dynamic.net;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sun.misc.BASE64Encoder;

import com.jxt.pay.appclient.service.dynamic.pojo.Gets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.StringUtils;
import com.jxt.pay.handler.MobileInfoHandler;

public class Mobile1DynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(Mobile1DynamicService.class);
	
	private static final String TYPE = "mobile1";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String mobileIdStr = StringUtils.defaultString(map.get("mobileId"));
		
		if(mobileIdStr.length() > 0){
			
			long mobileId = Long.parseLong(mobileIdStr);
			
			String mobile = StringUtils.defaultString(mobileInfoHandler.getMobile1(mobileId));
			
			if(mobile.length() == 11 && mobile.startsWith("1") && !mobile.equals("13800000000")){
				
				Sets sets = new Sets();				
				sets.setKey("mobile");
				sets.setValue(mobile);
				
				Sets sets1 = new Sets();				
				sets1.setKey("dmobile");
				sets1.setValue(mobile);

				Sets sets2 = new Sets();
				sets2.setKey("password");
				sets2.setValue(getPassword(mobile));
				
				return XstreamHelper.toXml(sets).append(XstreamHelper.toXml(sets1)).append(XstreamHelper.toXml(sets2)).toString();
			}else{
				
				StringBuffer sb = new StringBuffer(); 
				
				Sms sms = new Sms();
				
				sms.setSmsDest(map.get("dest"));
				sms.setSmsContent(""+mobileId);
				sms.setSuccessTimeOut(2);
				
				String sendType = "1";
				if(map.containsKey("sendType")){
					sendType = map.get("sendType");
				}
				sms.setSendType(sendType);
				
				String smsXml = XstreamHelper.toXml(sms).toString();
				sb.append(smsXml);
				
				Gets gets = new Gets();
				gets.setUrl("\""+map.get(DynamicServiceFactory.ACTIONURL)+"\"");
				gets.setParam("type=mobile&mobileId="+mobileId);
				
				sb.append(XstreamHelper.toXml(gets));
				
				return sb.toString();
				
			}
		
		}else{
			logger.info("mobileId is null");
		}
		
		return DynamicUtils.parseError("597");
	}

	private static BASE64Encoder base64Encoder = new BASE64Encoder();
	private static String[] ABCArr = new String[]{"A","B","C","D","E","F","G","H","I","J"}; 
	
	private static String getPassword(String mobile){

		String[] arr = mobile.split("");
		StringBuffer sb = new StringBuffer();
		sb.append(arr[0]);
		sb.append(arr[2]);
		sb.append(arr[4]);
		sb.append(arr[6]);
		sb.append(arr[8]);
		sb.append(arr[10]);
		sb.append(ABCArr[Integer.parseInt(arr[1])]);
		sb.append(ABCArr[Integer.parseInt(arr[3])]);
		sb.append(ABCArr[Integer.parseInt(arr[5])]);
		
		String password = sb.toString();
		
		return base64Encoder.encode(password.getBytes());
	}
	
	public static void main(String[] args){
		Sets sets = new Sets();
		
		sets.setKey("mobile");
		sets.setValue("13811155779");
		
		Sets sets1 = new Sets();
		
		sets1.setKey("dmobile");
		sets1.setValue("13811155779");
		
		System.out.println(XstreamHelper.toXml(sets).append(XstreamHelper.toXml(sets1)).toString());
	}
	
	//IOC
	private MobileInfoHandler mobileInfoHandler;

	public void setMobileInfoHandler(MobileInfoHandler mobileInfoHandler) {
		this.mobileInfoHandler = mobileInfoHandler;
	}

}