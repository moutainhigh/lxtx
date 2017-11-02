package com.lxtx.util;

import java.io.IOException;
import java.util.Properties;

public class DayLimitConfig {
  private Properties properties;
  
  private static DayLimitConfig config = null;
  
  private DayLimitConfig() {
    this.properties = new Properties();
    try {
      properties.load(DayLimitConfig.class.getResourceAsStream("/config/limit.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static DayLimitConfig getInstance() {
    if (config == null) {
      config = new DayLimitConfig();
    }
    return config;
  }
  
  public String get(String property) {
    return DayLimitConfig.getInstance().properties.getProperty(property);
  }

  public static void main(String[] args) {
    String dayTranAmount = DayLimitConfig.getInstance().get("day_tran_amount");
    System.out.println(dayTranAmount);
  }
  
}
