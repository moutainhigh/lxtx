package com.lxtech.cloud.util;

import java.io.StringReader;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class JsonUtil {
	public static Object convertStringToObject(String jsonSource) {
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new StringReader(jsonSource));
		reader.setLenient(true);
		return gson.fromJson(reader, Object.class);
	}
	
	public static String convertObjToStr(Object src) {
		Gson gson = new Gson();
		return gson.toJson(src);
	}
	
	public static void main(String[] args) {
//		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> map = ImmutableMap.of("key", "value", "key2", 1);
		System.out.println(convertObjToStr(map));
	}
}
