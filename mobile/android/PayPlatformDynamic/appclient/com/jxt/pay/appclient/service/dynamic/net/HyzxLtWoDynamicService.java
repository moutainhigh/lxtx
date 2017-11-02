package com.jxt.pay.appclient.service.dynamic.net;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.qlzf.commons.helper.MD5Encrypt;

/**
 * @author leoliu
 *
 */
public class HyzxLtWoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(HyzxLtWoDynamicService.class);
	
	private static final String TYPE = "hyzxLtWo";//华易掌信联通小沃
	
	private static final String PARAM1 = "type={type}&siteid={siteid}&codeid={codeid}&serial={serial}&ip={ip}&pr={pr}&imsi={imsi}&imei={imei}&phone={phone}";
	
	
	private static final String RESULTCODE_SUCC = "0";
	
	
	
	@Override
	public String getType() {
		return TYPE;
	}
	private Map<String, Integer> map1 = new HashMap<String, Integer>();
	@Override
	/**
	 * url app pid money key 
	 */
	public String dynamic(Map<String, String> map) {
		
		
		
		String url = map.get("url");
		
		String xml = null;
		String channel = map.get("channel");
		
		if(url != null && url.length() > 0){
			String type = map.get("Ttype");
			String siteid = map.get("siteid");
			String codeid = map.get("codeid");
			String serial = map.get("serial");
			String phone = StringUtils.defaultString(map.get("mobile"));
			String imsi = map.get("imsi");
			String imei = map.get("imei");
			String ip = map.get(Constants.IPPARAM);
			String pr = getProvince(map.get("pr"));
			
			String param = PARAM1.replace("{type}", type).replace("{siteid}",siteid)
					.replace("{codeid}",codeid).replace("{imsi}",imsi)
					.replace("{imei}",imei).replace("{serial}",serial).replace("{phone}",phone)
					.replace("{ip}",ip).replace("{pr}",pr);
			
			String responseJson = GetData.getData(url, param);
			logger.info("-----"+responseJson);
			xml = parseFirst(map,responseJson);

			if(xml == null){
				Integer cnt = map1.get(channel);
				
				if(cnt == null){
					cnt = 0;
				}
				
				if(cnt >= 3){
					map1.remove(channel);
					xml = DynamicUtils.parseError("598");
				}else{
					cnt ++;
					map1.put(channel, cnt);
					
					xml = DynamicUtils.parseWait(10,map);//获取失败
				}
			}else{
				map1.remove(channel);
			}
			
		}
		
		return xml;
	}
	
	private String parseFirst(Map<String,String> map,String responseJson){
		
		
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				int pos = responseJson.indexOf("{");
			
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				responseJson=responseJson.replaceAll("\r","").replace("\n","");
				logger.info("JSON:"+responseJson);
				JSONObject jo = new JSONObject(responseJson);
				
				if(jo.has("hRet")){
					String hRet = jo.getString("hRet");
					
					logger.info("parse first result code : "+hRet);
					
					if(RESULTCODE_SUCC.equals(hRet)){
						
						
						String Login = jo.getString("Login");
						JSONObject jo1 = new JSONObject(Login);
						
						
						
						String num = jo1.getString("num");
						String smsContent = jo1.getString("sms");
						
						Sms sms = new Sms();
						sms.setSmsDest(num);
						sms.setSmsContent(smsContent);
						sms.setSendType("2");
						
						return XstreamHelper.toXml(sms).toString();
					}
						
						
						return DynamicUtils.parseError(hRet);
					//}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;
	}
		
	
	private static String getProvince(String srcPro){
		int srcProI = Integer.parseInt(srcPro);
		
		switch(srcProI){
		case 8611:
			return "4";
		case 8612:
			return "7";
		case 8613:
			return "17";
		case 8614:
			return "11";
		case 8615:
			return "3";
		case 8621:
			return "27";
		case 8622:
			return "5";
		case 8623:
			return "31";
		case 8631:
			return "1";
		case 8632:
			return "15";
		case 8633:
			return "19";
		case 8634:
			return "9";
		case 8635:
			return "24";
		case 8636:
			return "16";
		case 8637:
			return "10";
		case 8641:
			return "18";
		case 8642:
			return "17";
		case 8643:
			return "22";
		case 8644:
			return "12";
		case 8645:
			return "13";
		case 8646:
			return "20";
		case 8650:
			return "28";
		case 8651:
			return "6";
		case 8652:
			return "26";
		case 8653:
			return "2";
		case 8654:
			return "25";
		case 8661:
			return "29";
		case 8662:
			return "23";
		case 8663:
			return "30";
		case 8664:
			return "8";
		case 8665:
			return "14";
		}
		
		return "4";
	}
	
	public static String string2Json(String s) {        
        StringBuffer sb = new StringBuffer();        
        for (int i=0; i<s.length(); i++) {  
            char c = s.charAt(i);    
             switch (c){  
             case '\"':        
                 sb.append("\\\"");        
                 break;        
             case '\\':        
                 sb.append("\\\\");        
                 break;        
             case '/':        
                 sb.append("\\/");        
                 break;        
             case '\b':        
                 sb.append("\\b");        
                 break;        
             case '\f':        
                 sb.append("\\f");        
                 break;        
             case '\n':        
                 sb.append("\\n");        
                 break;        
             case '\r':        
                 sb.append("\\r");        
                 break;        
             case '\t':        
                 sb.append("\\t");        
                 break;        
             default:        
                 sb.append(c);     
             }  
         }      
        return sb.toString();     
        }

	public static void main(String[] args){
//		
		Map<String, String> map = new HashMap<String, String>();	
		
		
		map.put("url", "http://ivas.iizhifu.com/init.php");
		map.put("type",TYPE);
		map.put("Ttype", "124");
		map.put("siteid","211");
		map.put("codeid","1233001");
		map.put("mobile","18801032292");
		map.put("serial","MMZX13A301");
		map.put("pr","8611");
		map.put(Constants.IPPARAM,"114.240.89.171");
		map.put("imsi","460078010952058");
		map.put("imei", "867451025555753");
		map.put("channel","13X265a123844860");
		
		//test1();
		logger.info(new HyzxLtWoDynamicService().dynamic(map));
	}
	
	
	private static void test1(){
		
		String responseJson = "{\"Login\":{\"sms\":\"7wSxGzp0e6OicT+YQjt3X+oY/zyFbhGykBHdFfTVe8TRtiWfZ7fkdohGgCBQLDsPWbUL81BqzerJK7Wyiaxn6yjDMuotqI19DFidpCyMiU9Y="
				+ "\",\"num\":\"10655198018\"},\"hRet\":\"0\",\"type\":\"data\"}";
		String responseJson1 ="{\"Login\":{\"sms\":\"7wSxGzp0e6OicT+YQjt3X+qXSje61ko9x9Jap+jhFVu9wuFO+4Ey0jldPmAtZtANb80IXaONi9RlJ8jE/l599AltDpbOrXKvPgx595R/hZgg=\",\"num\":\"10655198018\"},\"hRet\":\"0\",\"type\":\"data\"}";
		
//		String a =string2Json(responseJson);
		
		logger.info("JSON1 : "+responseJson);
		try {
			JSONObject jo = new JSONObject(responseJson);
			
			logger.info("JSON2 : "+jo);
			
			String hRet = jo.getString("hRet");
			
			logger.info("JSON3 : "+hRet);
			
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
}
