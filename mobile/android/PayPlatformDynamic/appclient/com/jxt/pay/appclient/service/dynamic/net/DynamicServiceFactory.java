package com.jxt.pay.appclient.service.dynamic.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.jxt.pay.appclient.utils.CommonUtil;
import com.jxt.pay.appclient.utils.Constants;

public class DynamicServiceFactory implements InitializingBean{
	
	private static Logger logger = Logger.getLogger(DynamicServiceFactory.class);
	
	public static final String ACTIONURL = "_actionUrl_";

	private Map<String, IDynamicService> map = new HashMap<String, IDynamicService>();
	
	private List<IDynamicService> list = new ArrayList<IDynamicService>();
	
	public String dynamic(String params,HttpServletRequest request){
	
		String result = null; 
		
		try{
			Map<String,String> pMap = new HashMap<String, String>();
			String[] arr = params.split("&");
			
			for(String s : arr){
//				String[] subArr = s.split("=");
//				if(subArr.length == 2){
//					pMap.put(subArr[0], subArr[1]);
//				}
				int pos = s.indexOf("=");
				if(pos > 0){
					pMap.put(s.substring(0,pos), s.substring(pos+1));
				}
			}
			
			pMap.put(Constants.IPPARAM, CommonUtil.getRemortIP(request));
			
			pMap.put(ACTIONURL, "http://"+request.getServerName()+":"+request.getLocalPort()+request.getRequestURI());
			
			String type = pMap.get("type");
			
			if(type != null && type.length() > 0){
				IDynamicService dynamicService = map.get(type);
				
				if(dynamicService != null){
					result = dynamicService.dynamic(pMap);
				}else{
					logger.info("dynamicService is null");
				}
			}else{
				logger.info("type is null");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(result == null){
			result = "<error>597</error>";
		}
		
		return result;
	}
	
	
	
	//IOC
	public void setList(List<IDynamicService> list){
		this.list = list;
	}



	@Override
	public void afterPropertiesSet() throws Exception {
		initMap();
	}
	
	private void initMap(){
		if(list != null && list.size() > 0){
			for(IDynamicService dynamicService : list){
				map.put(dynamicService.getType(), dynamicService);
			}
		}
	}
	
	public static void main(String[] args){
		String s = "url=http://wap.cmread.com/r/602241647/602241682/index.htm?cm=M3490001";
		int pos = s.indexOf("=");
		if(pos > 0){
			System.out.println(s.substring(0,pos)+";"+ s.substring(pos+1));
		}
	}
	
}
