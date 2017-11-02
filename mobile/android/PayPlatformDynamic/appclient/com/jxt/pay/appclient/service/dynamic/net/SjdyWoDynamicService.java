package com.jxt.pay.appclient.service.dynamic.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;
import com.jxt.pay.appclient.utils.PostData;
import com.jxt.pay.appclient.utils.SingleXmlUtil;

/**
 * 对接南京网游
 * @author leoliu
 *
 */
public class SjdyWoDynamicService implements IDynamicService{

	private static Logger logger = Logger.getLogger(SjdyWoDynamicService.class);
	
	private static final String FIRSTREQUESTMODEL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><gameId>{gameId}</gameId><mobile>{mobile}</mobile><extData>{extData}</extData></request>";
	
	private static final String SECONDREQUESTMODEL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><code>{code}</code><linkId>{linkId}</linkId></request>";
	
	private static final String THENO_1 = "1";
	private static final String THENO_2 = "2";
	
	private static final String TYPE = "sjdyWo";
		
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
			String gameId = map.get("gameId");
			String mobile = map.get("mobile");
			String extData = map.get("extData");
			
			String script = FIRSTREQUESTMODEL.replace("{gameId}", gameId).replace("{mobile}", mobile).replace("{extData}", extData+"");
			
			logger.info("firstDynamic : "+script);
				
			String responseXml = new PostData().PostData(script.getBytes(), url);
				
			xml = parseFirst(responseXml);
			
		}
		
		if(xml == null){
			xml = DynamicUtils.parseError("598");//获取失败
		}
		
		return xml;
	}

	private String parseFirst(String responseXml){
		logger.info("parseFirst : "+responseXml);
		if(responseXml != null && responseXml.length() > 0){
			String state = SingleXmlUtil.getNodeValue(responseXml, "state");
			
			if("0".equals(state)){
				String linkId = SingleXmlUtil.getNodeValue(responseXml, "linkId");
				
				Sets sets = new Sets();
				sets.setKey("linkId");
				sets.setValue(linkId);
				
				return XstreamHelper.toXml(sets).toString();
			}else{
				return DynamicUtils.parseError(state);
			}
		}
		
		return null;
	}

	private String secondDynamic(Map<String,String> map){
		String xml = null;
		String url = map.get("url");
		
		if(url != null && url.length() > 0){
			String code = map.get("code");
			String linkId = map.get("linkId");
			
			String script = SECONDREQUESTMODEL.replace("{code}", code).replace("{linkId}",linkId);
			
			logger.info("secondDynamic : "+script);
			
			String responseXml =  new PostData().PostData(script.getBytes(), url);
		
			xml = parseSecond(map,responseXml);
		}
		
		if(xml == null){
			xml = DynamicUtils.parseError("598");//获取失败
		}
		
		return xml;
	}
		
	private String parseSecond(Map<String,String> map,String responseXml){
		
		if(responseXml != null && responseXml.length() > 0){
			String state = SingleXmlUtil.getNodeValue(responseXml, "state");
			
			if("0".equals(state)){
				Sets sets = new Sets();
				sets.setKey("_succ");
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
		test1();
//		test2();
	}
	
	private static void test1(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("url","http://121.52.208.188:3001/RdoCode");
		map.put("type","sydjWo");
		map.put("theNo","1");
		map.put("gameId","14724");
		map.put("mobile","13811155779");
		map.put("extData","312435241412");
		
		System.out.println(new SjdyWoDynamicService().dynamic(map));
	}
	
	private static void test2(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("url","http://121.52.208.188:3001/WoApi20Pay");
		map.put("type","sydjWo");
		map.put("theNo","2");
		map.put("code","4877");
		map.put("linkId","sw91091726041110003277");
		
		System.out.println(new SjdyWoDynamicService().dynamic(map));
	}
}
