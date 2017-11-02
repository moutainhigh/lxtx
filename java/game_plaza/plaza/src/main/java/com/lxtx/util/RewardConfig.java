package com.lxtx.util;

import java.io.IOException;
import java.util.Properties;

public class RewardConfig {
	private Properties properties;

	private static RewardConfig config = null;

	private RewardConfig() {
		this.properties = new Properties();
		try {
			properties.load(RewardConfig.class.getResourceAsStream("/config/reward.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static RewardConfig getInstance() {
		if (config == null) {
			config = new RewardConfig();
		}
		return config;
	}

	public String get(String property) {
		return RewardConfig.getInstance().properties.getProperty(property);
	}

}
