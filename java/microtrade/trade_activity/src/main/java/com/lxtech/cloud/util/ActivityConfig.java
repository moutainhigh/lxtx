package com.lxtech.cloud.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class ActivityConfig {
  private Properties properties;
  
  private String path;
  
  public ActivityConfig(String path) {
	this.path = path;
    this.properties = new Properties();
    try {
      this.properties.load(new InputStreamReader(ActivityConfig.class.getResourceAsStream(this.path), "UTF-8"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public String get(String property) {
    return this.properties.getProperty(property);
  }

  public static void main(String[] args) {
	ActivityConfig config = new ActivityConfig("/crawler.properties");
    String emails = config.get("host");
    System.out.println(emails);
  }
  
}
