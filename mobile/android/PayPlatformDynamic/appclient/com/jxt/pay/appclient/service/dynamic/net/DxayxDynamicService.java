package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;

public class DxayxDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(DxayxDynamicService.class);
	
	private static final String TYPE = "dxayx";
	
	private static final String PAY_CODE_NAME = "首页礼";
	
	private static final String PARAMS = "cp_id={cp_id}&imsi={imsi}&imei={imei}&client_ip={client_ip}&phone={phone}&price={price}&custom_param={custom_param}&hsman=&hstype=&pay_code_name={pay_code_name}&app_name={app_name}&cp_channel_id={cp_channel_id}&biz_type={biz_type}&os_ver=&sign={sign}";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		try{
			String url = map.get("url");
			String cp_id = map.get("cp_id");
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String biz_type = "";
			String client_ip = map.get(Constants.IPPARAM);
			String phone = StringUtils.defaultString(map.get("phone"));
			String price = map.get("price");
			String custom_param = map.get("custom_param");
			String cp_channel_id = map.get("cp_channel_id");
			String app_name = getAppName(cp_channel_id);
			String pay_code_name = PAY_CODE_NAME;
			
			String key = map.get("key");
			
			String sign0 = cp_id+imsi+imei+client_ip+phone+price+custom_param+pay_code_name+app_name+cp_channel_id+biz_type+key;
			String sign = MD5Encrypt.MD5Encode(sign0);
			
			logger.info("sign0:"+sign0+";sign1:"+sign);
			
			String params = PARAMS.replace("{cp_id}",cp_id ).replace("{imsi}",imsi ).replace("{imei}", imei).replace("{biz_type}",biz_type )
					.replace("{client_ip}",client_ip ).replace("{phone}", phone).replace("{price}", price)
					.replace("{custom_param}", custom_param).replace("{app_name}",URLEncoder.encode(app_name,"utf-8") )
					.replace("{pay_code_name}",URLEncoder.encode(PAY_CODE_NAME,"utf-8") )
					.replace("{cp_channel_id}",cp_channel_id ).replace("{sign}", sign);
			
			String responseJson = GetData.getData(url,params);
			logger.info("responseJson2:"+responseJson);
			if(responseJson != null && responseJson.length() > 0){
				
				JSONObject jo = new JSONObject(responseJson);
				
				int result_code = jo.getInt("result_code");
				
				if(result_code == 1){
						
					JSONObject jo1 = jo.getJSONObject("complete_pay_code");
					
					String channel_order = jo1.getString("channel_order");
					String channel_port = jo1.getString("channel_port");
					
					Sms sms = new Sms();
					sms.setSmsContent(channel_order);
					sms.setSmsDest(channel_port);
					sms.setSuccessTimeOut(2);
					
					return XstreamHelper.toXml(sms).toString();
				}else{
					return DynamicUtils.parseError(result_code+"");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

	public static void main(String[] args){
		test1();
	}
	
	private static void test3(){
		
		try {
			String aa = "不要碰我";
			String bb = URLEncoder.encode(aa, "utf-8");
			String cc = URLDecoder.decode(bb,"utf-8");
			String dd = URLEncoder.encode(cc, "utf-8");
			
			System.out.println(bb);
			System.out.println(cc);
			System.out.println(dd);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void test2(){
		String a = "1010546003072295414699000562510327182.133.178.2192000zqkj道具游戏13G43Pd6e11249c9b86723cd033355ce3ece32";
//		sign1:2F92644CD104231F2ADFC70A899AAFB5
		a = "10082460030745987711A0000038A49A0110.170.80.1152000tw首页礼fkdwc13W20Zdf8a179b15ce9d751ceac3cdbc5b83bd";
		//7D3E2122A7222B072E60396C86F67CAB
//		a = "道具游戏";
		
		System.out.println(MD5Encrypt.MD5Encode(a));
		
		try {
//			System.out.println(URLEncoder.encode("游戏", "utf-8"));
//			System.out.println(URLEncoder.encode("道具", "utf-8"));
//			
//			System.out.println(URLDecoder.decode("%E6%B8%B8%E6%88%8F","utf-8"));
			
//			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	private static Map<String, String> codeMap = new HashMap<String, String>();
	
	static{
		codeMap.put("131", "不要碰我");
		codeMap.put("132", "全民酷跑");
		codeMap.put("134", "再来一炮");
		codeMap.put("137", "战机总动员");
		codeMap.put("139", "野蛮小鸟");
		codeMap.put("13A", "星星消灭者3");
		codeMap.put("13C", "太空酷跑");
		codeMap.put("13F", "农场消消乐");
		codeMap.put("13G", "疯狂消消乐");
		codeMap.put("13B", "无孔不入");
		codeMap.put("133", "霹雳战机");
		codeMap.put("13K", "无孔不入");
		codeMap.put("13U", "动物消消乐");
		codeMap.put("13I", "天才消消乐");
		codeMap.put("13V", "疯狂大冒险");
		codeMap.put("13X", "课堂大冒险");
		codeMap.put("13W", "疯狂动物城");
		codeMap.put("13Y", "愤怒的小鸟");
		codeMap.put("13Z", "一飞冲天");
		codeMap.put("135", "龙珠大闯关");
		
	}
	
	private String getAppName(String channelCode){
		String code = channelCode.substring(0,3);
		
		String appName = codeMap.get(code);
		
		if(appName == null){
			return "水果萌萌哒";
		}
		
		return appName;
	}
	
	private static void test1(){
//		http://payreq.i9188.net:8400/spApi/dxApiReq.do?
//		cp_id=10105&
//		imsi=460036071249319&
//		imei=A10000396D95AD&
//		client_ip=171.114.190.10&
//		phone=&
//		price=2000
//		&custom_param=zqkj
//		&hsman=&hstype=&
//		pay_code_name=%E9%81%93%E5%85%B7&
//		app_name=%E6%B8%B8%E6%88%8F&
//		cp_channel_id=13W301&
//		biz_type=&os_ver=&
//		sign=9D2D5591EA54FA314D5AB0EEBF7C8D17

		Map<String, String> map = new HashMap<String, String>();
		
		map.put("type","dxayx");
		map.put("url", "http://payreq.i9188.net:8400/spApi/dxApiReq.do");
		map.put("cp_id", "10105");
		map.put("imsi", "460036071249319");
		map.put("imei", "A10000396D95AD");
		map.put(Constants.IPPARAM, "171.114.190.10");
		map.put("custom_param", "zqkj");
		map.put("phone", null);
		map.put("price", "2000");
		map.put("cp_channel_id", "13W301");
		map.put("key", "d6e11249c9b86723cd033355ce3ece32");
		
		System.out.println(new DxayxDynamicService().dynamic(map));
	}
	
	static class MD5Encrypt {

	    private Logger log=Logger.getLogger(MD5Encrypt.class);
	    
	    private final static String[] hexDigits = {
	      "0", "1", "2", "3", "4", "5", "6", "7",
	      "8", "9", "a", "b", "c", "d", "e", "f"};

	  /**
	   * 转换字节数组�?16进制字串
	   * @param b 字节数组
	   * @return 16进制字串
	   */
	  public static String byteArrayToString(byte[] b) {
	    StringBuffer resultSb = new StringBuffer();
	    for (int i = 0; i < b.length; i++) {
	      resultSb.append(byteToHexString(b[i]));//若使用本函数转换则可得到加密结果�?16进制表示，即数字字母混合的形�?
	      //resultSb.append(byteToNumString(b[i]));//使用本函数则返回加密结果�?10进制数字字串，即全数字形�?
	    }
	    return resultSb.toString();
	  }

	  private static String byteToNumString(byte b) {

	    int _b = b;
	    if (_b < 0) {
	      _b = 256 + _b;
	    }

	    return String.valueOf(_b);
	  }

	  private static String byteToHexString(byte b) {
	    int n = b;
	    if (n < 0) {
	      n = 256 + n;
	    }
	    int d1 = n / 16;
	    int d2 = n % 16;
	    return hexDigits[d1] + hexDigits[d2];
	  }

	  public static String MD5Encode(String origin) {
	    String resultString = null;

	    try {
	      resultString = new String(origin);
	      MessageDigest md = MessageDigest.getInstance("MD5");
	      resultString =
	byteArrayToString(md.digest(resultString.getBytes("utf-8")));
	      resultString=resultString.toUpperCase();
	    }
	    catch (Exception ex) {

	    }
	    return resultString;
	  }

	  public static void main(String[] args){
//		  test1();
		  System.out.println(MD5Encode("游戏"));
	  }
	}
	
}
