package com.lxtx.util.tool;

import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class JsonUtil {
	public static Object convertStringToObject(String jsonSource) {
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new StringReader(jsonSource));
		reader.setLenient(true);
		return gson.fromJson(reader, Object.class);
	}
}
