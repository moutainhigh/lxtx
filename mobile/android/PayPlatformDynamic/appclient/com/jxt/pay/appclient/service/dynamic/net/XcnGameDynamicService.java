package com.jxt.pay.appclient.service.dynamic.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jxt.pay.appclient.service.dynamic.pojo.Guard;
import com.jxt.pay.appclient.service.dynamic.pojo.Sms;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.GetData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;

/**
 * 	http://$IP:$PORT/o/mgreqapi/$sid/?imei=$imei&imsi=$imsi&item_id=$payCode&item_price=$price&channel_id=$channel&cpparam=$cpparam&sdcid=$sdcid&start_time=$start_time&iccid=$iccid&ip=$ip
 * @author leoliu
 *
 */
public class XcnGameDynamicService implements IDynamicService{

	private static final String TYPE = "xcnGame";
	
	private static final Integer DEFAULTSUCCESSTIMEOUT = 2;
	
	private static final String REQUESTMODEL_DMSDK = "imei={imei}&imsi={imsi}&item_id={payCode}&item_price={price}&channel_id={channel_id}&cpparam={cpparam}&sdcid=&start_time={start_time}&iccid={iccid}&ip={ip}";
	private static final String REQUESTMODEL_MUSIC = "imei={imei}&imsi={imsi}&music_id={payCode}&item_price={price}&channel_id={channel_id}&cpparam={cpparam}&sdcid=&start_time={start_time}&iccid={iccid}&ip={ip}&music_type={music_type}";
	private static final String REQUESTMODEL_VIDEO = "imei={imei}&imsi={imsi}&node_id={nodeId}&product_id={productId}&content_id={contentId}&item_price={price}&cpparam={cpparam}&ua={ua}&video_ua={videoUa}&sdcid=&start_time={start_time}&huawei_id={huawe_id}&channel_id={channel}&public_key={public_key}&video_app_id={video_app_id}&video_app_key={video_app_key}&iccid={iccid}&ip={ip}";
	
	private static final String OPTYPE_DMSDK = "dmsdk";
	private static final String OPTYPE_MUSIC = "music";
	private static final String OPTYPE_VIDEO = "video";
	
	private static Logger logger = Logger.getLogger(XcnGameDynamicService.class);
	
	@Override
	public String getType() {
		return TYPE;
	}

	private int timeOut = 2;
	
	@Override
	public String dynamic(Map<String, String> map) {
		
		String opType = map.get("opType");
		
		if(OPTYPE_DMSDK.equals(opType)){
			return dynamicDmSdk(map);
		}else if(OPTYPE_MUSIC.equals(opType)){
			return dynamicMusic(map);
		}else if(OPTYPE_VIDEO.equals(opType)){
			return dynamicVideo(map);
		}
	
		return null;
	}
	
	private String dynamicVideo(Map<String,String> map){
		
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String payCode = map.get("payCode");																																																																																		
			String price = map.get("price");
			String channel_id = map.get("channel_id");
			String iccid = map.get("iccid");
			String ip = map.get(Constants.IPPARAM);
			long start_time = System.currentTimeMillis();
			String music_type = map.get("music_type");
			
			String param = REQUESTMODEL_VIDEO.replace("{imei}",imei);
			param = param.replace("{imsi}",imsi);
			param = param.replace("{payCode}",payCode);
			param = param.replace("{price}",price);
			param = param.replace("{channel_id}", channel_id);
			param = param.replace("{cpparam}", channel);
			param = param.replace("{start_time}",start_time+"");
			param = param.replace("{iccid}",iccid);
			param = param.replace("{ip}",ip);
			param = param.replace("{music_type}", music_type);
			
			String responseXml = GetData.getData(url, param);
			
			if(responseXml != null && responseXml.length() > 0){
				StringBuffer sb = new StringBuffer();
				
				String[] arr = new String[]{"music","migu"};
				
				for(String key : arr){
					String subXml = SingleXmlUtil.getNodeValue(responseXml, key);
					
					if(subXml != null && subXml.length() > 0){
						String sms_num = SingleXmlUtil.getNodeValue(subXml, "sms_num");
						
						String smsContent = SingleXmlUtil.getNodeValue(subXml, "sms");
						try {
							smsContent = CommonUtil.base64Decode(smsContent);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						Sms sms = new Sms();
						sms.setSmsContent(smsContent);
						sms.setSmsDest(sms_num);
						sms.setSuccessTimeOut(2);
						
						sb.append(XstreamHelper.toXml(sms));
					}
				}
				
				return sb.toString();
			}

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}

	private String dynamicMusic(Map<String,String> map){
		
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String payCode = map.get("payCode");																																																																																		
			String price = map.get("price");
			String channel_id = map.get("channel_id");
			String iccid = map.get("iccid");
			String ip = map.get(Constants.IPPARAM);
			long start_time = System.currentTimeMillis();
			String music_type = map.get("music_type");
			
			String param = REQUESTMODEL_MUSIC.replace("{imei}",imei);
			param = param.replace("{imsi}",imsi);
			param = param.replace("{payCode}",payCode);
			param = param.replace("{price}",price);
			param = param.replace("{channel_id}", channel_id);
			param = param.replace("{cpparam}", channel);
			param = param.replace("{start_time}",start_time+"");
			param = param.replace("{iccid}",iccid);
			param = param.replace("{ip}",ip);
			param = param.replace("{music_type}", music_type);
			
			String responseXml = GetData.getData(url, param);
			
			logger.info("responseXml : "+responseXml);
			
			if(responseXml != null && responseXml.length() > 0){
				StringBuffer sb = new StringBuffer();
				
				String[] arr = new String[]{"music","migu"};
				
				for(String key : arr){
					String subXml = SingleXmlUtil.getNodeValue(responseXml, key);
					
					if(subXml != null && subXml.length() > 0){
						String sms_num = SingleXmlUtil.getNodeValue(subXml, "sms_num");
						
						String smsContent = SingleXmlUtil.getNodeValue(subXml, "sms");
						try {
							smsContent = CommonUtil.base64Decode(smsContent);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						Sms sms = new Sms();
						sms.setSmsContent(smsContent);
						sms.setSmsDest(sms_num);
						sms.setSuccessTimeOut(2);
						
						sb.append(XstreamHelper.toXml(sms));
					}
				}
				
				return sb.toString();
			}

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	private String dynamicDmSdk(Map<String,String> map){
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String channel = map.get("channel");
			
			String imei = map.get("imei");
			String imsi = map.get("imsi");
			String payCode = map.get("payCode");																																																																																		
			String price = map.get("price");
			String channel_id = map.get("channel_id");
			String iccid = map.get("iccid");
			String ip = map.get(Constants.IPPARAM);
			long start_time = System.currentTimeMillis();
			
			String param = REQUESTMODEL_DMSDK.replace("{imei}",imei);
			param = param.replace("{imsi}",imsi);
			param = param.replace("{payCode}",payCode);
			param = param.replace("{price}",price);
			param = param.replace("{channel_id}", channel_id);
			param = param.replace("{cpparam}", channel);
			param = param.replace("{start_time}",start_time+"");
			param = param.replace("{iccid}",iccid);
			param = param.replace("{ip}",ip);
			
			String responseXml = GetData.getData(url, param);
			
			if(responseXml != null && responseXml.length() > 0){
				String sms_num = SingleXmlUtil.getNodeValue(responseXml, "sms_num");
				
				String smsContent = SingleXmlUtil.getNodeValue(responseXml, "sms");
				try {
					smsContent = CommonUtil.base64Decode(smsContent);
					
					System.out.println(smsContent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Sms sms = new Sms();
				sms.setSmsContent(smsContent);
				sms.setSmsDest(sms_num);
				sms.setSuccessTimeOut(2);
				
				return XstreamHelper.toXml(sms).toString();
			}

			if(xml == null || xml.length() == 0){
				xml = DynamicUtils.parseWait(10,map);
			}
		}
		
		return xml;
	}
	
	public static void main(String[] args){
//		testDmSdk();
		testMusic();
	}
	
	private static void testMusic(){
		String url = "http://115.159.74.129:8000/o/mgreqapi/58d2e66845a89464e14cfb7cdcc0dee32be304ad/";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "lxtxkj10B101a876535236");
		map.put("imsi","460002844968434");
		map.put("imei","869167025556314");
		
		map.put("payCode","600967020000006196");
		map.put("price","1000");
		map.put("channel_id","014A1HN");
		map.put("type","xcnGame");
		map.put("opType",OPTYPE_MUSIC);
		map.put("music_type","2");
		map.put("iccid","89860099261486241579");
		map.put(Constants.IPPARAM,"221.220.249.176");
		
		System.out.println(new XcnGameDynamicService().dynamic(map));
	}
	
	
	private static void testDmSdk(){
		String url = "http://115.159.74.129:8000/o/mgreqapi/14a20d1388a6ba752de02983237976a40b2016d4/";
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("url", url);
		map.put("channel", "lxtxkj10B101a876535236");
		map.put("imsi","460001123143655");
		map.put("imei","867376023133651");
		
		map.put("payCode","800000000617");
		map.put("price","200");
		map.put("channel_id","800001467");
		map.put("type","xcnGame");
		map.put("opType",OPTYPE_DMSDK);
		map.put("iccid","898600810115F0387588");
		map.put(Constants.IPPARAM,"221.220.249.176");
		
		System.out.println(new XcnGameDynamicService().dynamic(map));
	}

}
