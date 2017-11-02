package com.lxtech.util;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class ToJsonUtil {
	private final static Logger logger = LoggerFactory.getLogger(ToJsonUtil.class);
	/**
	 * 返回json串
	 * @param map
	 * @return
	 */
	public static String toJson(Map<String,String> map){
		Gson gson = new Gson();
		String json = gson.toJson(map,Map.class);
		return json;
	}
	
	public static void main(String args[]){
		Map<String,String> map = new HashMap<String,String>(){
			{
				put("h","1");
				put("a","2");
				put("s","3");
				put("d","4");
			}
		};
		logger.info(toJson(map));
		
	}
}
