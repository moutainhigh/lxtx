package com.lxtech.util;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	public final static Map<Integer, Integer> kTypeMap = new HashMap<Integer, Integer>();
	static {
		kTypeMap.put(5, 2);
		kTypeMap.put(15, 5);
		kTypeMap.put(30, 6);
		kTypeMap.put(60, 7);
	}
}
