package com.jxt.pay.appclient.service.dynamic.net;

import java.util.Map;

import com.jxt.pay.appclient.service.dynamic.pojo.Get;
import com.jxt.pay.appclient.service.dynamic.pojo.Posts;
import com.jxt.pay.appclient.service.dynamic.pojo.Sets;
import com.jxt.pay.appclient.service.dynamic.utils.XstreamHelper;
import com.jxt.pay.appclient.utils.DynamicUtils;

public class DdoDynamicService implements IDynamicService{

	private static final String TYPE = "ddo";
	
	private String dynamicUrl = "";
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String dynamic(Map<String, String> map) {
		
		String theNo = map.get("theNo");
		
		if("1".equals(theNo)){
			return firstDynamic(map);
		}else{
			return secondDynamic(map);
		}		
	}
	
	private String firstDynamic(Map<String,String> map){//初始页
		
		String content = map.get("content");
		
		if(content.contains("用户不存在")){//需要做注册
			String registerUrl = "http://wap.dm.10086.cn"+getLastStr(content, "href=\"", "\">注册");
			
			Get get = new Get();
			get.setUrl(registerUrl);
			get.setBack("registerPage");
			
			
			
		}else if(content.contains("")){//登录成功，跳转到计费页面
			Sets sets = new Sets();
			
			sets.setKey("afterLoginPage");
			sets.setValue(content);
			
			return XstreamHelper.toXml(sets).toString();
		}else{
			return DynamicUtils.parseError("597");
		}
		
		return null;
	}
	
	private String secondDynamic(Map<String,String> map){
		
		return null;
	}
	
	private static String getStr(String src,String prefix,String postfix){
		
		int pos0 = src.indexOf(prefix);
		int pos1 = src.indexOf(postfix,pos0+prefix.length()+1);
		
		if(pos0 >= 0 && pos1 >= 0){
			return src.substring(pos0+prefix.length(),pos1);
		}
		
		return null;
	}
	
	private static String getLastStr(String src,String prefix,String postfix){
		
		int pos1 = src.lastIndexOf(postfix);
		
		if(pos1 >= 0){
			src = src.substring(0,pos1);
			
			int pos0 = src.lastIndexOf(prefix);
			
			if(pos0 >= 0){
				return src.substring(pos0+prefix.length());
			}
		}
		
		return null;
	}
	
	public void setDynamicUrl(String dynamicUrl) {
		this.dynamicUrl = dynamicUrl;
	}

	public static void main(String[] args){
		String ss = "dasdsavar sessionId = 'http://121.31.12.34:8708/da.jsp?da=13124'dafsdfr";
	
		System.out.println(getStr(ss, "var sessionId = '", "'"));
	}
}
