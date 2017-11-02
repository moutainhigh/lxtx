package com.lxtx.util;

import java.io.IOException;
import java.util.Properties;

public class SystemSwitchConfig {
  private Properties properties;
  
  private static SystemSwitchConfig config = null;
  
  private SystemSwitchConfig() {
    this.properties = new Properties();
    try {
      properties.load(SystemSwitchConfig.class.getResourceAsStream("/config/system.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static SystemSwitchConfig getInstance() {
    if (config == null) {
      config = new SystemSwitchConfig();
    }
    return config;
  }
  
  public String get(String property) {
    return SystemSwitchConfig.getInstance().properties.getProperty(property);
  }
  
  public String getSysSwitch() {
	    return SystemSwitchConfig.getInstance().properties.getProperty("sys_switch");
	  }

  
}
