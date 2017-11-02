package com.lxtech.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SystemProperty {

  private static Properties systemProperties;

  static {
    InputStream is = SystemProperty.class.getResourceAsStream("/config/system.properties");
    Properties systemProperties = new Properties();

    try {
    	systemProperties.load(is);
      SystemProperty.systemProperties = systemProperties;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static final String getProperty(String name) {
    return systemProperties.getProperty(name);
  }

  
  public static void main(String[] args) {
    System.out.println(SystemProperty.getProperty("temp_msg_flag"));  
  }
}
