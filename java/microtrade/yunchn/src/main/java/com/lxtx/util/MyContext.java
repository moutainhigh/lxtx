package com.lxtx.util;

import java.io.IOException;
import java.util.Properties;

public class MyContext {

	private static Properties prop = new Properties();
	static {
		try {
			prop.load(MyContext.class.getClassLoader().getResourceAsStream(
					"interface.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据 keyCode 获取properties 文件的值
	 * 
	 * @param keyCode
	 * @return
	 */
	public static String getUrlByKey(String keyCode) {
		return prop.getProperty(keyCode);
	}

}
