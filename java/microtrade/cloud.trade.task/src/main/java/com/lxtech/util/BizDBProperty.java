package com.lxtech.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BizDBProperty {

  private static Properties bizDBProperties;

  static {
    InputStream is = BizDBProperty.class.getResourceAsStream("/config/cloudtrade.properties");
    Properties systemProperties = new Properties();

    try {
    	systemProperties.load(is);
      BizDBProperty.bizDBProperties = systemProperties;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static final String getProperty(String name) {
    return bizDBProperties.getProperty(name);
  }

  
  public static void main(String[] args) {
    System.out.println(BizDBProperty.getProperty("host"));  
  }
}
