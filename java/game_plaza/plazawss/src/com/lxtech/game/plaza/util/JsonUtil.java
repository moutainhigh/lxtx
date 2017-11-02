package com.lxtech.game.plaza.util;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.lxtech.game.plaza.db.model.ChipsetRequest;
import com.lxtech.game.plaza.websocket.WebSocketMessageInboundPool;

public class JsonUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	
	public static Object convertStringToObject(String jsonSource) {
		Gson gson = new GsonBuilder().registerTypeAdapter(Integer.class, new JsonSerializer<Integer>() {
			@Override
			public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
				if (src == src.longValue())
					return new JsonPrimitive(src.longValue());
				return new JsonPrimitive(src);
			}
		}).create();
		JsonReader reader = new JsonReader(new StringReader(jsonSource));
		reader.setLenient(true);
		return gson.fromJson(reader, Object.class);
	}
	
	public static String convertObjToStr(Object src) {
		Gson gson = new Gson();
		return gson.toJson(src);
	}
	
	public static void main(String[] args) {
/*		Map userMap = ImmutableMap.of("key1", 1, "key2", 2);
		
		Map map = ImmutableMap.of("user", JsonUtil.convertObjToStr(userMap));
		System.out.println(JsonUtil.convertObjToStr(map));*/
		
/*		String source = "{\"protocol\":1103,\"lotteryIndex\":1,\"num\":500}";
		Gson gson = new Gson();
		ChipsetRequest req = gson.fromJson(source, ChipsetRequest.class);
		System.out.println(req.getProtocol());
		req.setUserId(1);
		System.out.println(gson.toJson(req));*/
		
//		logger.info("hello");
		String source = "{\"protocol\":1107,\"arrSetChips\":[null,500,1000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]}";
		Map map = (Map) JsonUtil.convertStringToObject(source);
		Object obj = map.get("arrSetChips");
		System.out.println("hello");
	}
}
