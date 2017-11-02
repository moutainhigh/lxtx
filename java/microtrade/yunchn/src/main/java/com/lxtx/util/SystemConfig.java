package com.lxtx.util;

import java.io.IOException;
import java.util.Properties;

public class SystemConfig {
  private Properties properties;
  
  private static SystemConfig config = null;
  
  private SystemConfig() {
    this.properties = new Properties();
    try {
      properties.load(SystemConfig.class.getResourceAsStream("/system.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static SystemConfig getInstance() {
    if (config == null) {
      config = new SystemConfig();
    }
    return config;
  }
  
  public String get(String property) {
    return SystemConfig.getInstance().properties.getProperty(property);
  }
  
  public String getSysMenu() {
	    return SystemConfig.getInstance().properties.getProperty("sys_menu_user_profit");
	  }

  
}
