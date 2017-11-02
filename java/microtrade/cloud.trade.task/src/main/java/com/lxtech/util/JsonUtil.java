package com.lxtech.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import net.sf.json.JSONObject;

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

	public static HashMap<String, String> toHashMap(Object object) {
		HashMap<String, String> data = new HashMap<String, String>();
		// 将json字符串转换成jsonObject
		JSONObject jsonObject = JSONObject.fromObject(object);
		Iterator it = jsonObject.keys();
		// 遍历jsonObject数据，添加到Map对象
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			String value = jsonObject.get(key).toString();
			data.put(key, value);
		}
		return data;
	}
}
