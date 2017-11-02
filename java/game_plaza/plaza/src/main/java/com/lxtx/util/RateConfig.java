package com.lxtx.util;

import java.io.IOException;
import java.util.Properties;

public class RateConfig {
	private Properties properties;

	private static RateConfig config = null;

	private RateConfig() {
		this.properties = new Properties();
		try {
			properties.load(RateConfig.class.getResourceAsStream("/config/rate.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static RateConfig getInstance() {
		if (config == null) {
			config = new RateConfig();
		}
		return config;
	}

	public float getRate1() {
		return Float.parseFloat(RateConfig.getInstance().properties.getProperty("rate1"));
	}

	public float getRate2() {
		return Float.parseFloat(RateConfig.getInstance().properties.getProperty("rate2"));
	}

	public float getRate3() {
		return Float.parseFloat(RateConfig.getInstance().properties.getProperty("rate3"));
	}

}
