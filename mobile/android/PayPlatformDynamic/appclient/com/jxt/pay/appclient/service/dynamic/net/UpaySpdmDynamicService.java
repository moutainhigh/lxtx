package com.jxt.pay.appclient.service.dynamic.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.StringUtils;
import com.jxt.pay.appclient.utils.XmlUtils;

/**
 * 1、取计费指令 

1.1  请求地址 

http://211.151.129.105/owni/dysms?Fee=&vcid=&CPParameter=&IMSI=&IMEI=&Cli 
entIP=&ua=&video_ua=&sdcid=&iccid= 

参数说明：注意参数名大小写 
参数名称                 是否必  意义 
                         填 
Fee                      是          资费，单位：元 
vcid                     是          我司分配，不做处理 (9011)
CPParameter              是          商户自定义参数，可用于标识渠道，可以重 
                                     复，透传参数 
IMSI                     是          手机卡IMSI 
IMEI                     是          手机IMEI 
ClientIP                 是          客户端用户IP，需真实用户IP 
ua                       是          手机ua，算法见3.1 ua 算法，不传用默认 
                                     值会造成重复   (可以默认xiaomi)
video_ua                 是          手机浏览器的ua，算法见《ua 算法》  (可以默认xiaomi)
sdcidsd                  否          sd 卡id,32 字节， 
                                     例:1501014d414732474313015ebede511f， 
                                     如果取不到这个值可以不传，请不要穿假 
                                     的，能校验 
iccid                    是          sim 卡ICCID 值 

1.2 返回报文(json) 

返回json 报文中包括1-2 条短信内容，以实际接口返回为准，返回几条短信就发 
送几条短信。 

1.2.1 一条短信示例 

{ 
     "ResultCode":"0", 
     "MoSms":[{"SmsCode": 
mvwlan,e98cb39f97052151278e39e7f08c858e,5375","SmsNum":10658423","SmsTy 
pe":"2"}] 
} 



1.2.2 两条短信示例 

{ 
     "ResultCode":"0", 
     "MoSms":[ 
         {"SmsCode":mvwlan,e98cb39f97052151278e39e7f08c858e,5375", 
         "SmsNum":10658423", 
         "SmsType":"2"}, 
     {"SmsCode":0000000086T166/&17668594f00c1208MD0q37!hZSTCMmzkSB6cY6 
BN>4F(==3k7X99}z7O9710d220)Rm101e61914wM000|0H00000MQkw1/#%dc1$s5Y 
JzURmi3gSZa[>", 
         "SmsNum":1065842230", 
         "SmsType":"1"} 
              ] 
} 

1.2.3JSON 节点说明 

ResultCode 状态报告:0 表示获取短信成功,获取短信成功，需要立即发送短信到 
端口号,非0 为获取短信失败，详见3.2 错误代码表; 
SmsCode 为短信内容； 
SmsNum 为短信发送端口； 
SmsType 为发送短信编码，2 发送二进制短信，1 发送字符串普通短信 


3、附录 

3.1 算法示例 

备注：ua 的算法（传递的时候别忘了java.net.URLEncoder.encode ） 
$ua: 
String ua=android.os.Build.BRAND + "_" + android.os.Build.MANUFACTURER + "_" + 
android.os.Build.MODEL; 

$video_ua: 
WebViewwebview = new WebView(context); 
webview.layout(0, 0, 0, 0); 
WebSettings settings = webview.getSettings(); 
String video_ua = settings.getUserAgentString(); 

3.2 错误代码表 

代码编号                                     意义 
 201                                         计费-接收参数有误 
 202                                         计费-imsi 格式有误 
 219                                         计费-imei 格式有误 
 204                                         计费-渠道编号有误 
 205                                         计费-道具编号有误 
 206                                         计费-获取短信失败 
 207                                         计费-单日代码总量达上限或道具未设 
                                             上限 
 * @author leoliu
 *
 */
public class UpaySpdmDynamicService implements IDynamicService{
	private static Logger logger = Logger.getLogger(UpaySpdmDynamicService.class);
	
	private static final String TYPE = "upaySpdm";//优贝视频代码20元
	private static final String PARAM1 = "Fee={Fee}&vcid={vcid}&CPParameter={CPParameter}&IMSI={IMSI}&IMEI={IMEI}&ClientIP={ClientIP}&ua={ua}&video_ua={video_ua}&ib={ib}&iccid={iccid} ";
	private int timeOut = 2;
	
//	private static final Guard guard0 = new Guard("10655477","成功|购买",2880,"1",0);
//	private static final Guard guard1 = new Guard("10655","",960,null,1);
	
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	private Map<String, Long> tryMap = new HashMap<String, Long>();

	@Override
	public String dynamic(Map<String, String> map) {
	
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			try {
				String Fee = map.get("Fee");
				String vcid = map.get("vcid");		
				String CPParameter = map.get("CPParameter");
				String ClientIP = map.get(Constants.IPPARAM);
				String IMSI = map.get("IMSI");
				String IMEI = map.get("IMEI");
				String ua = URLEncoder.encode("Huawei_HUAWEI_HUAWEI MT7-TL00","utf-8");
				String video_ua = URLEncoder.encode("Mozilla/5.0 (Linux; Android 4.4.2; HUAWEI MT7-TL00 Build/HuaweiMT7-TL00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36","utf-8");
				String iccid = map.get("iccid");
				String ib = map.get("ib");
				
				String param = PARAM1.replace("{Fee}", Fee).replace("{vcid}", vcid).replace("{CPParameter}", CPParameter)
						.replace("{IMSI}",IMSI)
						.replace("{IMEI}",IMEI)
						.replace("{ua}",ua)
						.replace("{video_ua}",video_ua).replace("{ClientIP}",ClientIP).replace("{iccid}",iccid).replace("{ib}",ib);
				
				String responseJson = GetData.getData(url,param);
				logger.info("-----"+responseJson);
				if(responseJson != null && responseJson.length() > 0){
					xml = parseXml(map,responseJson);
				}else{
					return DynamicUtils.parseWait(599);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		
		return xml;
	}
	
	private String parseXml(Map<String, String> map,String responseJson){
		
		if(responseJson != null && responseJson.length() > 0){
			try{
				int pos = responseJson.indexOf("{");
				
				if(pos > 0){
					responseJson = responseJson.substring(pos);
				}
				
				JSONObject jo = new JSONObject(responseJson);
				String ResultCode = jo.getString("ResultCode");
				logger.info("parse second result code : "+ResultCode);
				
				if("0".equals(ResultCode)){
					
					
					//String MoSms = jo.getString("MoSms");
					
					JSONArray ja = jo.getJSONArray("MoSms");
					
					List<Sms> smsList = new ArrayList<Sms>();
					
					for(int i = 0 ; i < ja.length() ; i ++){
						JSONObject subJo = ja.getJSONObject(i);
						
						String SmsCode = subJo.getString("SmsCode");
						String SmsNum = subJo.getString("SmsNum");
						String SmsType = subJo.getString("SmsType");
						
						Sms sms = new Sms();
						
						sms.setSmsDest(SmsNum);
						
						if("1".equals(SmsType)){
							
						}else if("2".equals(SmsType)){
							sms.setSendType("2");
						}
						
						sms.setSmsContent(SmsCode);
						sms.setSuccessTimeOut(2);
						
						smsList.add(sms);
					}
	
					return XstreamHelper.toXml(smsList).toString();
					
				}else{
					return DynamicUtils.parseError(ResultCode);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return null;

				

				
	}


	
	public static void main(String[] args) throws Exception{
		
//		http://cc.channel.3gshow.cn/common/req.ashx&type=zztXly&imsi=460026620811823&imei=354026050655833&mobileId=9872554&channel=100101a049397252&cid=151&pid=1113&payCodeId=5255
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url","http://211.151.129.105/owni/dysms");
		map.put("type", TYPE);
		map.put("IMEI","867451025555753");
		map.put("IMSI","460078010952058");
		map.put("Fee","20");
		map.put("vcid","9037");
		map.put("CPParameter","LX13A301");
		map.put("iccid","898600f2261578514218");
		map.put("ib","1");
		map.put(Constants.IPPARAM,"58.212.108.155");
		
		logger.info(new UpaySpdmDynamicService().dynamic(map));

	}
}
