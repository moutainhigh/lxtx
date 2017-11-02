package com.lxtech.game.plaza.util;

import java.io.IOException;
import java.util.Properties;

public class CrawlerConfig {
  private Properties properties;
  
  private static CrawlerConfig config = null;
  
  private CrawlerConfig() {
    this.properties = new Properties();
    try {
      properties.load(CrawlerConfig.class.getResourceAsStream("/crawler.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static CrawlerConfig getInstance() {
    if (config == null) {
      config = new CrawlerConfig();
    }
    return config;
  }
  
  public static String get(String property) {
    return CrawlerConfig.getInstance().properties.getProperty(property);
  }

  public static void main(String[] args) {
    String emails = CrawlerConfig.getInstance().get("list.email");
    System.out.println(emails);
  }
  
}
